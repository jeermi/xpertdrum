package fr.xpert.drum.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTimestamp
import android.media.AudioTrack
import android.os.Process
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Fournit des sons à jouer à une position précise (en frames audio).
 * Appelé depuis le thread audio : doit rester rapide et ne jamais bloquer.
 *
 * @param writeFrame première frame du tampon en cours d'écriture
 * @param untilFrame planifier au moins jusqu'à cette frame
 * @param sink (frame, son, gain)
 */
fun interface Scheduler {
    fun fill(writeFrame: Long, untilFrame: Long, sink: (Long, Sound, Float) -> Unit)
}

/**
 * Moteur audio basse latence basé sur AudioTrack (mode PERFORMANCE_MODE_LOW_LATENCY).
 * L'horloge de référence est la position audio (frames), convertie en temps
 * système grâce à AudioTrack.getTimestamp() : c'est ce qui permet de juger
 * un coup « en avance », « en retard » ou « pile ».
 */
class AudioEngine(context: Context) {
    val sampleRate: Int
    private val burst: Int
    val kit: DrumKit

    @Volatile var scheduler: Scheduler? = null
    @Volatile var writtenFrames = 0L
        private set

    private val immediate = ConcurrentLinkedQueue<Pair<Sound, Float>>()
    private var track: AudioTrack? = null
    private var thread: Thread? = null
    @Volatile private var running = false
    private class TsPoint(val frame: Long, val nanos: Long)
    @Volatile private var tsPoint: TsPoint? = null
    @Volatile private var startNanos = 0L
    @Volatile private var latencyFrames = 0

    init {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        sampleRate = am.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)?.toIntOrNull() ?: 48000
        burst = (am.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)?.toIntOrNull() ?: 256)
            .coerceIn(64, 1024)
        kit = DrumKit(sampleRate, context.assets)
    }

    @Synchronized
    fun start() {
        if (running) return
        val minBytes = AudioTrack.getMinBufferSize(
            sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_FLOAT
        )
        val t = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(maxOf(minBytes, burst * 4 * 2))
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
            .build()
        latencyFrames = t.bufferSizeInFrames
        writtenFrames = 0L
        tsPoint = null
        startNanos = System.nanoTime()
        track = t
        running = true
        t.play()
        thread = Thread({ renderLoop(t) }, "xpert-audio").also { it.start() }
    }

    @Synchronized
    fun stop() {
        if (!running) return
        running = false
        thread?.join(500)
        thread = null
        track?.let {
            try {
                it.pause(); it.flush(); it.release()
            } catch (_: Exception) {
            }
        }
        track = null
        scheduler = null
    }

    /** Joue un son le plus tôt possible (pads). */
    fun playNow(sound: Sound, gain: Float = 1f) {
        immediate.add(sound to gain)
    }

    /** Instant (System.nanoTime) où la frame [frame] sort du haut-parleur. */
    fun frameToNanos(frame: Long): Long {
        val p = tsPoint
        return if (p != null) {
            p.nanos + (frame - p.frame) * 1_000_000_000L / sampleRate
        } else {
            startNanos + (frame + latencyFrames) * 1_000_000_000L / sampleRate
        }
    }

    /** Frame audible à l'instant [nanos] (inverse de [frameToNanos]). */
    fun nanosToFrame(nanos: Long): Long {
        val p = tsPoint
        return if (p != null) {
            p.frame + (nanos - p.nanos) * sampleRate / 1_000_000_000L
        } else {
            (nanos - startNanos) * sampleRate / 1_000_000_000L - latencyFrames
        }
    }

    private class Pending(val frame: Long, val sound: Sound, val gain: Float)

    private class Voice(val sound: Sound, val gain: Float, var offset: Int) {
        var pos = 0

        /** Mixe dans le tampon ; renvoie true quand le son est terminé. */
        fun mixInto(buf: FloatArray): Boolean {
            var i = offset
            offset = 0
            val d = sound.data
            while (i < buf.size && pos < d.size) {
                buf[i] += d[pos] * gain
                i++; pos++
            }
            return pos >= d.size
        }
    }

    private fun renderLoop(t: AudioTrack) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
        val buf = FloatArray(burst)
        val voices = ArrayList<Voice>()
        val pending = ArrayList<Pending>()
        val ts = AudioTimestamp()
        val sink: (Long, Sound, Float) -> Unit = { f, s, g -> pending.add(Pending(f, s, g)) }
        var lastScheduler: Scheduler? = null
        var lastTsCheck = -sampleRate.toLong()
        val minAhead = sampleRate / 10L

        while (running) {
            val start = writtenFrames
            val end = start + burst

            val sch = scheduler
            if (sch !== lastScheduler) {
                pending.clear()
                lastScheduler = sch
            }
            sch?.fill(start, end + minAhead, sink)

            while (true) {
                val p = immediate.poll() ?: break
                voices.add(Voice(p.first, p.second, 0))
            }
            val it = pending.iterator()
            while (it.hasNext()) {
                val p = it.next()
                if (p.frame < end) {
                    voices.add(Voice(p.sound, p.gain, (p.frame - start).coerceAtLeast(0L).toInt()))
                    it.remove()
                }
            }

            java.util.Arrays.fill(buf, 0f)
            val vi = voices.iterator()
            while (vi.hasNext()) {
                if (vi.next().mixInto(buf)) vi.remove()
            }
            for (i in buf.indices) {
                val x = buf[i] * 0.8f
                // Limiteur doux : au-delà de 0,8 on arrondit au lieu d'écrêter net.
                val a = kotlin.math.abs(x)
                buf[i] = if (a <= 0.8f) x else kotlin.math.sign(x) * (0.8f + 0.2f * kotlin.math.tanh((a - 0.8f) / 0.2f))
            }

            val n = t.write(buf, 0, burst, AudioTrack.WRITE_BLOCKING)
            if (n < 0) break
            writtenFrames = end

            if (end - lastTsCheck > sampleRate / 4) {
                lastTsCheck = end
                if (t.getTimestamp(ts)) {
                    tsPoint = TsPoint(ts.framePosition, ts.nanoTime)
                }
            }
        }
    }
}
