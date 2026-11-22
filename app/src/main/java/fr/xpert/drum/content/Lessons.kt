package fr.xpert.drum.content

import fr.xpert.drum.practice.Lane
import fr.xpert.drum.practice.Pattern
import fr.xpert.drum.practice.Row
import fr.xpert.drum.practice.pattern

/** Un rythme à écouter depuis une leçon (joué [bars] fois, avec ou sans clic). */
data class Rhythm(val pattern: Pattern, val bpm: Int, val bars: Int = 2, val click: Boolean = true)

sealed interface Block {
    data class Text(val text: String) : Block
    data class Section(val title: String) : Block

    /** Un conseil illustré, avec éventuellement un son ou un rythme à écouter. */
    data class Advice(
        val text: String,
        val illus: Illus,
        val title: String? = null,
        val sound: Lane? = null,
        val rhythm: Rhythm? = null,
    ) : Block

    data class Tip(val text: String) : Block
    data class Warning(val text: String) : Block
}

data class Lesson(
    val id: String,
    val icon: String,
    val title: String,
    val summary: String,
    val blocks: List<Block>,
)

/** Les bases du grand débutant : à lire avant (et entre) les exercices. */
object Lessons {
    val all = listOf(
        Lesson(
            id = "posture",
            icon = "🪑",
            title = "S'installer et se tenir",
            summary = "Hauteur du siège, place des pieds, position du dos et des bras.",
            blocks = listOf(
                Block.Text(
                    "Une bonne posture évite les douleurs et rend le jeu plus facile. Prends deux minutes pour " +
                        "régler ton installation avant chaque séance : c'est le même réflexe que régler son siège en voiture."
                ),
                Block.Section("Le siège"),
                Block.Advice(
                    "Règle la hauteur pour que tes cuisses soient à l'horizontale ou très légèrement inclinées vers le bas : genoux un peu plus bas que les hanches.",
                    Illus.SEAT_HEIGHT, "La bonne hauteur",
                ),
                Block.Advice(
                    "Assieds-toi sur la moitié avant du siège, pas tout au fond : tes jambes sont libres de bouger.",
                    Illus.SEAT_FRONT, "Sur l'avant du siège",
                ),
                Block.Advice(
                    "Pieds posés sur les pédales, tes genoux forment un angle d'environ 90 à 110°.",
                    Illus.KNEE_ANGLE, "L'angle des genoux",
                ),
                Block.Section("Le haut du corps"),
                Block.Advice(
                    "Dos droit mais pas raide, comme si un fil te tirait doucement le haut de la tête.",
                    Illus.BACK_STRAIGHT, "Le dos",
                ),
                Block.Advice(
                    "Épaules basses et relâchées. Si elles montent vers les oreilles, c'est que tu te crispes : souffle et relâche.",
                    Illus.SHOULDERS_RELAXED, "Les épaules",
                ),
                Block.Advice(
                    "Coudes près du corps, sans les coller ni les écarter en « ailes de poulet ».",
                    Illus.ELBOWS, "Les coudes",
                ),
                Block.Advice(
                    "Avant-bras presque parallèles au sol quand les baguettes sont posées sur la caisse claire.",
                    Illus.FOREARMS, "Les avant-bras",
                ),
                Block.Section("Placer les éléments (pour un droitier)"),
                Block.Advice(
                    "Caisse claire entre les genoux, à peu près à hauteur de la ceinture, à plat ou très légèrement inclinée vers toi.",
                    Illus.SNARE_PLACEMENT, "La caisse claire",
                ),
                Block.Advice(
                    "Grosse caisse droit devant toi, pédale sous le pied droit.",
                    Illus.KICK_PLACEMENT, "La grosse caisse",
                ),
                Block.Advice(
                    "Charleston à gauche, pédale sous le pied gauche, cymbales 10 à 15 cm au-dessus de la caisse claire.",
                    Illus.HIHAT_PLACEMENT, "Le charleston",
                ),
                Block.Advice(
                    "Tout doit être atteignable sans te pencher ni tendre complètement le bras.",
                    Illus.REACH, "Tout à portée",
                ),
                Block.Advice(
                    "Gaucher ? Tu peux tout inverser (batterie « en miroir ») ou jouer en droitier : beaucoup de gauchers le font. Choisis tôt et garde ton choix.",
                    Illus.LEFTY, "Et si je suis gaucher ?",
                ),
                Block.Advice(
                    "Pas de batterie sous la main ? Un pad d'entraînement fait très bien l'affaire : le micro de l'appli l'entend aussi. En dépannage, tu peux jouer sur les pads de l'écran.",
                    Illus.PRACTICE_PAD, "S'entraîner sans batterie",
                ),
            ),
        ),
        Lesson(
            id = "baguettes",
            icon = "🥢",
            title = "Tenir les baguettes",
            summary = "Le point d'équilibre, la pince pouce-index et les trois prises.",
            blocks = listOf(
                Block.Text(
                    "Pour débuter, on utilise la prise « matched grip » : les deux mains tiennent la baguette de la même façon."
                ),
                Block.Advice(
                    "Prends des baguettes 5A, le standard. Les 7A, plus fines, conviennent bien aux petites mains.",
                    Illus.STICK_SIZES, "Choisir ses baguettes",
                ),
                Block.Section("Trouver le point d'équilibre"),
                Block.Advice(
                    "Le point d'appui se situe à environ un tiers de la longueur, en partant du bout arrière (le « talon »).",
                    Illus.STICK_THIRD, "Le tiers arrière",
                ),
                Block.Advice(
                    "Pince la baguette entre le pouce et l'index à cet endroit, et laisse-la rebondir sur la caisse claire ou le pad.",
                    Illus.STICK_BOUNCE, "Le test du rebond",
                ),
                Block.Advice(
                    "Fais glisser ta pince de quelques millimètres jusqu'à trouver l'endroit où elle rebondit le plus longtemps : c'est ton pivot.",
                    Illus.FULCRUM_SLIDE, "Ajuster le pivot",
                ),
                Block.Section("Fermer la main"),
                Block.Advice(
                    "Le pouce est posé à plat sur le côté de la baguette, face à l'index. L'index s'enroule autour, sans pointer dessus.",
                    Illus.THUMB_INDEX, "Pouce et index",
                ),
                Block.Advice(
                    "Majeur, annulaire et auriculaire se referment souplement : ils guident la baguette, ils ne la serrent pas.",
                    Illus.FINGERS_WRAP, "Les autres doigts",
                ),
                Block.Advice(
                    "Le bout arrière de la baguette dépasse légèrement de la paume.",
                    Illus.BUTT_OUT, "Le talon dépasse",
                ),
                Block.Section("Les trois prises"),
                Block.Advice(
                    "Allemande : paumes vers le sol, beaucoup de poignet, puissant. Américaine : mains tournées d'environ 45°, polyvalente, c'est la meilleure pour commencer. Française : pouces vers le haut, on joue davantage avec les doigts.",
                    Illus.GRIPS_THREE, "Allemande, américaine, française",
                ),
                Block.Advice(
                    "Erreur n°1 du débutant : serrer trop fort. Une main crispée fatigue vite, tue le rebond et peut faire mal au poignet. Tiens la baguette comme un oiseau : assez pour qu'il ne s'envole pas, pas au point de l'étouffer.",
                    Illus.TOO_TIGHT, "Ne pas serrer",
                ),
            ),
        ),
        Lesson(
            id = "geste",
            icon = "💪",
            title = "Le geste : frapper et rebondir",
            summary = "Laisser revenir la baguette, la hauteur et le volume, où frapper, les pieds.",
            blocks = listOf(
                Block.Advice(
                    "Le bon geste ressemble à un dribble au basket : tu lances la baguette vers la peau et tu la laisses remonter toute seule. On ne « pousse » pas la baguette dans la peau.",
                    Illus.DRIBBLE, "Comme un dribble",
                ),
                Block.Section("Le coup de base"),
                Block.Advice(
                    "Pars d'une hauteur d'environ 20 cm entre l'olive et la peau.",
                    Illus.START_HEIGHT, "La hauteur de départ",
                ),
                Block.Advice(
                    "Le mouvement vient d'abord du poignet ; l'avant-bras accompagne.",
                    Illus.WRIST_MOTION, "Le poignet",
                ),
                Block.Advice(
                    "Frappe, puis laisse la baguette remonter à sa hauteur de départ : elle est prête pour le coup suivant.",
                    Illus.REBOUND, "Frapper et remonter",
                ),
                Block.Advice(
                    "Vise le centre de la caisse claire pour un son plein et régulier.",
                    Illus.CENTER_HIT, "Où frapper",
                    sound = Lane.SNARE,
                ),
                Block.Section("Hauteur et volume"),
                Block.Advice(
                    "Baguette haute : coup fort. Baguette basse : coup doux.",
                    Illus.HEIGHT_VOLUME, "Hauteur = volume",
                ),
                Block.Advice(
                    "Pour un son égal, les deux mains partent exactement de la même hauteur.",
                    Illus.SAME_HEIGHT, "Deux mains égales",
                ),
                Block.Section("Les pieds"),
                Block.Advice(
                    "Grosse caisse talon posé : c'est la cheville qui bouge. Plus de contrôle, idéal pour débuter.",
                    Illus.HEEL_DOWN, "Talon posé",
                    sound = Lane.KICK,
                ),
                Block.Advice(
                    "Talon levé : la jambe participe. Plus de puissance, on y viendra plus tard.",
                    Illus.HEEL_UP, "Talon levé",
                ),
                Block.Advice(
                    "Relâche la pédale après chaque coup pour que la mailloche ne reste pas collée à la peau : le son reste rond.",
                    Illus.PEDAL_RELEASE, "Relâcher",
                ),
                Block.Advice(
                    "Travaille devant un miroir ou filme-toi : tu verras tout de suite si une main monte plus haut que l'autre.",
                    Illus.MIRROR, "Se regarder jouer",
                ),
            ),
        ),
        Lesson(
            id = "kit",
            icon = "🥁",
            title = "Les éléments de la batterie",
            summary = "Grosse caisse, caisse claire, charleston, toms et cymbales, avec leur son.",
            blocks = listOf(
                Block.Advice(
                    "Vue de dessus d'une batterie de droitier. Touche « Écouter » sous chaque élément pour entendre son son.",
                    Illus.KIT_FULL, "La batterie",
                ),
                Block.Advice(
                    "Le gros tambour au sol, joué au pied droit avec une pédale. C'est le « boum », la base grave du rythme.",
                    Illus.KIT_KICK, "Grosse caisse (GC)",
                    sound = Lane.KICK,
                ),
                Block.Advice(
                    "Le tambour entre tes genoux, avec un timbre métallique tendu dessous. C'est le « tchak » des temps 2 et 4 en rock.",
                    Illus.KIT_SNARE, "Caisse claire (CC)",
                    sound = Lane.SNARE,
                ),
                Block.Advice(
                    "Deux cymbales l'une sur l'autre, jouées à la baguette. Fermé, il fait un « tsit » court : il marque souvent les croches.",
                    Illus.KIT_HIHAT, "Charleston (HH)",
                    sound = Lane.HIHAT,
                ),
                Block.Advice(
                    "Le pied gauche appuie sur la pédale : les cymbales se referment avec un « tchik ». On s'en sert pour marquer les temps 2 et 4.",
                    Illus.KIT_HIHAT_FOOT, "Charleston au pied (HP)",
                    sound = Lane.HHFOOT,
                ),
                Block.Advice(
                    "Les tambours montés sur la grosse caisse (aigus) et le tom basse au sol (grave). Surtout utilisés dans les breaks.",
                    Illus.KIT_TOMS, "Toms (T)",
                    sound = Lane.TOM,
                ),
                Block.Advice(
                    "La cymbale qui « explose », pour marquer un début ou une fin de phrase.",
                    Illus.KIT_CRASH, "Crash (CR)",
                    sound = Lane.CRASH,
                ),
                Block.Advice(
                    "Grande cymbale à droite, au son plus « ping », pour jouer un motif régulier à la place du charleston.",
                    Illus.KIT_RIDE, "Ride (RD)",
                    sound = Lane.RIDE,
                ),
            ),
        ),
        Lesson(
            id = "rythme",
            icon = "🔢",
            title = "Compter et lire le rythme",
            summary = "Temps, mesures, noires, croches, doubles croches et doigtés, à écouter.",
            blocks = listOf(
                Block.Advice(
                    "La plupart des morceaux sont en 4/4 : 4 temps par mesure. Le métronome donne les temps, et le premier temps de chaque mesure est accentué (clic plus aigu).",
                    Illus.TIME_SIGNATURE, "La mesure à 4 temps",
                    rhythm = Rhythm(pattern(1, 4, Row(Lane.SNARE, "....")), 80, bars = 2),
                ),
                Block.Section("Les valeurs de notes"),
                Block.Advice(
                    "Noire : un coup par temps. On compte « 1, 2, 3, 4 ».",
                    Illus.QUARTERS, "La noire",
                    rhythm = Rhythm(pattern(1, 4, Row(Lane.SNARE, "xxxx")), 80),
                ),
                Block.Advice(
                    "Croche : deux coups par temps. On compte « 1 et 2 et 3 et 4 et ».",
                    Illus.EIGHTHS, "La croche",
                    rhythm = Rhythm(pattern(2, 4, Row(Lane.SNARE, "xxxxxxxx")), 80),
                ),
                Block.Advice(
                    "Double croche : quatre coups par temps. On compte « 1 e et a 2 e et a… ».",
                    Illus.SIXTEENTHS, "La double croche",
                    rhythm = Rhythm(pattern(4, 4, Row(Lane.SNARE, "xxxxxxxxxxxxxxxx")), 70),
                ),
                Block.Section("Lire une partition de batterie"),
                Block.Advice(
                    "Chaque élément a sa place sur la portée. Grosse caisse dans l'interligne du bas, caisse claire dans le 3e interligne, charleston en croix au-dessus de la portée, crash en croix sur une ligne supplémentaire, ride en croix sur la ligne du haut, charleston au pied en croix sous la portée.",
                    Illus.STAFF_MAP, "Où est quoi ?",
                    rhythm = Rhythm(
                        pattern(
                            1, 4,
                            Row(Lane.CRASH, "x......."),
                            Row(Lane.RIDE, ".x......"),
                            Row(Lane.HIHAT, "..x....."),
                            Row(Lane.TOM, "...x...."),
                            Row(Lane.SNARE, "....x..."),
                            Row(Lane.KICK, ".....x.."),
                            Row(Lane.HHFOOT, "......x."),
                        ),
                        70, bars = 1, click = false,
                    ),
                ),
                Block.Advice(
                    "Sous les notes, D = main droite et G = main gauche. Ici : D G D G puis D D G G.",
                    Illus.STICKING, "Les doigtés",
                    rhythm = Rhythm(pattern(2, 4, Row(Lane.SNARE, "xxxxxxxx", "DGDGDDGG")), 70),
                ),
                Block.Tip(
                    "Compte à voix haute pendant les exercices, surtout au début. Ça paraît bête, mais c'est ce qui ancre le tempo."
                ),
            ),
        ),
        Lesson(
            id = "independance",
            icon = "🧩",
            title = "L'indépendance des membres",
            summary = "Faire jouer bras gauche, bras droit, pied gauche et pied droit chacun leur partie.",
            blocks = listOf(
                Block.Text(
                    "L'indépendance, c'est la capacité de jouer quatre rythmes différents en même temps : un pour chaque " +
                        "membre. Personne ne naît avec : elle se construit lentement, un membre à la fois."
                ),
                Block.Advice(
                    "Commence par le membre qui joue la partie la plus régulière (souvent la main droite) : c'est le « tapis ». Ajoute un seul membre à la fois, et ne passe au suivant que quand le précédent est automatique. Si tout s'écroule, enlève le dernier ajouté.",
                    Illus.LAYERS, "Un membre à la fois",
                    rhythm = Rhythm(
                        pattern(
                            2, 4,
                            Row(Lane.RIDE, "xxxxxxxx"),
                            Row(Lane.SNARE, "..x...x."),
                            Row(Lane.KICK, "x...x..."),
                            Row(Lane.HHFOOT, "..x...x."),
                        ),
                        70,
                    ),
                ),
                Block.Advice(
                    "Joue nettement plus lentement que tu ne le crois nécessaire : 50 à 60 BPM, c'est très bien. Dis le rythme à voix haute (« 1 et 2 et… ») pour aider ton cerveau à séparer les parties.",
                    Illus.TEMPO_RAMP, "Lentement",
                ),
                Block.Advice(
                    "Le pied gauche actionne la pédale du charleston : talon posé, la pointe appuie puis relâche, comme quand on tape du pied en écoutant de la musique. Au début, il joue souvent sur 2 et 4, avec la caisse claire.",
                    Illus.KIT_HIHAT_FOOT, "Le pied gauche",
                    sound = Lane.HHFOOT,
                ),
                Block.Tip(
                    "Les exercices du niveau 5 suivent cet ordre : mains + pied droit, les deux pieds, pieds + mains, deux rythmes de mains, puis les quatre membres."
                ),
            ),
        ),
        Lesson(
            id = "travailler",
            icon = "📈",
            title = "Bien travailler",
            summary = "Routine, métronome, tempo qui monte, pauses et protection auditive.",
            blocks = listOf(
                Block.Advice(
                    "3 min d'échauffement (noires puis croches alternées, très lentement), 10 min sur l'exercice du jour avec le tempo qui monte, 5 min de rythme ou de break pour le plaisir, 2 min de retour au calme.",
                    Illus.ROUTINE, "Une séance type",
                ),
                Block.Advice(
                    "Lentement d'abord : si tu n'arrives pas à le jouer lentement, tu ne pourras pas le jouer vite. Monte le tempo seulement quand tu es à 80 % de précision ou plus.",
                    Illus.TEMPO_RAMP, "Le tempo qui monte",
                ),
                Block.Advice(
                    "Mieux vaut 15 minutes chaque jour qu'une heure et demie le dimanche. Et une douleur n'est jamais normale : arrête, détends-toi et vérifie ta tenue.",
                    Illus.DAILY, "Un peu chaque jour",
                ),
                Block.Advice(
                    "Une vraie batterie dépasse facilement 100 dB. Porte des bouchons d'oreilles (des modèles pour musiciens existent) dès que tu joues sur une batterie acoustique : les dégâts sur l'audition sont définitifs.",
                    Illus.EARPLUGS, "Protège tes oreilles",
                ),
                Block.Tip(
                    "Avant ta première séance, fais le « Réglage du micro » depuis l'accueil : l'appli apprend à entendre ta " +
                        "batterie et mesure le délai de ton téléphone, pour que le verdict « pile / en avance / en retard » soit juste."
                ),
            ),
        ),
    )

    fun byId(id: String) = all.firstOrNull { it.id == id }
}
