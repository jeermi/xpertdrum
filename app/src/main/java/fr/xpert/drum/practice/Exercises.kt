package fr.xpert.drum.practice

import fr.xpert.drum.practice.Lane.HIHAT
import fr.xpert.drum.practice.Lane.KICK
import fr.xpert.drum.practice.Lane.SNARE

/** Parcours grand débutant : une main, puis l'alternance, le pied, la coordination, le premier rythme. */
object Exercises {
    val all: List<Exercise> = listOf(
        Exercise(
            id = "noires-cc",
            level = 1,
            title = "Noires à la caisse claire",
            description = "Un coup sur chaque temps, en alternant main droite (D) et main gauche (G).",
            tip = "Compte à voix haute « 1, 2, 3, 4 ». Les deux baguettes doivent partir de la même hauteur et sonner pareil.",
            pattern = pattern(1, 4, Row(SNARE, "xxxx", "DGDG")),
            startBpm = 60, targetBpm = 100,
        ),
        Exercise(
            id = "croches-cc",
            level = 1,
            title = "Croches alternées",
            description = "Deux coups par temps, toujours en alternant D G D G : c'est le « single stroke ».",
            tip = "Compte « 1 et 2 et 3 et 4 et ». Laisse rebondir la baguette, ne l'écrase pas sur la peau.",
            pattern = pattern(2, 4, Row(SNARE, "xxxxxxxx", "DGDGDGDG")),
            startBpm = 60, targetBpm = 100,
        ),
        Exercise(
            id = "noires-gc",
            level = 1,
            title = "Grosse caisse sur les temps",
            description = "Le pied droit seul, un coup sur chaque temps.",
            tip = "Commence talon posé : c'est la cheville qui travaille. Relâche la pédale après chaque coup, ne laisse pas la mailloche collée à la peau.",
            pattern = pattern(1, 4, Row(KICK, "xxxx")),
            startBpm = 60, targetBpm = 100,
        ),
        Exercise(
            id = "croches-hh",
            level = 2,
            title = "Charleston en croches",
            description = "La main droite joue des croches régulières sur le charleston fermé.",
            tip = "Pied gauche appuyé sur la pédale du charleston pour le garder fermé. Vise un son égal sur chaque croche.",
            pattern = pattern(2, 4, Row(HIHAT, "xxxxxxxx", "DDDDDDDD")),
            startBpm = 60, targetBpm = 110,
        ),
        Exercise(
            id = "coord-hh-gc",
            level = 2,
            title = "Charleston + grosse caisse",
            description = "Croches au charleston, grosse caisse sur les temps 1 et 3.",
            tip = "La grosse caisse tombe exactement en même temps que la croche du charleston. Commence très lentement.",
            pattern = pattern(
                2, 4,
                Row(HIHAT, "xxxxxxxx", "DDDDDDDD"),
                Row(KICK, "x...x..."),
            ),
            startBpm = 55, targetBpm = 100,
        ),
        Exercise(
            id = "doubles",
            level = 2,
            title = "Coups doubles",
            description = "Deux coups par main : D D G G. C'est le « double stroke ».",
            tip = "Le 2e coup de chaque main doit être aussi fort que le 1er. À tempo lent, fais deux vrais coups de poignet.",
            pattern = pattern(2, 4, Row(SNARE, "xxxxxxxx", "DDGGDDGG")),
            startBpm = 50, targetBpm = 90,
        ),
        Exercise(
            id = "rock-1",
            level = 3,
            title = "Mon premier rythme rock",
            description = "Charleston en croches, grosse caisse sur 1 et 3, caisse claire sur 2 et 4.",
            tip = "Construis-le par étapes : charleston seul, puis + grosse caisse, puis + caisse claire. La caisse claire se joue main gauche.",
            pattern = pattern(
                2, 4,
                Row(HIHAT, "xxxxxxxx", "DDDDDDDD"),
                Row(SNARE, "..x...x.", "..G...G."),
                Row(KICK, "x...x..."),
            ),
            startBpm = 55, targetBpm = 110,
        ),
        Exercise(
            id = "rock-2",
            level = 3,
            title = "Rock : variante de grosse caisse",
            description = "Le même rythme avec une grosse caisse en plus sur le « et » du 3.",
            tip = "Le coup ajouté tombe avec la croche du charleston juste après le temps 3. Garde la main droite parfaitement régulière.",
            pattern = pattern(
                2, 4,
                Row(HIHAT, "xxxxxxxx", "DDDDDDDD"),
                Row(SNARE, "..x...x.", "..G...G."),
                Row(KICK, "x...xx.."),
            ),
            startBpm = 55, targetBpm = 105,
        ),
        Exercise(
            id = "paradiddle",
            level = 3,
            title = "Paradiddle simple",
            description = "D G D D  G D G G en doubles croches : le rudiment le plus utile après le single et le double stroke.",
            tip = "Compte « 1 e et a ». Le premier coup de chaque groupe de quatre est le temps.",
            pattern = pattern(4, 4, Row(SNARE, "xxxxxxxxxxxxxxxx", "DGDDGDGGDGDDGDGG")),
            startBpm = 45, targetBpm = 80,
        ),
        Exercise(
            id = "fill-1",
            level = 4,
            title = "Premier break",
            description = "Une mesure de rock, puis une mesure qui finit par 4 doubles croches à la caisse claire.",
            tip = "Pendant le break, la main droite quitte le charleston. Reviens au rythme sur le 1 suivant sans ralentir.",
            pattern = pattern(
                4, 4,
                Row(HIHAT,
                    "x.x.x.x.x.x.x.x." + "x.x.x.x.x.x.....",
                    "D.D.D.D.D.D.D.D." + "D.D.D.D.D.D....."),
                Row(SNARE,
                    "....x.......x..." + "....x.......xxxx",
                    "....G.......G..." + "....G.......DGDG"),
                Row(KICK,
                    "x.......x......." + "x.......x......."),
            ),
            startBpm = 55, targetBpm = 100, rampEveryBars = 4,
        ),
    )

    fun byId(id: String): Exercise? = all.firstOrNull { it.id == id }

    fun levelName(level: Int) = when (level) {
        1 -> "Niveau 1 · Les mains et le pied"
        2 -> "Niveau 2 · Coordination"
        3 -> "Niveau 3 · Premiers rythmes"
        else -> "Niveau 4 · Breaks"
    }
}
