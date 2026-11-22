package fr.xpert.drum.practice

import fr.xpert.drum.practice.Lane.HHFOOT
import fr.xpert.drum.practice.Lane.HIHAT
import fr.xpert.drum.practice.Lane.KICK
import fr.xpert.drum.practice.Lane.RIDE
import fr.xpert.drum.practice.Lane.SNARE
import fr.xpert.drum.practice.Lane.TOM

/** Doigté alterné strict : D sur les positions paires, G sur les impaires (mains « en l'air » sur les silences). */
private fun alt(hits: String) =
    hits.mapIndexed { i, c -> if (c == 'x') (if (i % 2 == 0) 'D' else 'G') else '.' }.joinToString("")

/** Même doigté pour tous les coups d'une ligne. */
private fun same(hits: String, hand: Char) = hits.map { if (it == 'x') hand else '.' }.joinToString("")

/**
 * Principe de la « lecture appliquée » : les mains jouent des croches alternées continues,
 * les notes du rythme lu sont accentuées, la grosse caisse marque chaque temps.
 */
private fun applied(accents: String, kickEvery: Int = 2): Array<Row> {
    val hands = "x".repeat(accents.length)
    val kick = accents.indices.joinToString("") { if (it % kickEvery == 0) "x" else "." }
    return arrayOf(Row(SNARE, hands, alt(hands), accents), Row(KICK, kick))
}

