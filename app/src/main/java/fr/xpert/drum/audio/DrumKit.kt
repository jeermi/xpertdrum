package fr.xpert.drum.audio

import fr.xpert.drum.practice.Lane
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/** Un son pré-calculé (mono, float, -1..1). */
class Sound(val name: String, val data: FloatArray)

/**
 * Sons de batterie synthétisés au démarrage : aucun échantillon externe,
 * donc aucun souci de droits et un APK léger.
 */
class DrumKit(private val sampleRate: Int) {
    val kick = render("kick", 0.45) { t, rnd, ph ->
        val f = 48.0 + 95.0 * exp(-t * 32.0)
        ph[0] += 2 * PI * f / sampleRate
        sin(ph[0]) * exp(-t * 7.5) + rnd.nextDouble(-1.0, 1.0) * 0.25 * exp(-t * 320.0)
    }
    val snare = render("snare", 0.25) { t, rnd, _ ->
        val noise = rnd.nextDouble(-1.0, 1.0) * 0.65 * exp(-t * 20.0)
        val tone = sin(2 * PI * 185.0 * t) * 0.45 * exp(-t * 28.0)
        noise + tone
    }
    val hihat = highPassed(render("hihat", 0.12) { t, rnd, _ ->
        rnd.nextDouble(-1.0, 1.0) * 0.55 * exp(-t * 48.0)
    })
    val tom = render("tom", 0.5) { t, _, ph ->
        val f = 105.0 + 45.0 * exp(-t * 12.0)
        ph[0] += 2 * PI * f / sampleRate
        sin(ph[0]) * 0.85 * exp(-t * 6.5)
    }
    val crash = highPassed(render("crash", 1.6) { t, rnd, _ ->
        rnd.nextDouble(-1.0, 1.0) * 0.45 * exp(-t * 2.6)
    })
    val click = render("click", 0.04) { t, _, _ -> sin(2 * PI * 1000.0 * t) * 0.55 * exp(-t * 110.0) }
    val clickAccent = render("clickAccent", 0.05) { t, _, _ -> sin(2 * PI * 1600.0 * t) * 0.8 * exp(-t * 90.0) }

    fun forLane(lane: Lane): Sound = when (lane) {
        Lane.CRASH -> crash
        Lane.HIHAT -> hihat
        Lane.TOM -> tom
        Lane.SNARE -> snare
        Lane.KICK -> kick
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
