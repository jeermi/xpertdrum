package fr.xpert.drum.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.xpert.drum.data.ProgressStore
import fr.xpert.drum.practice.Exercises
import fr.xpert.drum.ui.Nav
import fr.xpert.drum.ui.Screen
import fr.xpert.drum.ui.components.Pill
import fr.xpert.drum.ui.components.ScreenHeader
import fr.xpert.drum.ui.components.SectionLabel
import fr.xpert.drum.ui.components.XCard
import fr.xpert.drum.ui.components.laneColor
import fr.xpert.drum.ui.theme.X

@Composable
fun ExercisesScreen(store: ProgressStore, nav: Nav) {
    val bests = store.bests()
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Exercices") { nav.back() }
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            if (store.useMic && !store.isMicCalibrated) {
                XCard(onClick = { nav.go(Screen.MicSetup) }) {
                    Text("🎤 Fais d'abord le réglage du micro", style = MaterialTheme.typography.titleMedium, color = X.Jaune)
                    Text(
                        "1 minute pour que l'appli entende bien ta batterie et connaisse le délai de ton téléphone.",
                        color = X.Texte2,
                    )
                }
            }
            for ((level, list) in Exercises.all.groupBy { it.level }) {
                SectionLabel(Exercises.levelName(level))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (ex in list) {
                        val best = bests[ex.id]
                        XCard(onClick = { nav.go(Screen.Exercise(ex.id)) }) {
                            Text(ex.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(2.dp))
                            Row {
                                for (lane in ex.pattern.lanes) {
                                    Text(lane.short, color = laneColor(lane), style = MaterialTheme.typography.labelLarge)
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text("${ex.startBpm} → ${ex.targetBpm} BPM", color = X.Texte2, style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(ex.description, color = X.Texte2, style = MaterialTheme.typography.bodyMedium)
                            if (best != null) {
                                Spacer(Modifier.height(8.dp))
                                Row {
                                    Pill("Record ${best.accuracy} %", if (best.accuracy >= 80) X.Vert else X.Jaune)
                                    if (best.bpm > 0) {
                                        Spacer(Modifier.width(8.dp))
                                        Pill("${best.bpm} BPM")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
