package fr.xpert.drum.practice

import fr.xpert.drum.practice.Lane.HHFOOT
import fr.xpert.drum.practice.Lane.HIHAT
import fr.xpert.drum.practice.Lane.KICK
import fr.xpert.drum.practice.Lane.RIDE
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
        // ---------------- Niveau 5 : indépendance des quatre membres
        Exercise(
            id = "indep-mains-gc",
            level = 5,
            title = "Mains alternées + grosse caisse",
            description = "Croches alternées D G à la caisse claire, et la grosse caisse sur chaque temps.",
            tip = "La grosse caisse tombe toujours avec la main droite. Joue d'abord les mains seules, puis ajoute le pied sans changer les mains.",
            pattern = pattern(
                2, 4,
                Row(SNARE, "xxxxxxxx", "DGDGDGDG"),
                Row(KICK, "x.x.x.x."),
            ),
            startBpm = 55, targetBpm = 95,
        ),
        Exercise(
            id = "indep-deux-pieds",
            level = 5,
            title = "Les deux pieds",
            description = "Grosse caisse (pied droit) sur 1 et 3, charleston au pied gauche sur 2 et 4.",
            tip = "Pied gauche : appuie sur la pédale du charleston pour faire « tchik », puis relâche. Les deux pieds se répondent comme une marche.",
            pattern = pattern(
                1, 4,
                Row(KICK, "x.x."),
                Row(HHFOOT, ".x.x"),
            ),
            startBpm = 60, targetBpm = 110,
        ),
        Exercise(
            id = "indep-pieds-mains",
            level = 5,
            title = "Pieds qui marchent, mains en noires",
            description = "Les deux pieds alternés comme avant, et les mains jouent D G D G sur chaque temps.",
            tip = "Main droite avec la grosse caisse, main gauche avec le charleston au pied : chaque côté du corps joue ensemble.",
            pattern = pattern(
                1, 4,
                Row(SNARE, "xxxx", "DGDG"),
                Row(KICK, "x.x."),
                Row(HHFOOT, ".x.x"),
            ),
            startBpm = 55, targetBpm = 100,
        ),
        Exercise(
            id = "indep-deux-mains",
            level = 5,
            title = "Deux mains, deux rythmes",
            description = "Main droite en noires sur la ride, main gauche en croches à la caisse claire.",
            tip = "Sur les temps les deux mains frappent ensemble, sur les « et » seule la gauche joue. Compte « 1 et 2 et » à voix haute.",
            pattern = pattern(
                2, 4,
                Row(RIDE, "x.x.x.x.", "D.D.D.D."),
                Row(SNARE, "xxxxxxxx", "GGGGGGGG"),
            ),
            startBpm = 50, targetBpm = 90,
        ),
        Exercise(
            id = "indep-gauche-contretemps",
            level = 5,
            title = "Main gauche à contretemps",
            description = "Ride en noires (main droite), caisse claire sur tous les « et » (main gauche), grosse caisse sur 1 et 3.",
            tip = "La main gauche ne joue jamais avec la droite : elle tombe exactement entre deux coups de ride. Très lentement au début !",
            pattern = pattern(
                2, 4,
                Row(RIDE, "x.x.x.x.", "D.D.D.D."),
                Row(SNARE, ".x.x.x.x", ".G.G.G.G"),
                Row(KICK, "x...x..."),
            ),
            startBpm = 50, targetBpm = 90,
        ),
        Exercise(
            id = "indep-gc-bouge",
            level = 5,
            title = "Grosse caisse qui bouge",
            description = "Le rythme rock aux mains, et la grosse caisse sur 1, le « et » du 2 et le « et » du 3.",
            tip = "Les mains restent un tapis régulier (l'« ostinato »). Seul le pied change : écoute-le se glisser entre les temps.",
            pattern = pattern(
                2, 4,
                Row(HIHAT, "xxxxxxxx", "DDDDDDDD"),
                Row(SNARE, "..x...x.", "..G...G."),
                Row(KICK, "x..x.x.."),
            ),
            startBpm = 50, targetBpm = 95,
        ),
        Exercise(
            id = "indep-quatre-membres",
            level = 5,
            title = "Les quatre membres",
            description = "Ride en croches, caisse claire sur 2 et 4, grosse caisse sur 1 et 3, charleston au pied sur 2 et 4.",
            tip = "Le pied gauche joue en même temps que la caisse claire. Ajoute les membres un par un : ride, puis grosse caisse, puis caisse claire, puis pied gauche.",
            pattern = pattern(
                2, 4,
                Row(RIDE, "xxxxxxxx", "DDDDDDDD"),
                Row(SNARE, "..x...x.", "..G...G."),
                Row(KICK, "x...x..."),
                Row(HHFOOT, "..x...x."),
            ),
            startBpm = 50, targetBpm = 95,
        ),
    )

    fun byId(id: String): Exercise? = all.firstOrNull { it.id == id }

    fun levelName(level: Int) = when (level) {
        1 -> "Niveau 1 · Les mains et le pied"
        2 -> "Niveau 2 · Coordination"
        3 -> "Niveau 3 · Premiers rythmes"
        4 -> "Niveau 4 · Breaks"
        else -> "Niveau 5 · Indépendance des membres"
    }
}
