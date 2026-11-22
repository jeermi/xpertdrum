package fr.xpert.drum.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class SessionRecord(
    val exerciseId: String,
    val title: String,
    val atMillis: Long,
    val durationSec: Int,
    val accuracy: Int,
    val bpm: Int,
    val meanDeltaMs: Int,
)

data class BestScore(val accuracy: Int, val bpm: Int, val sessions: Int)

/** Progression et réglages, stockés localement (rien ne quitte le téléphone). */
class ProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("xpert_drum", Context.MODE_PRIVATE)

    /** Décalage moyen mesuré entre le clic entendu et le doigt sur l'écran. */
    var calibrationMs: Int
        get() = prefs.getInt("calibration_ms", 0)
        set(v) = prefs.edit().putInt("calibration_ms", v).apply()

    val isCalibrated: Boolean get() = prefs.contains("calibration_ms")

    /** true = on juge au micro (vraie batterie), false = pads à l'écran. */
    var useMic: Boolean
        get() = prefs.getBoolean("use_mic", true)
        set(v) = prefs.edit().putBoolean("use_mic", v).apply()

    /** Seuil de détection des coups au micro (échelle de OnsetDetector). */
    var micThreshold: Float
        get() = prefs.getFloat("mic_threshold", 0.03f)
        set(v) = prefs.edit().putFloat("mic_threshold", v).apply()

    /** Décalage mesuré entre le son joué et sa détection au micro (chaîne audio du téléphone). */
    var micOffsetMs: Int
        get() = prefs.getInt("mic_offset_ms", 0)
        set(v) = prefs.edit().putInt("mic_offset_ms", v).apply()

    val isMicCalibrated: Boolean get() = prefs.contains("mic_threshold")

    var metronomeBpm: Int
        get() = prefs.getInt("metronome_bpm", 80)
        set(v) = prefs.edit().putInt("metronome_bpm", v).apply()

    fun markLessonRead(id: String) {
        val set = prefs.getStringSet("lessons_read", emptySet())!!.toMutableSet()
        if (set.add(id)) prefs.edit().putStringSet("lessons_read", set).apply()
    }

    fun lessonsRead(): Set<String> = prefs.getStringSet("lessons_read", emptySet())!!.toSet()

    fun addPracticeSeconds(sec: Int) {
        if (sec <= 0) return
        prefs.edit().putInt("practice_sec", totalPracticeSeconds() + sec).apply()
    }

    fun totalPracticeSeconds(): Int = prefs.getInt("practice_sec", 0)

    fun record(r: SessionRecord) {
        val arr = JSONArray(prefs.getString("history", "[]"))
        arr.put(JSONObject().apply {
            put("id", r.exerciseId); put("title", r.title); put("at", r.atMillis)
            put("dur", r.durationSec); put("acc", r.accuracy); put("bpm", r.bpm); put("delta", r.meanDeltaMs)
        })
        val trimmed = JSONArray()
        val from = maxOf(0, arr.length() - 300)
        for (i in from until arr.length()) trimmed.put(arr.get(i))
        prefs.edit().putString("history", trimmed.toString()).apply()
        addPracticeSeconds(r.durationSec)
    }

    fun history(): List<SessionRecord> {
        val arr = JSONArray(prefs.getString("history", "[]"))
        return (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            SessionRecord(
                o.getString("id"), o.getString("title"), o.getLong("at"), o.getInt("dur"),
                o.getInt("acc"), o.getInt("bpm"), o.optInt("delta", 0),
            )
        }
    }

    /** Meilleur résultat par exercice : meilleure précision, et tempo le plus haut tenu à 80 % ou plus. */
    fun bests(): Map<String, BestScore> = history().groupBy { it.exerciseId }.mapValues { (_, list) ->
        BestScore(
            accuracy = list.maxOf { it.accuracy },
            bpm = list.filter { it.accuracy >= 80 }.maxOfOrNull { it.bpm } ?: 0,
            sessions = list.size,
        )
    }
}
