package fr.xpert.drum.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.xpert.drum.audio.AudioEngine
import fr.xpert.drum.content.Block
import fr.xpert.drum.content.Lessons
import fr.xpert.drum.data.ProgressStore
import fr.xpert.drum.practice.RhythmPlayer
import fr.xpert.drum.ui.Nav
import fr.xpert.drum.ui.Screen
import fr.xpert.drum.ui.components.Illustration
import fr.xpert.drum.ui.components.Pill
import fr.xpert.drum.ui.components.PrimaryButton
import fr.xpert.drum.ui.components.ScreenHeader
import fr.xpert.drum.ui.components.SecondaryButton
import fr.xpert.drum.ui.components.XCard
import fr.xpert.drum.ui.theme.X
import kotlinx.coroutines.delay

@Composable
fun LessonsScreen(store: ProgressStore, nav: Nav) {
    val read = store.lessonsRead()
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Les bases") { nav.back() }
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "À lire dans l'ordre avant tes premiers exercices. Tu peux y revenir quand tu veux.",
                color = X.Texte2,
            )
            Lessons.all.forEachIndexed { i, lesson ->
                XCard(onClick = { nav.go(Screen.Lesson(lesson.id)) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(lesson.icon, fontSize = 30.sp)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text("${i + 1}. ${lesson.title}", style = MaterialTheme.typography.titleMedium, color = X.Texte)
                            Text(lesson.summary, color = X.Texte2, style = MaterialTheme.typography.bodyMedium)
                            if (lesson.id in read) {
                                Spacer(Modifier.height(6.dp))
                                Pill("✔ Lue", X.Vert)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun LessonScreen(id: String, engine: AudioEngine, store: ProgressStore, nav: Nav) {
    val lesson = Lessons.byId(id) ?: return
    LaunchedEffect(id) { store.markLessonRead(id) }
    DisposableEffect(id) { onDispose { if (engine.scheduler is RhythmPlayer) engine.scheduler = null } }
    val index = Lessons.all.indexOf(lesson)
    val next = Lessons.all.getOrNull(index + 1)
    var playing by remember { mutableStateOf<Block.Advice?>(null) }
    val current = playing
    if (current?.rhythm != null) {
        LaunchedEffect(current) {
            val r = current.rhythm
            val player = RhythmPlayer(engine, r.pattern, r.bpm, r.bars, r.click)
            engine.scheduler = player
            delay(player.durationMs)
            if (engine.scheduler === player) engine.scheduler = null
            playing = null
        }
    }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(lesson.title) { nav.back() }
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            for (b in lesson.blocks) {
                BlockView(
                    b,
                    playingRhythm = b === current,
                    onSound = { lane -> engine.playNow(engine.kit.forLane(lane)) },
                    onRhythm = { adv ->
                        if (playing === adv) {
                            playing = null
                            if (engine.scheduler is RhythmPlayer) engine.scheduler = null
                        } else {
                            playing = adv
                        }
                    },
                )
            }
            Spacer(Modifier.height(8.dp))
            if (next != null) {
                PrimaryButton("Leçon suivante : ${next.title}", Modifier.fillMaxWidth()) {
                    nav.back(); nav.go(Screen.Lesson(next.id))
                }
            } else {
                PrimaryButton("Passer aux exercices", Modifier.fillMaxWidth()) {
                    nav.back(); nav.go(Screen.Exercises)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BlockView(
    b: Block,
    playingRhythm: Boolean,
    onSound: (fr.xpert.drum.practice.Lane) -> Unit,
    onRhythm: (Block.Advice) -> Unit,
) {
    when (b) {
        is Block.Text -> Text(b.text, style = MaterialTheme.typography.bodyLarge, color = X.Texte)
        is Block.Section -> Text(
            b.title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = X.Texte2,
            modifier = Modifier.padding(top = 8.dp),
        )
        is Block.Advice -> XCard {
            if (b.title != null) {
                Text(b.title, style = MaterialTheme.typography.titleMedium, color = X.Jaune)
                Spacer(Modifier.height(10.dp))
            }
            Illustration(
                b.illus,
                Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(X.Fond.copy(alpha = 0.55f))
                    .padding(6.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(b.text, style = MaterialTheme.typography.bodyLarge, color = X.Texte)
            val sound = b.sound
            if (sound != null || b.rhythm != null) {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (sound != null) {
                        SecondaryButton("🔊 Écouter") { onSound(sound) }
                    }
                    if (b.rhythm != null) {
                        SecondaryButton(if (playingRhythm) "■ Arrêter" else "▶ Écouter le rythme") { onRhythm(b) }
                    }
                }
            }
        }
        is Block.Tip -> Callout("💡 Astuce", b.text, X.Jaune, X.Jaune)
        is Block.Warning -> Callout("⚠️ Attention", b.text, X.Orange, X.OrangeTexte)
    }
}

@Composable
private fun Callout(title: String, text: String, color: Color, titleColor: Color) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(title, color = titleColor, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, color = X.Texte)
    }
}