/** Parcours : une main, l'alternance, le pied, la coordination, les premiers rythmes, puis une progression de lecture (accents, doubles croches, point, triolets, 6/8) inspirée de la méthode Agostini. */
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
        // ---------------- Niveau 6 : lecture appliquée (progression inspirée de la méthode Agostini)
        Exercise(
            id = "app-noires",
            level = 6,
            title = "Lecture appliquée : les noires",
            description = "Les mains jouent des croches alternées sans s'arrêter. Accentue (>) chaque temps : c'est la noire que tu « lis ». Grosse caisse sur chaque temps.",
            tip = "Accent = baguette qui part de haut, coup normal = baguette qui part de 3 cm. Le pied ne doit pas devenir plus fort avec les accents.",
            pattern = pattern(2, 4, *applied(">.>.>.>.")),
            startBpm = 55, targetBpm = 100,
        ),
        Exercise(
            id = "app-blanches",
            level = 6,
            title = "Lecture appliquée : blanches et noires",
            description = "Mesure 1 : deux blanches (accents sur 1 et 3). Mesure 2 : quatre noires. Les mains continuent les croches.",
            tip = "Pendant une blanche, les mains ne s'arrêtent pas : elles jouent doucement. Seul l'accent te dit où commence la note.",
            pattern = pattern(2, 4, *applied(">...>..." + ">.>.>.>.")),
            startBpm = 55, targetBpm = 100,
        ),
        Exercise(
            id = "app-croches",
            level = 6,
            title = "Lecture appliquée : noires et croches",
            description = "Le rythme mélange noires et croches ; chaque note lue est accentuée sur les croches continues.",
            tip = "Lis le rythme à voix haute (« ta, ti-ti ») avant de jouer. Deux accents qui se suivent = D puis G accentués.",
            pattern = pattern(2, 4, *applied(">.>>>.>>" + ">>>>>.>.")),
            startBpm = 50, targetBpm = 95,
        ),
        Exercise(
            id = "app-soupirs",
            level = 6,
            title = "Lecture appliquée : les soupirs",
            description = "Le rythme contient des silences (soupirs) : à ces endroits, aucun accent, les mains jouent doucement.",
            tip = "Le silence se compte aussi fort que les notes. Si tu hésites, compte « 1 et 2 et » et pose les accents dessus.",
            pattern = pattern(2, 4, *applied(">.>...>." + "..>.>.>.")),
            startBpm = 50, targetBpm = 95,
        ),
        // ---------------- Niveau 7 : doubles croches et silences
        Exercise(
            id = "dc-alternees",
            level = 7,
            title = "Doubles croches alternées",
            description = "Quatre coups par temps en D G D G, accent sur chaque temps, grosse caisse sur les temps.",
            tip = "Compte « 1 e et a ». Le coup accentué est toujours la main droite : les trois autres restent bas.",
            pattern = pattern(
                4, 4,
                Row(SNARE, "x".repeat(16), alt("x".repeat(16)), ">...>...>...>..."),
                Row(KICK, "x...x...x...x..."),
            ),
            startBpm = 45, targetBpm = 85,
        ),
        Exercise(
            id = "dc-galop",
            level = 7,
            title = "Croche + deux doubles",
            description = "Le « galop » : une croche puis deux doubles croches sur chaque temps. Doigté D . D G (la main gauche reste en l'air sur le silence).",
            tip = "Garde le mouvement alterné même sans frapper : la main gauche fait le geste au-dessus de la peau. Le doigté reste toujours le même.",
            pattern = pattern(4, 4, Row(SNARE, "x.xx".repeat(4), alt("x.xx".repeat(4))), Row(KICK, "x...x...x...x...")),
            startBpm = 45, targetBpm = 85,
        ),
        Exercise(
            id = "dc-deux-doubles-croche",
            level = 7,
            title = "Deux doubles + croche",
            description = "Deux doubles croches puis une croche sur chaque temps. Doigté D G D .",
            tip = "C'est le galop à l'envers. Les trois coups sont réguliers, puis un silence d'une double : ne raccourcis pas le silence.",
            pattern = pattern(4, 4, Row(SNARE, "xxx.".repeat(4), alt("xxx.".repeat(4))), Row(KICK, "x...x...x...x...")),
            startBpm = 45, targetBpm = 85,
        ),
        Exercise(
            id = "dc-quart-soupir",
            level = 7,
            title = "Le quart de soupir",
            description = "Silence sur le temps, puis trois doubles croches : . G D G. Seule la grosse caisse joue sur le temps.",
            tip = "La main droite fait son geste en l'air avec la grosse caisse, la gauche part juste après. Écoute bien le pied pour te caler.",
            pattern = pattern(4, 4, Row(SNARE, ".xxx".repeat(4), alt(".xxx".repeat(4))), Row(KICK, "x...x...x...x...")),
            startBpm = 45, targetBpm = 80,
        ),
        Exercise(
            id = "dc-recap",
            level = 7,
            title = "Récapitulation doubles croches",
            description = "Deux mesures qui mélangent toutes les cellules : doubles, galop, deux doubles + croche, quart de soupir, noire.",
            tip = "Repère chaque cellule avant de lancer. Si une cellule bloque, reviens à son exercice seul.",
            pattern = run {
                val h = "xxxx" + "x.xx" + "xxx." + "x..." + ".xxx" + "x.xx" + "xxxx" + "x..."
                pattern(4, 4, Row(SNARE, h, alt(h)), Row(KICK, "x...x...x...x...".repeat(2)))
            },
            startBpm = 45, targetBpm = 80,
        ),
        // ---------------- Niveau 8 : le point et la syncope
        Exercise(
            id = "croche-pointee",
            level = 8,
            title = "Croche pointée + double",
            description = "Sur chaque temps : une croche pointée (3 doubles) puis une double. Doigté D . . G.",
            tip = "Pense « 1 e et a » et ne joue que le « 1 » et le « a ». Le coup de la main gauche est court et colle au temps suivant.",
            pattern = pattern(4, 4, Row(SNARE, "x..x".repeat(4), alt("x..x".repeat(4))), Row(KICK, "x...x...x...x...")),
            startBpm = 45, targetBpm = 85,
        ),
        Exercise(
            id = "point-melange",
            level = 8,
            title = "Pointées et croches",
            description = "Mesure 1 : croches pointées. Mesure 2 : croches normales puis doubles. Compare les deux sensations.",
            tip = "La croche pointée « sautille », la croche normale est égale. Si tout sonne pareil, ralentis.",
            pattern = run {
                val h = "x..x".repeat(4) + "x.x." + "x.x." + "xxxx" + "x..."
                pattern(4, 4, Row(SNARE, h, alt(h)), Row(KICK, "x...x...x...x...".repeat(2)))
            },
            startBpm = 45, targetBpm = 80,
        ),
        Exercise(
            id = "syncope",
            level = 8,
            title = "La syncope",
            description = "Croche, noire, croche : la noire tombe entre deux temps. Grosse caisse sur chaque temps, même quand les mains se taisent.",
            tip = "Sur le temps 2 et le temps 4, seul le pied joue. Si tu sens l'envie de frapper avec la main, c'est normal : retiens-la.",
            pattern = pattern(2, 4, Row(SNARE, "xx.xxx.x", alt("xx.xxx.x")), Row(KICK, "x.x.x.x.")),
            startBpm = 50, targetBpm = 95,
        ),
        Exercise(
            id = "syncope-appliquee",
            level = 8,
            title = "Syncope en lecture appliquée",
            description = "Le même rythme syncopé, mais en accents sur des croches continues : les mains ne s'arrêtent plus.",
            tip = "Les accents sur les « et » demandent une main gauche aussi forte que la droite. Travaille-la seule si besoin.",
            pattern = pattern(2, 4, *applied(">>.>>>.>")),
            startBpm = 50, targetBpm = 100,
        ),
        // ---------------- Niveau 9 : les triolets
        Exercise(
            id = "triolets",
            level = 9,
            title = "Triolets alternés",
            description = "Trois coups par temps : D G D  G D G. L'accent tombe sur chaque temps, donc une fois à droite, une fois à gauche.",
            tip = "Compte « 1 - et - a, 2 - et - a » ou « tri-o-let ». C'est l'exercice idéal pour égaliser les deux mains.",
            pattern = pattern(
                3, 4,
                Row(SNARE, "x".repeat(12), alt("x".repeat(12)), ">..>..>..>.."),
                Row(KICK, "x..x..x..x.."),
            ),
            startBpm = 50, targetBpm = 95,
        ),
        Exercise(
            id = "triolets-silence",
            level = 9,
            title = "Triolets avec silence",
            description = "Dans chaque triolet, la note du milieu se tait : il reste le 1er et le 3e coup. C'est la base du « shuffle ».",
            tip = "Garde le doigté alterné du triolet complet : la main du milieu frappe dans le vide. Ça sonne « ta - ti, ta - ti ».",
            pattern = pattern(3, 4, Row(SNARE, "x.x".repeat(4), alt("x.x".repeat(4))), Row(KICK, "x..x..x..x..")),
            startBpm = 50, targetBpm = 95,
        ),
        Exercise(
            id = "shuffle",
            level = 9,
            title = "Rythme shuffle",
            description = "Charleston en triolets sans la note du milieu, caisse claire sur 2 et 4, grosse caisse sur 1 et 3.",
            tip = "Le charleston « balance ». Joue d'abord la main droite seule jusqu'à ce que le balancement soit naturel.",
            pattern = pattern(
                3, 4,
                Row(HIHAT, "x.x".repeat(4), same("x.x".repeat(4), 'D')),
                Row(SNARE, "...x.....x..", "...G.....G.."),
                Row(KICK, "x.....x....."),
            ),
            startBpm = 55, targetBpm = 100,
        ),
        Exercise(
            id = "break-triolets",
            level = 9,
            title = "Break en triolets",
            description = "Une mesure de shuffle, puis une mesure de triolets : deux temps à la caisse claire, deux temps au tom.",
            tip = "Le passage au tom se fait sans casser le triolet : la main droite part vers le tom sur le temps 3.",
            pattern = pattern(
                3, 4,
                Row(HIHAT, "x.x".repeat(4) + "............", same("x.x".repeat(4), 'D') + "............"),
                Row(SNARE, "...x.....x.." + "xxxxxx......", "...G.....G.." + "DGDGDG......"),
                Row(TOM, "............" + "......xxxxxx", "............" + "......DGDGDG"),
                Row(KICK, "x.....x....." + "x.....x....."),
            ),
            startBpm = 50, targetBpm = 90,
        ),
        // ---------------- Niveau 10 : mesures composées
        Exercise(
            id = "six-huit",
            level = 10,
            title = "Le 6/8",
            description = "Deux temps par mesure, chacun divisé en trois croches. Accent et grosse caisse sur 1 et 4.",
            tip = "Le métronome bat la noire pointée (deux clics par mesure). Compte « 1 2 3 4 5 6 » et appuie le 1 et le 4.",
            pattern = pattern(3, 2, Row(SNARE, "xxxxxx", alt("xxxxxx"), ">..>.."), Row(KICK, "x..x..")),
            startBpm = 40, targetBpm = 70,
        ),
        Exercise(
            id = "six-huit-rythme",
            level = 10,
            title = "Rythme en 6/8",
            description = "Charleston sur les six croches, grosse caisse sur 1, caisse claire sur 4.",
            tip = "C'est le rythme des slows et des ballades. Laisse respirer : pas de précipitation sur la caisse claire.",
            pattern = pattern(
                3, 2,
                Row(HIHAT, "xxxxxx", "DDDDDD"),
                Row(SNARE, "...x..", "...G.."),
                Row(KICK, "x....."),
            ),
            startBpm = 40, targetBpm = 70,
        ),
        Exercise(
            id = "douze-huit",
            level = 10,
            title = "Le 12/8 blues",
            description = "Quatre temps de trois croches. Ride sur toutes les croches, caisse claire sur 2 et 4, grosse caisse sur 1 et 3.",
            tip = "C'est le shuffle, mais avec la croche du milieu jouée. La ride reste légère, la caisse claire bien posée.",
            pattern = pattern(
                3, 4,
                Row(RIDE, "x".repeat(12), "D".repeat(12)),
                Row(SNARE, "...x.....x..", "...G.....G.."),
                Row(KICK, "x.....x....."),
            ),
            startBpm = 45, targetBpm = 80,
        ),
        // ---------------- Niveau 11 : partitions
        Exercise(
            id = "partition-rock",
            level = 11,
            title = "Partition : rock et break",
            description = "Quatre mesures : trois mesures de rock, puis une mesure avec un break caisse claire et tom.",
            tip = "Compte les mesures « 1, 2, 3, break ». Le retour sur le 1 après le break est le moment le plus important.",
            pattern = pattern(
                4, 4,
                Row(HIHAT,
                    "x.x.x.x.x.x.x.x.".repeat(3) + "x.x.x.x.........",
                    "D.D.D.D.D.D.D.D.".repeat(3) + "D.D.D.D........."),
                Row(SNARE,
                    "....x.......x...".repeat(3) + "....x...xxxx....",
                    "....G.......G...".repeat(3) + "....G...DGDG...."),
                Row(TOM, "................".repeat(3) + "............xxxx", "................".repeat(3) + "............DGDG"),
                Row(KICK, "x.......x.......".repeat(4)),
            ),
            startBpm = 55, targetBpm = 100, rampEveryBars = 8,
        ),
        Exercise(
            id = "partition-gc",
            level = 11,
            title = "Partition : grosse caisse en doubles",
            description = "Deux mesures de groove : la grosse caisse joue des doubles croches différentes à chaque mesure.",
            tip = "Le charleston et la caisse claire ne bougent pas. Lis la grosse caisse sur l'écran, elle change à la 2e mesure.",
            pattern = pattern(
                4, 4,
                Row(HIHAT, "x.x.x.x.x.x.x.x.".repeat(2), "D.D.D.D.D.D.D.D.".repeat(2)),
                Row(SNARE, "....x.......x...".repeat(2), "....G.......G...".repeat(2)),
                Row(KICK, "x.....x.x......." + "x.....x...x....."),
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
        5 -> "Niveau 5 · Indépendance des membres"
        6 -> "Niveau 6 · Lecture appliquée (accents)"
        7 -> "Niveau 7 · Doubles croches et silences"
        8 -> "Niveau 8 · Le point et la syncope"
        9 -> "Niveau 9 · Les triolets"
        10 -> "Niveau 10 · Mesures composées"
        else -> "Niveau 11 · Partitions"
    }
}
