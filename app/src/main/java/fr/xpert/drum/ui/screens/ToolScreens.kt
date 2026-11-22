package fr.xpert.drum.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.xpert.drum.audio.AudioEngine
import fr.xpert.drum.data.ProgressStore
import fr.xpert.drum.practice.Lane
import fr.xpert.drum.practice.MetronomeScheduler
import fr.xpert.drum.ui.Nav
import fr.xpert.drum.ui.components.PadGrid
import fr.xpert.drum.ui.components.PrimaryButton
import fr.xpert.drum.ui.components.ScreenHeader
import fr.xpert.drum.ui.components.SecondaryButton
import fr.xpert.drum.ui.components.XCard
import fr.xpert.drum.ui.theme.X
import kotlin.math.abs

/** Boucle d'animation : renvoie System.nanoTime() à chaque image tant que [active]. */
@Composable
private fun frameClock(active: Boolean): Long {
    var now by remember { mutableLongStateOf(System.nanoTime()) }
    LaunchedEffect(active) {
        if (!active) return@LaunchedEffect
        while (true) {
            withFrameNanos { }
            now = System.nanoTime()
        }
    }
    return now
}

// ---------------------------------------------------------------- Métronome

@Composable
fun MetronomeScreen(engine: AudioEngine, store: ProgressStore, nav: Nav) {
    val metro = remember { MetronomeScheduler(engine, store.metronomeBpm) }
    var running by remember { mutableStateOf(false) }
    var bpm by remember { mutableIntStateOf(metro.bpm) }
    var beats by remember { mutableIntStateOf(4) }
    var ramp by remember { mutableStateOf(false) }
    var rampTarget by remember { mutableIntStateOf(minOf(bpm + 40, 240)) }
    val taps = remember { mutableStateListOf<Long>() }
    val now = frameClock(running)

    fun setBpm(v: Int) {
        bpm = v.coerceIn(30, 240); metro.bpm = bpm; store.metronomeBpm = bpm
    }
    DisposableEffect(Unit) { onDispose { if (engine.scheduler === metro) engine.scheduler = null } }

    // Le tempo qui monte modifie metro.bpm depuis le thread audio : on le reflète à l'écran.
    if (running && metro.bpm != bpm) bpm = metro.bpm

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Métronome") { nav.back() }
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val current = if (running) metro.lastBeat(now) else null
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                for (i in 1..beats) {
                    val on = current?.number == i
                    Box(
                        Modifier
                            .size(if (i == 1) 30.dp else 24.dp)
                            .background(
                                when {
                                    on && i == 1 -> X.Orange
                                    on -> X.Jaune
                                    else -> X.Pastille
                                },
                                CircleShape,
                            )
                    )
                }
            }
            Text("$bpm", fontSize = 84.sp, color = X.Texte, style = MaterialTheme.typography.headlineLarge)
            Text("BPM · ${tempoName(bpm)}", color = X.Texte2)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (d in listOf(-5, -1, +1, +5)) {
                    SecondaryButton(if (d > 0) "+$d" else "$d", Modifier.weight(1f)) { setBpm(bpm + d) }
                }
            }
            Slider(
                value = bpm.toFloat(),
                onValueChange = { setBpm(it.toInt()) },
                valueRange = 30f..240f,
                colors = SliderDefaults.colors(thumbColor = X.Orange, activeTrackColor = X.Orange),
            )
            PrimaryButton(if (running) "■ Stop" else "▶ Démarrer", Modifier.fillMaxWidth()) {
                running = !running
                engine.scheduler = if (running) metro.also { it.bpm = bpm } else null
            }
            SecondaryButton("👆 Tap tempo", Modifier.fillMaxWidth()) {
                val t = System.nanoTime()
                if (taps.isNotEmpty() && t - taps.last() > 2_000_000_000L) taps.clear()
                taps.add(t)
                if (taps.size > 6) taps.removeAt(0)
                if (taps.size >= 3) {
                    val avg = (taps.last() - taps.first()) / (taps.size - 1)
                    setBpm((60_000_000_000L / avg).toInt())
                }
            }
            XCard {
                Text("Mesure", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (b in listOf(2, 3, 4, 6)) {
                        FilterChip(
                            selected = beats == b,
                            onClick = { beats = b; metro.beatsPerBar = b },
                            label = { Text("$b/4") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = X.Orange, selectedLabelColor = X.Texte,
                            ),
                        )
                    }
                }
                ToggleRow("Tempo qui monte", "+4 BPM toutes les 4 mesures jusqu'à $rampTarget BPM", ramp) {
                    ramp = it; metro.rampEnabled = it; metro.rampTarget = rampTarget
                }
                if (ramp) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Objectif", Modifier.weight(1f), color = X.Texte2)
                        TextButton(onClick = { rampTarget = (rampTarget - 5).coerceAtLeast(40); metro.rampTarget = rampTarget }) { Text("−5", color = X.Jaune) }
                        Text("$rampTarget", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { rampTarget = (rampTarget + 5).coerceAtMost(240); metro.rampTarget = rampTarget }) { Text("+5", color = X.Jaune) }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun tempoName(bpm: Int) = when {
    bpm < 60 -> "Largo"
    bpm < 76 -> "Adagio"
    bpm < 108 -> "Andante / Moderato"
    bpm < 168 -> "Allegro"
    else -> "Presto"
}

// ---------------------------------------------------------------- Pads libres

@Composable
fun FreePadsScreen(engine: AudioEngine, nav: Nav) {
    val metro = remember { MetronomeScheduler(engine, 80) }
    var clickOn by remember { mutableStateOf(false) }
    var bpm by remember { mutableIntStateOf(80) }
    val now = frameClock(true)
    DisposableEffect(Unit) { onDispose { if (engine.scheduler === metro) engine.scheduler = null } }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Pads libres") { nav.back() }
        Row(
            Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = clickOn,
                onClick = {
                    clickOn = !clickOn
                    engine.scheduler = if (clickOn) metro else null
                },
                label = { Text(if (clickOn) "Clic : $bpm BPM" else "Clic coupé") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = X.Orange, selectedLabelColor = X.Texte),
            )
            TextButton(onClick = { bpm = (bpm - 5).coerceAtLeast(30); metro.bpm = bpm }) { Text("−5", color = X.Jaune) }
            TextButton(onClick = { bpm = (bpm + 5).coerceAtMost(240); metro.bpm = bpm }) { Text("+5", color = X.Jaune) }
        }
        PadGrid(
            listOf(Lane.CRASH, Lane.HIHAT, Lane.TOM, Lane.SNARE, Lane.KICK),
            now,
            Modifier
                .weight(1f)
                .padding(16.dp),
        ) { lane, _ -> engine.playNow(engine.kit.forLane(lane)) }
    }
}

