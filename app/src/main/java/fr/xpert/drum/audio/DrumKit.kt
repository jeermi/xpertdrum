package fr.xpert.drum.audio

import android.content.res.AssetManager
import android.util.Log
import fr.xpert.drum.practice.Lane
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/** Un son pré-calculé (mono, float, -1..1). */
class Sound(val name: String, val data: FloatArray)

/**
 * Sons de la batterie.
 *
 * Les fûts et cymbales sont de vrais enregistrements : le kit « Virtuosity Drums »
 * de Versilian Studios, publié sous licence CC0 (domaine public). Chaque élément
 * a 3 nuances (doux, moyen, fort) et 2 variantes jouées en alternance, pour éviter
 * l'effet « mitraillette ». Voir assets/kit/CREDITS.txt.
 *
 * Si les échantillons ne peuvent pas être lus, l'appli retombe sur les sons
 * synthétisés ci-dessous. Le clic du métronome reste synthétisé.
 */
class DrumKit(private val sampleRate: Int, assets: AssetManager? = null) {
    private val synthKick = render("kick", 0.45) { t, rnd, ph ->
        val f = 48.0 + 95.0 * exp(-t * 32.0)
        ph[0] += 2 * PI * f / sampleRate
        sin(ph[0]) * exp(-t * 7.5) + rnd.nextDouble(-1.0, 1.0) * 0.25 * exp(-t * 320.0)
    }
    private val synthSnare = render("snare", 0.25) { t, rnd, _ ->
        val noise = rnd.nextDouble(-1.0, 1.0) * 0.65 * exp(-t * 20.0)
        val tone = sin(2 * PI * 185.0 * t) * 0.45 * exp(-t * 28.0)
        noise + tone
    }
    private val synthHihat = highPassed(render("hihat", 0.12) { t, rnd, _ ->
        rnd.nextDouble(-1.0, 1.0) * 0.55 * exp(-t * 48.0)
    })
    private val synthTom = render("tom", 0.5) { t, _, ph ->
        val f = 105.0 + 45.0 * exp(-t * 12.0)
        ph[0] += 2 * PI * f / sampleRate
        sin(ph[0]) * 0.85 * exp(-t * 6.5)
    }
    private val synthCrash = highPassed(render("crash", 1.6) { t, rnd, _ ->
        rnd.nextDouble(-1.0, 1.0) * 0.45 * exp(-t * 2.6)
    })
    private val synthRide = highPassed(render("ride", 1.0) { t, rnd, _ ->
        rnd.nextDouble(-1.0, 1.0) * 0.25 * exp(-t * 4.0) + sin(2 * PI * 3200.0 * t) * 0.12 * exp(-t * 3.0)
    })
    private val synthHihatFoot = highPassed(render("hihatFoot", 0.05) { t, rnd, _ ->
        rnd.nextDouble(-1.0, 1.0) * 0.4 * exp(-t * 90.0)
    })
    val click = render("click", 0.04) { t, _, _ -> sin(2 * PI * 1000.0 * t) * 0.55 * exp(-t * 110.0) }
    val clickAccent = render("clickAccent", 0.05) { t, _, _ -> sin(2 * PI * 1600.0 * t) * 0.8 * exp(-t * 90.0) }

    private val synth = mapOf(
        Lane.CRASH to synthCrash,
        Lane.RIDE to synthRide,
        Lane.HHFOOT to synthHihatFoot,
        Lane.HIHAT to synthHihat,
        Lane.TOM to synthTom,
        Lane.SNARE to synthSnare,
        Lane.KICK to synthKick,
    )

    /** Échantillons réels : [lane][nuance 0..2][variante]. Vide si non chargés. */
    private val sampled: Map<Lane, List<List<Sound>>> = assets?.let { loadSamples(it) } ?: emptyMap()
    private val roundRobin = IntArray(Lane.values().size)

    /** Vrai si les vrais enregistrements sont utilisés. */
    val realistic: Boolean get() = sampled.isNotEmpty()

    /**
     * Son à jouer pour un élément, choisi selon la force du coup :
     * [velocity] ≥ 0,95 = accent (fort), ≥ 0,6 = coup normal (moyen), sinon doux.
     */
    fun forLane(lane: Lane, velocity: Float = 0.9f): Sound {
        val layers = sampled[lane] ?: return synth.getValue(lane)
        val level = when {
            velocity >= 0.95f -> 2
            velocity >= 0.6f -> 1
            else -> 0
        }
        val variants = layers[level]
        val i = roundRobin[lane.ordinal]++ % variants.size
        return variants[i]
    }

