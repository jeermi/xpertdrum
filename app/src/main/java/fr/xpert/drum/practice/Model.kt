package fr.xpert.drum.practice

/**
 * Éléments de la batterie. [staffPos] = position sur la portée en demi-interlignes
 * comptés depuis la ligne du haut (0 = 5e ligne, 8 = 1re ligne, négatif = au-dessus).
 */
enum class Lane(
    val label: String,
    val short: String,
    val staffPos: Int,
    val cross: Boolean,
    val foot: Boolean,
) {
    CRASH("Crash", "CR", -2, true, false),
    RIDE("Ride", "RD", 0, true, false),
    HIHAT("Charleston", "HH", -1, true, false),
    TOM("Tom", "T", 1, false, false),
    SNARE("Caisse claire", "CC", 3, false, false),
    KICK("Grosse caisse", "GC", 7, false, true),
    HHFOOT("Charleston au pied", "HP", 9, true, true),
}

/** Un coup à jouer : [step] est l'indice de pas depuis le début du motif. */
data class Hit(val step: Int, val lane: Lane, val sticking: Char? = null, val accent: Boolean = false)

/**
 * Motif rythmique en boucle.
 * @param stepsPerBeat 1 = noires, 2 = croches, 3 = triolets (ou croches en 6/8), 4 = doubles croches
 */
data class Pattern(
    val beatsPerBar: Int,
    val stepsPerBeat: Int,
    val bars: Int,
    val hits: List<Hit>,
) {
    val stepsPerBar get() = beatsPerBar * stepsPerBeat
    val lanes: List<Lane> get() = hits.map { it.lane }.distinct().sortedBy { it.ordinal }
    val hasAccents: Boolean get() = hits.any { it.accent }

    /** Volume de lecture d'un coup : les accents ressortent nettement quand le motif en contient. */
    fun gainOf(h: Hit): Float = if (!hasAccents) 0.9f else if (h.accent) 1.0f else 0.4f
}

/**
 * Une ligne de motif : « x » = coup, « . » = silence ; [sticking] aligné : D, G ou « . » ;
 * [accents] aligné : « > » = coup accentué.
 */
data class Row(val lane: Lane, val hits: String, val sticking: String? = null, val accents: String? = null)

fun pattern(stepsPerBeat: Int, beatsPerBar: Int = 4, vararg rows: Row): Pattern {
    val stepsPerBar = beatsPerBar * stepsPerBeat
    val len = rows.first().hits.length
    require(len % stepsPerBar == 0) { "Longueur de motif invalide : $len" }
    val hits = ArrayList<Hit>()
    for (r in rows) {
        require(r.hits.length == len) { "Lignes de longueurs différentes" }
        require(r.sticking == null || r.sticking.length == len) { "Doigté mal aligné" }
        require(r.accents == null || r.accents.length == len) { "Accents mal alignés" }
        r.hits.forEachIndexed { i, c ->
            if (c == 'x') {
                val st = r.sticking?.get(i)?.takeIf { it == 'D' || it == 'G' }
                hits.add(Hit(i, r.lane, st, r.accents?.get(i) == '>'))
            }
        }
    }
    hits.sortBy { it.step }
    return Pattern(beatsPerBar, stepsPerBeat, len / stepsPerBar, hits)
}

data class Exercise(
    val id: String,
    val level: Int,
    val title: String,
    val description: String,
    val tip: String,
    val pattern: Pattern,
    val startBpm: Int,
    val targetBpm: Int,
    val rampStep: Int = 4,
    /** Monter le tempo toutes les N mesures (multiple de la longueur du motif). */
    val rampEveryBars: Int = 4,
)

enum class Judgement(val label: String) {
    PERFECT("Pile !"),
    EARLY("En avance"),
    LATE("En retard"),
    MISS("Raté"),
    EXTRA("Coup en trop"),
}
