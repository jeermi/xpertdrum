package fr.xpert.drum.practice

import fr.xpert.drum.audio.AudioEngine
import fr.xpert.drum.audio.Scheduler
import fr.xpert.drum.audio.Sound
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs

class NoteEvent(val frame: Long, val lane: Lane, val sticking: Char?, val accent: Boolean = false) {
    @Volatile var result: Judgement? = null
    @Volatile var deltaMs: Int = 0
}

class BarMark(val frame: Long, val countIn: Boolean, val bpm: Int)
class BeatMark(val frame: Long, val number: Int, val countIn: Boolean)

class Feedback(val judgement: Judgement, val deltaMs: Int, val atNanos: Long, val counted: Boolean = true)

class Stats {
    var perfect = 0; var early = 0; var late = 0; var miss = 0; var extra = 0
    private var sumDelta = 0L
    private var nDelta = 0

    val judged get() = perfect + early + late + miss

    fun record(j: Judgement, deltaMs: Int) {
        when (j) {
            Judgement.PERFECT -> perfect++
            Judgement.EARLY -> early++
            Judgement.LATE -> late++
            Judgement.MISS -> miss++
            Judgement.EXTRA -> extra++
        }
        if (j == Judgement.PERFECT || j == Judgement.EARLY || j == Judgement.LATE) {
            sumDelta += deltaMs; nDelta++
        }
    }

    /** Précision en % : pile = 1, avance/retard = 0,5, raté = 0. */
    val accuracy: Int
        get() = if (judged == 0) 0 else ((perfect + 0.5 * (early + late)) * 100.0 / judged).toInt()

    /** Écart moyen en ms (négatif = tendance à être en avance). */
    val meanDeltaMs: Int get() = if (nDelta == 0) 0 else (sumDelta / nDelta).toInt()
}

/**
 * Une séance d'exercice : décompte d'une mesure, puis le motif en boucle avec
 * métronome et tempo qui monte. Les coups attendus sont publiés dans [notes]
 * (avec 3 s d'avance) pour l'affichage défilant et le jugement des pads.
 */
