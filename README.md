# XPert Drum

Appli Android perso pour apprendre la batterie, niveau grand débutant. Installée à la main (APK), sans Play Store.
Charte graphique et logo repris de [XPert](https://jeermi.github.io/xpert/).

## Ce qu'il y a dedans

- **Les bases** : 6 leçons (s'installer et se tenir, tenir les baguettes, le geste, les éléments de la batterie, compter et lire le rythme, bien travailler).
- **Exercices** : 39 exercices progressifs (noires, croches alternées, grosse caisse, charleston, coordination, coups doubles, premier rythme rock, variante, paradiddle, premier break, puis 7 exercices d'**indépendance** bras G/D et jambes G/D, et 22 exercices de lecture qui suivent la progression de la méthode Dante Agostini vol. 1 : lecture appliquée avec accents, doubles croches et silences, point et syncope, triolets et shuffle, 6/8 et 12/8, partitions de 2 à 4 mesures). Les exercices sont écrits pour l'appli, ils ne recopient pas les pages du livre.
  - partition de batterie qui défile, avec les doigtés D / G ;
  - **l'appli écoute ta vraie batterie au micro** et donne un verdict à chaque coup : **pile**, **en avance** ou **en retard**, avec l'écart en ms (le micro juge le placement rythmique, pas l'élément frappé) ;
  - en dépannage, pads tactiles à l'écran (multi-doigts) ;
  - décompte d'une mesure, métronome, **tempo qui monte** automatiquement ;
  - mode « Écouter » où l'appli joue le motif ;
  - bilan de fin : précision, tendance avance / retard, conseil pour la suite.
- **Métronome** : 30 à 240 BPM, mesures 2/4 à 6/4, tap tempo, tempo qui monte.
- **Pads libres** avec ou sans clic.
- **Réglage du micro** (1 min) : bruit de la pièce, délai du téléphone mesuré en écoutant son propre clic, seuil de détection adapté à la force de tes coups. Le son est analysé en direct, jamais enregistré.
- **Calibration des pads** : délai son + écran, si tu joues sur l'écran.
- **Progression** : temps de pratique, records par exercice, historique (stocké sur le téléphone uniquement).

Les sons de batterie sont synthétisés dans l'appli : aucun échantillon externe.

## Installer l'APK

1. Depuis le téléphone, ouvre la [dernière release](https://github.com/jeermi/xpertdrum/releases/latest) et télécharge `xpert-drum.apk`. Chaque modification sur `main` en publie une nouvelle automatiquement.
2. Copie `xpert-drum.apk` sur le téléphone et ouvre-le. Android demande d'autoriser l'installation depuis cette source : accepte.
3. Les mises à jour s'installent par-dessus, tant que l'APK est signé avec la même clé (voir plus bas).

## Compiler soi-même

Prérequis : JDK 17 et le SDK Android (Android Studio suffit).

```bash
./gradlew testDebugUnitTest   # tests
./gradlew assembleRelease     # APK : app/build/outputs/apk/release/app-release.apk
```

Sans clé perso, l'APK est signé avec une clé de debug qui change à chaque build sur GitHub Actions : Android refusera
alors d'installer une mise à jour par-dessus (« conflit de signature ») et il faudra désinstaller, ce qui efface la progression.
Pour l'éviter, crée une clé une fois pour toutes et ajoute-la en secrets du dépôt :

```bash
keytool -genkeypair -keystore xpert.jks -alias xpertdrum -keyalg RSA -keysize 2048 -validity 10000
base64 -w0 xpert.jks   # à coller dans le secret XPERT_KEYSTORE_B64
```

puis le mot de passe (le même pour la clé et le magasin) dans le secret `XPERT_KEYSTORE_PASSWORD`
(Settings > Secrets and variables > Actions). Garde `xpert.jks` en lieu sûr, hors du dépôt.

## Architecture

- `audio/` : moteur audio basse latence (`AudioTrack`, mode `PERFORMANCE_MODE_LOW_LATENCY`). Les sons sont planifiés
  à la frame près, et `AudioTrack.getTimestamp()` relie la position audio à l'horloge système pour juger les coups.
  `MicListener` lit le micro (`AudioRecord`, source `UNPROCESSED` si disponible) et `OnsetDetector` repère les attaques
  (pré-accentuation, énergie par trames de 2,7 ms comparée au seuil et au niveau précédent, instant affiné à l'échantillon).
- `practice/` : modèle (éléments, motifs, exercices), séance d'exercice (décompte, boucle, tempo qui monte, jugement), métronome.
- `content/` : textes des leçons.
- `data/` : progression et réglages (SharedPreferences).
- `ui/` : écrans Jetpack Compose et thème XPert.

## Pistes suivantes

- Reconnaître quel élément est frappé (grosse caisse / caisse claire / charleston) au micro.
- Import de partitions MusicXML / MIDI.
- Quiz théorie / lecture / rudiments avec le moteur XPert.
- Moteur natif Oboe si la latence d'`AudioTrack` s'avère trop élevée sur certains téléphones.
