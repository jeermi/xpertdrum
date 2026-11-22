package fr.xpert.drum.content

sealed interface Block {
    data class Text(val text: String) : Block
    data class Bullets(val title: String?, val items: List<String>) : Block
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
                Block.Bullets(
                    "Le siège",
                    listOf(
                        "Règle la hauteur pour que tes cuisses soient à l'horizontale ou très légèrement inclinées vers le bas : genoux un peu plus bas que les hanches.",
                        "Assieds-toi sur la moitié avant du siège, pas tout au fond.",
                        "Pieds posés sur les pédales, tes genoux forment un angle d'environ 90 à 110°.",
                    ),
                ),
                Block.Bullets(
                    "Le haut du corps",
                    listOf(
                        "Dos droit mais pas raide, comme si un fil te tirait doucement le haut de la tête.",
                        "Épaules basses et relâchées. Si elles montent vers les oreilles, c'est que tu te crispes.",
                        "Coudes près du corps, sans les coller ni les écarter en « ailes de poulet ».",
                        "Avant-bras presque parallèles au sol quand les baguettes sont sur la caisse claire.",
                    ),
                ),
                Block.Bullets(
                    "Placer les éléments (pour un droitier)",
                    listOf(
                        "Caisse claire entre les genoux, à peu près à hauteur de la ceinture, à plat ou très légèrement inclinée vers toi.",
                        "Grosse caisse devant toi, pédale sous le pied droit.",
                        "Charleston à gauche, pédale sous le pied gauche, cymbales 10 à 15 cm au-dessus de la caisse claire.",
                        "Tout doit être atteignable sans te pencher ni tendre complètement le bras.",
                    ),
                ),
                Block.Tip(
                    "Gaucher ? Tu peux tout inverser (batterie « en miroir »), ou jouer en droitier : beaucoup de gauchers le font. " +
                        "Choisis tôt et garde ton choix."
                ),
                Block.Tip(
                    "Pas de batterie sous la main ? Un pad d'entraînement, un coussin ferme ou un annuaire font très bien l'affaire " +
                        "pour les mains, et les pads de l'appli pour travailler le placement."
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
                    "Pour débuter, on utilise la prise « matched grip » : les deux mains tiennent la baguette de la même façon. " +
                        "Prends des baguettes 5A (le standard) ou 7A (plus fines si tu as de petites mains)."
                ),
                Block.Bullets(
                    "Trouver le point d'équilibre",
                    listOf(
                        "Pince la baguette entre le pouce et l'index, à environ un tiers de sa longueur en partant du bout arrière.",
                        "Laisse-la rebondir sur la caisse claire ou le pad.",
                        "Fais glisser ta pince de quelques millimètres jusqu'à trouver l'endroit où elle rebondit le plus longtemps : c'est ton point d'appui (le « pivot »).",
                    ),
                ),
                Block.Bullets(
                    "Fermer la main",
                    listOf(
                        "Le pouce est posé à plat sur le côté de la baguette, face à l'index.",
                        "L'index s'enroule autour de la baguette, sans pointer dessus.",
                        "Majeur, annulaire et auriculaire se referment souplement : ils guident, ils ne serrent pas.",
                        "Le bout arrière dépasse légèrement de la paume.",
                    ),
                ),
                Block.Bullets(
                    "Les trois prises",
                    listOf(
                        "Allemande : paumes vers le sol. Beaucoup de poignet, puissant.",
                        "Américaine : mains tournées d'environ 45°. Polyvalente : c'est la meilleure pour commencer.",
                        "Française : pouces vers le haut. On joue davantage avec les doigts, plus fin.",
                    ),
                ),
                Block.Warning(
                    "Erreur n°1 du débutant : serrer trop fort. Une main crispée fatigue vite, tue le rebond et peut faire mal " +
                        "au poignet. Tiens la baguette comme un oiseau : assez pour qu'il ne s'envole pas, pas au point de l'étouffer."
                ),
            ),
        ),
        Lesson(
            id = "geste",
            icon = "💪",
            title = "Le geste : frapper et rebondir",
            summary = "Laisser revenir la baguette, la hauteur et le volume, où frapper.",
            blocks = listOf(
                Block.Text(
                    "Le bon geste ressemble à un dribble au basket : tu lances la baguette vers la peau et tu la laisses " +
                        "remonter toute seule. On ne « pousse » pas la baguette dans la peau."
                ),
                Block.Bullets(
                    "Le coup de base",
                    listOf(
                        "Pars d'une hauteur d'environ 20 cm, baguette presque verticale à la main.",
                        "Le mouvement vient d'abord du poignet ; l'avant-bras accompagne.",
                        "Frappe, puis laisse la baguette remonter à sa hauteur de départ : prête pour le coup suivant.",
                        "Vise le centre de la caisse claire pour un son plein et régulier.",
                    ),
                ),
                Block.Bullets(
                    "Hauteur = volume",
                    listOf(
                        "Baguette haute : coup fort. Baguette basse : coup doux.",
                        "Pour un son égal, les deux mains partent exactement de la même hauteur.",
                    ),
                ),
                Block.Bullets(
                    "Les pieds",
                    listOf(
                        "Grosse caisse talon posé : c'est la cheville qui bouge. Plus de contrôle, idéal pour débuter.",
                        "Talon levé : la jambe participe. Plus de puissance, on y viendra plus tard.",
                        "Relâche la pédale après chaque coup pour que la mailloche ne reste pas collée à la peau.",
                    ),
                ),
                Block.Tip(
                    "Travaille devant un miroir ou filme-toi : tu verras tout de suite si une main monte plus haut que l'autre."
                ),
            ),
        ),
        Lesson(
            id = "kit",
            icon = "🥁",
            title = "Les éléments de la batterie",
            summary = "Grosse caisse, caisse claire, charleston, toms et cymbales.",
            blocks = listOf(
                Block.Bullets(
                    null,
                    listOf(
                        "Grosse caisse (GC) : le gros tambour au sol, joué au pied droit avec une pédale. Le « boum », la base grave.",
                        "Caisse claire (CC) : le tambour entre tes genoux, avec un timbre métallique dessous. Le « tchak » des temps 2 et 4 en rock.",
                        "Charleston (HH) : deux cymbales l'une sur l'autre, ouvertes ou fermées avec le pied gauche. Il marque souvent les croches.",
                        "Toms : les tambours au-dessus de la grosse caisse (aigus) et au sol (basse). Surtout utilisés dans les breaks.",
                        "Crash : cymbale qui « explose », pour marquer un début ou une fin de phrase.",
                        "Ride : grande cymbale pour jouer un motif régulier à la place du charleston.",
                    ),
                ),
                Block.Tip(
                    "Dans l'appli, les pads reprennent ces abréviations : HH, CC, GC, T et CR."
                ),
            ),
        ),
        Lesson(
            id = "rythme",
            icon = "🔢",
            title = "Compter et lire le rythme",
            summary = "Temps, mesures, noires, croches, doubles croches et doigtés.",
            blocks = listOf(
                Block.Text(
                    "La plupart des morceaux sont en 4/4 : 4 temps par mesure. Le métronome donne les temps, et le premier " +
                        "temps de chaque mesure est accentué (clic plus aigu)."
                ),
                Block.Bullets(
                    "Les valeurs de notes",
                    listOf(
                        "Noire : un coup par temps. On compte « 1, 2, 3, 4 ».",
                        "Croche : deux coups par temps. On compte « 1 et 2 et 3 et 4 et ».",
                        "Double croche : quatre coups par temps. On compte « 1 e et a 2 e et a… ».",
                    ),
                ),
                Block.Bullets(
                    "Lire une partition de batterie",
                    listOf(
                        "Chaque élément a sa place sur la portée, pas sa hauteur de note.",
                        "Grosse caisse : dans l'interligne du bas.",
                        "Caisse claire : dans le 3e interligne en partant du bas.",
                        "Charleston : une croix au-dessus de la portée. Crash : une croix sur une ligne supplémentaire.",
                        "Sous les notes, D = main droite et G = main gauche.",
                    ),
                ),
                Block.Tip(
                    "Compte à voix haute pendant les exercices, surtout au début. Ça paraît bête, mais c'est ce qui ancre le tempo."
                ),
            ),
        ),
        Lesson(
            id = "travailler",
            icon = "📈",
            title = "Bien travailler",
            summary = "Routine, métronome, tempo qui monte, pauses et protection auditive.",
            blocks = listOf(
                Block.Bullets(
                    "Une séance type de 20 minutes",
                    listOf(
                        "3 min d'échauffement : noires puis croches alternées, très lentement.",
                        "10 min sur l'exercice du jour avec le tempo qui monte.",
                        "5 min de rythme ou de break pour le plaisir.",
                        "2 min de retour au calme : relâche les épaules, secoue les mains.",
                    ),
                ),
                Block.Bullets(
                    "Les règles d'or",
                    listOf(
                        "Lentement d'abord : si tu n'arrives pas à le jouer lentement, tu ne pourras pas le jouer vite.",
                        "Monte le tempo seulement quand tu es à 80 % de précision ou plus.",
                        "Mieux vaut 15 minutes chaque jour qu'une heure et demie le dimanche.",
                        "Une douleur n'est jamais normale : arrête, détends-toi et vérifie ta tenue.",
                    ),
                ),
                Block.Warning(
                    "Une vraie batterie dépasse facilement 100 dB. Porte des bouchons d'oreilles (des modèles pour musiciens " +
                        "existent) dès que tu joues sur une batterie acoustique : les dégâts sur l'audition sont définitifs."
                ),
                Block.Tip(
                    "Avant ta première séance, lance la calibration (Réglages) : elle mesure le délai de ton téléphone pour que " +
                        "le verdict « pile / en avance / en retard » soit juste."
                ),
            ),
        ),
    )

    fun byId(id: String) = all.firstOrNull { it.id == id }
}