class PracticeSession(
    private val engine: AudioEngine,
    val exercise: Exercise,
    startBpm: Int,
    private val rampEnabled: Boolean,
    /** Mode « écouter » : l'appli joue le motif elle-même, sans jugement. */
    val demo: Boolean,
    private val metronome: Boolean,
    private val calibrationMs: Int,
    /** Jugement par position rythmique (micro) plutôt que par élément (pads). */
    val byPosition: Boolean = false,
    private val countInBars: Int = 1,
) : Scheduler {
    private val sr = engine.sampleRate
    private val pattern = exercise.pattern
    private val kit = engine.kit
    private val aheadFrames = (sr * 3.0).toLong()

    private var bpm = startBpm
    private var nextBarFrame = -1.0
    private var barIndex = 0

    val notes = CopyOnWriteArrayList<NoteEvent>()
    val barMarks = CopyOnWriteArrayList<BarMark>()
    val beatMarks = CopyOnWriteArrayList<BeatMark>()
    val stats = Stats()
    var maxBpmPlayed = startBpm
        private set
    val startedAtMillis = System.currentTimeMillis()

    override fun fill(writeFrame: Long, untilFrame: Long, sink: (Long, Sound, Float) -> Unit) {
        if (nextBarFrame < 0) {
            nextBarFrame = (writeFrame + sr / 4).toDouble()
            barIndex = -countInBars
        }
        val target = maxOf(untilFrame, writeFrame + aheadFrames)
        while (nextBarFrame < target) generateBar(sink)
    }

    private fun generateBar(sink: (Long, Sound, Float) -> Unit) {
        val framesPerBeat = sr * 60.0 / bpm
        val stepFrames = framesPerBeat / pattern.stepsPerBeat
        val barStart = nextBarFrame
        val countIn = barIndex < 0
        barMarks.add(BarMark(barStart.toLong(), countIn, bpm))
        for (b in 0 until pattern.beatsPerBar) {
            val f = (barStart + b * framesPerBeat).toLong()
            if (metronome || countIn) {
                sink(f, if (b == 0) kit.clickAccent else kit.click, if (countIn) 0.9f else 0.55f)
            }
            beatMarks.add(BeatMark(f, b + 1, countIn))
        }
        if (!countIn) {
            val pBar = barIndex % pattern.bars
            for (h in pattern.hits) {
                if (h.step / pattern.stepsPerBar != pBar) continue
                val f = (barStart + (h.step % pattern.stepsPerBar) * stepFrames).toLong()
                notes.add(NoteEvent(f, h.lane, h.sticking, h.accent))
                if (demo) {
                    val v = pattern.gainOf(h)
                    sink(f, kit.forLane(h.lane, v), kit.gainFor(v))
                }
            }
        }
        nextBarFrame += pattern.beatsPerBar * framesPerBeat
        barIndex++
        if (rampEnabled && barIndex > 0 && barIndex % exercise.rampEveryBars == 0 && bpm < exercise.targetBpm) {
            bpm = minOf(bpm + exercise.rampStep, exercise.targetBpm)
        }
    }

    fun expectedNanos(n: NoteEvent): Long = engine.frameToNanos(n.frame)

    fun frameNanos(frame: Long): Long = engine.frameToNanos(frame)

    /** Tempo de la mesure en train de sonner. */
    fun currentBpm(nowNanos: Long): Int {
        var b = barMarks.firstOrNull()?.bpm ?: bpm
        for (m in barMarks) {
            if (engine.frameToNanos(m.frame) <= nowNanos) b = m.bpm else break
        }
        return b
    }

    /** Numéro du temps du décompte en cours, ou null si le décompte est fini. */
    fun countInBeat(nowNanos: Long): Int? {
        var current: BeatMark? = null
        for (m in beatMarks) {
            if (engine.frameToNanos(m.frame) <= nowNanos) current = m else break
        }
        return if (current == null) 0 else if (current.countIn) current.number else null
    }

    /**
     * Un coup entendu au micro à [onsetNanos]. On ne sait pas quel élément a été frappé :
     * le coup valide la position rythmique attendue la plus proche (toutes les notes qui
     * tombent à cet instant, par exemple charleston + grosse caisse ensemble).
     */
    fun onOnset(onsetNanos: Long): Feedback {
        // Rebond, flam ou deux éléments frappés quasi ensemble : un seul coup.
        if (onsetNanos - lastMatchedOnset < 70_000_000L) return Feedback(Judgement.EXTRA, 0, onsetNanos, counted = false)
        val adjusted = onsetNanos - calibrationMs * 1_000_000L
        var best: NoteEvent? = null
        var bestDelta = Long.MAX_VALUE
        for (n in notes) {
            if (n.result != null) continue
            val d = adjusted - expectedNanos(n)
            if (abs(d) < abs(bestDelta)) {
                best = n; bestDelta = d
            }
        }
        if (best == null || abs(bestDelta) > WINDOW_NANOS) {
            stats.record(Judgement.EXTRA, 0)
            return Feedback(Judgement.EXTRA, 0, onsetNanos)
        }
        val ms = (bestDelta / 1_000_000L).toInt()
        val j = when {
            abs(ms) <= PERFECT_MS -> Judgement.PERFECT
            ms < 0 -> Judgement.EARLY
            else -> Judgement.LATE
        }
        for (n in notes) {
            if (n.frame == best.frame && n.result == null) {
                n.deltaMs = ms; n.result = j
            }
        }
        lastMatchedOnset = onsetNanos
        stats.record(j, ms)
        return Feedback(j, ms, onsetNanos)
    }

    private var lastMatchedOnset = 0L

    /** À appeler à chaque image : marque les coups ratés et fait le ménage. */
    fun tick(nowNanos: Long) {
        val adjustedNow = nowNanos - calibrationMs * 1_000_000L
        for (n in notes) {
            val t = expectedNanos(n)
            if (!demo && n.result == null && adjustedNow - t > WINDOW_NANOS) {
                if (byPosition) {
                    for (o in notes) if (o.frame == n.frame) o.result = Judgement.MISS
                } else {
                    n.result = Judgement.MISS
                }
                stats.record(Judgement.MISS, 0)
            }
            if (nowNanos - t > 3_000_000_000L) notes.remove(n)
        }
        if (!demo) {
            val playing = currentBpm(nowNanos)
            if (countInBeat(nowNanos) == null && playing > maxBpmPlayed) maxBpmPlayed = playing
        }
        barMarks.removeAll { nowNanos - engine.frameToNanos(it.frame) > 8_000_000_000L }
        beatMarks.removeAll { nowNanos - engine.frameToNanos(it.frame) > 8_000_000_000L }
    }

    companion object {
        const val PERFECT_MS = 35
        const val WINDOW_NANOS = 160_000_000L
    }
}