// ---------------------------------------------------------------- Calibration

@Composable
fun CalibrationScreen(engine: AudioEngine, store: ProgressStore, nav: Nav) {
    val metro = remember { MetronomeScheduler(engine, 80, accent = false) }
    var running by remember { mutableStateOf(false) }
    val deltas = remember { mutableStateListOf<Int>() }
    var tapCount by remember { mutableIntStateOf(0) }
    var result by remember { mutableStateOf<Int?>(null) }
    var lastTap by remember { mutableLongStateOf(0L) }
    val now = frameClock(running)
    val needed = 12
    val warmup = 4

    fun finish() {
        running = false
        engine.scheduler = null
        val sorted = deltas.sorted()
        result = sorted[sorted.size / 2]
    }
    DisposableEffect(Unit) { onDispose { if (engine.scheduler === metro) engine.scheduler = null } }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Calibration") { nav.back() }
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                "Chaque téléphone met un petit temps à jouer le son et à détecter ton doigt. On le mesure une fois " +
                    "pour que l'appli te juge juste.",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                "Monte le son (sans écouteurs Bluetooth, qui ajoutent beaucoup de délai), lance, puis tape dans le " +
                    "grand carré exactement sur chaque clic, ${warmup + needed} fois.",
                color = X.Texte2,
            )
            val r = result
            if (r != null) {
                XCard {
                    Text("Décalage mesuré : $r ms", color = X.Jaune, style = MaterialTheme.typography.titleLarge)
                    Text(
                        when {
                            abs(r) < 15 -> "Ton téléphone est très réactif, presque aucune correction n'est nécessaire."
                            r > 0 -> "Tes frappes arrivent un peu après le son : l'appli en tiendra compte."
                            else -> "Tes frappes arrivent un peu avant le son : l'appli en tiendra compte."
                        },
                        color = X.Texte2,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SecondaryButton("Refaire", Modifier.weight(1f)) {
                        result = null; deltas.clear(); tapCount = 0
                    }
                    PrimaryButton("Enregistrer", Modifier.weight(1f)) {
                        store.calibrationMs = r
                        nav.back()
                    }
                }
            } else {
                val lit = running && metro.lastBeat(now)?.let { now - engine.frameToNanos(it.frame) < 120_000_000L } == true
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(if (lit) X.Orange.copy(alpha = 0.35f) else X.Carte, RoundedCornerShape(28.dp))
                        .border(2.dp, if (now - lastTap < 100_000_000L) X.Jaune else X.CarteBord, RoundedCornerShape(28.dp))
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val ev = awaitPointerEvent()
                                    for (c in ev.changes) {
                                        if (!(c.pressed && !c.previousPressed)) continue
                                        c.consume()
                                        if (engine.scheduler !== metro) continue
                                        val t = c.uptimeMillis * 1_000_000L
                                        lastTap = System.nanoTime()
                                        val beat = metro.beatMarks.minByOrNull { abs(t - engine.frameToNanos(it.frame)) }
                                            ?: continue
                                        val d = ((t - engine.frameToNanos(beat.frame)) / 1_000_000L).toInt()
                                        tapCount++
                                        if (tapCount > warmup && abs(d) < 300) deltas.add(d)
                                        if (deltas.size >= needed) finish()
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (running) "Tape sur le clic\n${minOf(tapCount, warmup + needed)} / ${warmup + needed}" else "Touche « Lancer »",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
                PrimaryButton(if (running) "Annuler" else "▶ Lancer", Modifier.fillMaxWidth()) {
                    if (running) {
                        running = false; engine.scheduler = null
                    } else {
                        deltas.clear(); tapCount = 0
                        metro.beatMarks.clear()
                        running = true
                        engine.scheduler = metro
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