    /**
     * Gain à appliquer à [forLane] : la nuance est déjà dans l'enregistrement,
     * on ne baisse donc le volume que pour les sons synthétisés.
     */
    fun gainFor(velocity: Float): Float = if (realistic) 1f else velocity

    private fun loadSamples(am: AssetManager): Map<Lane, List<List<Sound>>> {
        val files = mapOf(
            Lane.KICK to "kick", Lane.SNARE to "snare", Lane.HIHAT to "hihat",
            Lane.HHFOOT to "hhfoot", Lane.TOM to "tom", Lane.CRASH to "crash", Lane.RIDE to "ride",
        )
        return try {
            files.mapValues { (_, name) ->
                listOf("soft", "mid", "loud").map { level ->
                    (1..2).map { rr ->
                        am.open("kit/${name}_${level}_$rr.wav").use { readWav("${name}_${level}_$rr", it) }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("DrumKit", "Échantillons indisponibles, sons synthétisés utilisés", e)
            emptyMap()
        }
    }

    /** Lit un WAV PCM 16 bits mono et le convertit à la fréquence de sortie. */
    private fun readWav(name: String, input: InputStream): Sound {
        val bytes = input.readBytes()
        val bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        require(bytes.size > 12 && String(bytes, 0, 4) == "RIFF" && String(bytes, 8, 4) == "WAVE") { "$name : pas un WAV" }
        var pos = 12
        var rate = 0
        var channels = 0
        var bits = 0
        var dataPos = -1
        var dataLen = 0
        while (pos + 8 <= bytes.size) {
            val id = String(bytes, pos, 4)
            val len = bb.getInt(pos + 4)
            if (id == "fmt ") {
                channels = bb.getShort(pos + 10).toInt()
                rate = bb.getInt(pos + 12)
                bits = bb.getShort(pos + 22).toInt()
            } else if (id == "data") {
                dataPos = pos + 8
                dataLen = minOf(len, bytes.size - dataPos)
                break
            }
            pos += 8 + len + (len and 1)
        }
        require(dataPos > 0 && bits == 16 && channels >= 1 && rate > 0) { "$name : format non pris en charge" }
        val frames = dataLen / (2 * channels)
        val src = FloatArray(frames)
        for (i in 0 until frames) {
            var sum = 0f
            for (c in 0 until channels) sum += bb.getShort(dataPos + (i * channels + c) * 2)
            src[i] = sum / channels / 32768f
        }
        return Sound(name, resample(src, rate, sampleRate))
    }

    /** Rééchantillonnage linéaire (suffisant pour passer de 48 à 44,1 kHz). */
    private fun resample(src: FloatArray, from: Int, to: Int): FloatArray {
        if (from == to || src.isEmpty()) return src
        val n = (src.size.toLong() * to / from).toInt()
        val out = FloatArray(n)
        val step = from.toDouble() / to
        for (i in 0 until n) {
            val x = i * step
            val j = x.toInt()
            val f = (x - j).toFloat()
            val a = src[minOf(j, src.size - 1)]
            val b = src[minOf(j + 1, src.size - 1)]
            out[i] = a + (b - a) * f
        }
        return out
    }

    private fun render(name: String, seconds: Double, gen: (Double, Random, DoubleArray) -> Double): Sound {
        val n = (seconds * sampleRate).toInt()
        val out = FloatArray(n)
        val rnd = Random(name.hashCode())
        val phase = DoubleArray(1)
        val fade = minOf(n / 10, sampleRate / 200)
        for (i in 0 until n) {
            var v = gen(i.toDouble() / sampleRate, rnd, phase)
            if (i > n - fade) v *= (n - i).toDouble() / fade
            out[i] = v.toFloat()
        }
        return Sound(name, out)
    }

    /** Filtre passe-haut simple (différence du premier ordre) pour les cymbales. */
    private fun highPassed(s: Sound): Sound {
        val d = s.data
        var prev = 0f
        for (i in d.indices) {
            val x = d[i]
            d[i] = (x - prev) * 0.8f
            prev = x
        }
        return s
    }
}
