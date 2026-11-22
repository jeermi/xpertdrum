package fr.xpert.drum.practice

import fr.xpert.drum.audio.AudioEngine
import fr.xpert.drum.audio.Scheduler
import fr.xpert.drum.audio.Sound

/** Joue une fois un court motif (exemple d'une leçon), avec ou sans clic, puis se tait. */
class RhythmPlayer(
    private val engine: AudioEngine,
    private val pattern: Pattern,
    private val bpm: Int,
    private val repeats: Int,
    private val click: Boolean,
) : Scheduler {
    private var scheduled = false

    /** Durée de l'exemple en millisecondes. */
    val durationMs: Long
        get() = (pattern.beatsPerBar * pattern.bars * repeats * 60_000L / bpm) + 300L

    override fun fill(writeFrame: Long, untilFrame: Long, sink: (Long, Sound, Float) -> Unit) {
        if (scheduled) return
        scheduled = true
        val sr = engine.sampleRate
        val start = writeFrame + sr / 10
        val framesPerBeat = sr * 60.0 / bpm
        val stepFrames = framesPerBeat / pattern.stepsPerBeat
        val totalBeats = pattern.beatsPerBar * pattern.bars * repeats
        if (click) {
            for (b in 0 until totalBeats) {
                val accent = b % pattern.beatsPerBar == 0
                sink(
                    start + (b * framesPerBeat).toLong(),
                    if (accent) engine.kit.clickAccent else engine.kit.click,
                    if (accent) 0.6f else 0.4f,
                )
            }
        }
        val stepsTotal = pattern.bars * pattern.stepsPerBar
        for (r in 0 until repeats) {
            for (h in pattern.hits) {
                sink(start + ((r * stepsTotal + h.step) * stepFrames).toLong(), engine.kit.forLane(h.lane), pattern.gainOf(h))
            }
        }
    }
}
