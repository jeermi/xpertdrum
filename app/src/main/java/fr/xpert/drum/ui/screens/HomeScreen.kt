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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.xpert.drum.content.Lessons
import fr.xpert.drum.data.ProgressStore
import fr.xpert.drum.practice.Exercises
import fr.xpert.drum.ui.Nav
import fr.xpert.drum.ui.Screen
import fr.xpert.drum.ui.components.Pill
import fr.xpert.drum.ui.components.SectionLabel
import fr.xpert.drum.ui.components.XCard
import fr.xpert.drum.ui.components.XpertWordmark
import fr.xpert.drum.ui.theme.X

@Composable
fun HomeScreen(store: ProgressStore, nav: Nav) {
    val read = store.lessonsRead().size
    val bests = store.bests()
    val minutes = store.totalPracticeSeconds() / 60
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        XpertWordmark()
        Spacer(Modifier.height(24.dp))
        Text(
            buildAnnotatedString {
                val name = store.firstName
                append(if (name.isEmpty()) "Salut, on joue " else "Salut $name, on joue ")
                withStyle(SpanStyle(color = X.Jaune)) { append("quoi") }
                append(" ?")
            },
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Les bases pour bien démarrer, des exercices qui écoutent ta batterie et un métronome qui te fait progresser.",
            color = X.Texte2,
            style = MaterialTheme.typography.bodyLarge,
        )

        SectionLabel("Apprendre")
        MenuCard(
            "🥁", "Les bases", "Posture, tenue des baguettes, geste, lecture",
            "$read leçon${if (read > 1) "s" else ""} lue${if (read > 1) "s" else ""} sur ${Lessons.all.size}",
        ) { nav.go(Screen.Lessons) }
        Spacer(Modifier.height(12.dp))
        MenuCard(
            "🎯", "Exercices", "Joue sur ta batterie : pile, en avance ou en retard",
            "${bests.size} exercice${if (bests.size > 1) "s" else ""} joué${if (bests.size > 1) "s" else ""} sur ${Exercises.all.size}",
        ) { nav.go(Screen.Exercises) }

        SectionLabel("Outils")
        MenuCard("⏱️", "Métronome", "Tempo, mesure et tempo qui monte", null) { nav.go(Screen.Metronome) }

        SectionLabel("Moi")
        MenuCard(
            "📈", "Ma progression", "Temps de pratique, records et historique",
            if (minutes > 0) "$minutes min de pratique" else null,
        ) { nav.go(Screen.Progress) }
        Spacer(Modifier.height(12.dp))
        MenuCard(
            "🎤", "Réglage du micro", "Pour que l'appli entende ta batterie (1 min)",
            if (store.isMicCalibrated) "Réglé · délai ${store.micOffsetMs} ms" else "À faire avant le premier exercice",
        ) { nav.go(Screen.MicSetup) }
        Spacer(Modifier.height(12.dp))
        MenuCard("✏️", "Mon prénom", "Changer le prénom affiché", store.firstName.ifEmpty { null }) { nav.go(Screen.Name) }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun MenuCard(icon: String, title: String, subtitle: String, pill: String?, onClick: () -> Unit) {
    XCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 34.sp)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(subtitle, color = X.Texte2, style = MaterialTheme.typography.bodyMedium)
                if (pill != null) {
                    Spacer(Modifier.height(4.dp))
                    Pill(pill)
                }
            }
            Text("›", color = X.Jaune, fontSize = 28.sp)
        }
    }
}
