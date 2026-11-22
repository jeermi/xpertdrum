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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.xpert.drum.data.ProgressStore
import fr.xpert.drum.practice.Exercises
import fr.xpert.drum.ui.Nav
import fr.xpert.drum.ui.components.Pill
import fr.xpert.drum.ui.components.ScreenHeader
import fr.xpert.drum.ui.components.SectionLabel
import fr.xpert.drum.ui.components.XCard
import fr.xpert.drum.ui.theme.X
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressScreen(store: ProgressStore, nav: Nav) {
    val history = store.history()
    val bests = store.bests()
    val minutes = store.totalPracticeSeconds() / 60
    val days7 = practiceDaysLast7(history.map { it.atMillis })
    val fmt = SimpleDateFormat("EEE d MMM · HH:mm", Locale.FRANCE)

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Ma progression") { nav.back() }
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BigStat("$minutes", "min de pratique", Modifier.weight(1f))
                BigStat("${history.size}", "séances", Modifier.weight(1f))
                BigStat("$days7/7", "jours cette semaine", Modifier.weight(1f))
            }

            SectionLabel("Records par exercice")
            if (bests.isEmpty()) {
                Text("Pas encore de séance. Lance ton premier exercice !", color = X.Texte2)
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (ex in Exercises.all) {
                    val b = bests[ex.id] ?: continue
                    XCard {
                        Text(ex.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Pill("${b.accuracy} %", if (b.accuracy >= 80) X.Vert else X.Jaune)
                            Spacer(Modifier.width(8.dp))
                            Pill(if (b.bpm > 0) "${b.bpm} / ${ex.targetBpm} BPM" else "80 % pas encore atteint")
                            Spacer(Modifier.width(8.dp))
                            Text("${b.sessions} séance(s)", color = X.Texte2)
                        }
                    }
                }
            }

            SectionLabel("Dernières séances")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (r in history.takeLast(25).reversed()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(r.title, style = MaterialTheme.typography.bodyLarge)
                            Text(fmt.format(Date(r.atMillis)), color = X.Texte2, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text("${r.accuracy} % · ${r.bpm} BPM", color = if (r.accuracy >= 80) X.Vert else X.Jaune)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BigStat(value: String, label: String, modifier: Modifier) {
    XCard(modifier) {
        Text(value, color = X.Jaune, style = MaterialTheme.typography.headlineSmall)
        Text(label, color = X.Texte2, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun practiceDaysLast7(times: List<Long>): Int {
    val tz = java.util.TimeZone.getDefault()
    fun day(t: Long) = (t + tz.getOffset(t)) / 86_400_000L
    val today = day(System.currentTimeMillis())
    return times.map { day(it) }.filter { today - it in 0..6 }.distinct().size
}
