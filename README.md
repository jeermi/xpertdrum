# XPert Drum

Appli Android perso pour apprendre la batterie, niveau grand débutant. Installée à la main (APK), sans Play Store.
Charte graphique et logo repris de [XPert](https://jeermi.github.io/xpert/).

## Ce qu'il y a dedans

- **Les bases** : 6 leçons (s'installer et se tenir, tenir les baguettes, le geste, les éléments de la batterie, compter et lire le rythme, bien travailler).
- **Exercices** : 10 exercices progressifs (noires, croches alternées, grosse caisse, charleston, coordination, coups doubles, premier rythme rock, variante, paradiddle, premier break).
  - partition de batterie qui défile, avec les doigtés D / G ;
  - pads tactiles (multi-doigts) et verdict à chaque coup : **pile**, **en avance** ou **en retard**, avec l'écart en ms ;
  - décompte d'une mesure, métronome, **tempo qui monte** automatiquement ;
  - mode « Écouter » où l'appli joue le motif ;
  - bilan de fin : précision, tendance avance / retard, conseil pour la suite.
- **Métronome** : 30 à 240 BPM, mesures 2/4 à 6/4, tap tempo, tempo qui monte.
- **Pads libres** avec ou sans clic.
- **Calibration** : mesure le délai son + écran propre au téléphone pour un verdict juste.
- **Progression** : temps de pratique, records par exercice, historique (stocké sur le téléphone uniquement).

Les sons de batterie sont synthétisés dans l'appli : aucun échantillon externe.

## Installer l'APK

1. Ouvre l'onglet **Actions** du dépôt, choisis le dernier run « Build APK » réussi et télécharge l'artefact `xpert-drum-apk` (ou, pour une version taguée `v*`, l'APK joint à la **Release**).
2. Copie `xpert-drum.apk` sur le téléphone et ouvre-le. Android demande d'autoriser l'installation depuis cette source : accepte.
3. Les mises à jour s'installent par-dessus, tant que l'APK est signé avec la même clé (voir plus bas).

## Compiler soi-même

Prérequis : JDK 17 et le SDK Android (Android Studio suffit).

```bash
./gradlew testDebugUnitTest   # tests
./gradlew assembleRelease     # APK : app/build/outputs/apk/release/app-release.apk
```

L'APK « release » est signé avec la clé de debug, ce qui suffit pour une installation manuelle. Attention : la clé de debug
générée sur GitHub Actions change d'une machine à l'autre ; si Android refuse une mise à jour (« conflit de signature »),
désinstalle l'ancienne version, ou ajoute une clé perso (à venir).

## Architecture

- `audio/` : moteur audio basse latence (`AudioTrack`, mode `PERFORMANCE_MODE_LOW_LATENCY`). Les sons sont planifiés
  à la frame près, et `AudioTrack.getTimestamp()` relie la position audio à l'horloge système pour juger les coups.
- `practice/` : modèle (éléments, motifs, exercices), séance d'exercice (décompte, boucle, tempo qui monte, jugement), métronome.
- `content/` : textes des leçons.
- `data/` : progression et réglages (SharedPreferences).
- `ui/` : écrans Jetpack Compose et thème XPert.

## Pistes suivantes

- Import de partitions MusicXML / MIDI.
- Quiz théorie / lecture / rudiments avec le moteur XPert.
- Moteur natif Oboe si la latence d'`AudioTrack` s'avère trop élevée sur certains téléphones.
