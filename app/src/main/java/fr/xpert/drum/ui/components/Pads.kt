package fr.xpert.drum.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.xpert.drum.practice.Lane
import fr.xpert.drum.ui.theme.X

fun laneColor(lane: Lane): Color = when (lane) {
    Lane.CRASH -> Color(0xFFFFAB19)
    Lane.HIHAT -> Color(0xFFFBC63C)
    Lane.TOM -> Color(0xFF8E7CF0)
    Lane.SNARE -> Color(0xFFE8501E)
    Lane.KICK -> Color(0xFF5B8DEF)
}

/**
 * Pad tactile. Réagit à l'appui (pas au relâchement) et transmet l'horodatage
 * de l'évènement tactile en nanosecondes (même horloge que System.nanoTime).
 * Plusieurs doigts sur plusieurs pads en même temps : OK.
 */
@Composable
fun DrumPad(
    lane: Lane,
    nowNanos: Long,
    modifier: Modifier = Modifier,
    hint: String? = null,
    onHit: (Lane, Long) -> Unit,
) {
    var lastHit by remember { mutableLongStateOf(0L) }
    val currentOnHit by rememberUpdatedState(onHit)
    val lit = nowNanos - lastHit < 110_000_000L
    val c = laneColor(lane)
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier
            .background(if (lit) c.copy(alpha = 0.55f) else c.copy(alpha = 0.14f), shape)
            .border(2.dp, c.copy(alpha = if (lit) 1f else 0.6f), shape)
            .pointerInput(lane) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        for (change in event.changes) {
                            if (change.pressed && !change.previousPressed) {
                                val t = change.uptimeMillis * 1_000_000L
                                lastHit = System.nanoTime()
                                currentOnHit(lane, t)
                                change.consume()
                            }
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(lane.short, color = c, fontSize = 26.sp, style = MaterialTheme.typography.headlineSmall)
            Text(lane.label, color = X.Texte2, fontSize = 13.sp, textAlign = TextAlign.Center)
            if (hint != null) Text(hint, color = X.Texte2, fontSize = 12.sp)
        }
    }
}

/**
 * Disposition des pads : mains en haut (dans l'ordre crash, charleston, tom, caisse claire),
 * grosse caisse en large bande en bas, sous le pouce.
 */
@Composable
fun PadGrid(
    lanes: List<Lane>,
    nowNanos: Long,
    modifier: Modifier = Modifier,
    onHit: (Lane, Long) -> Unit,
) {
    val hands = lanes.filter { !it.foot }
    val feet = lanes.filter { it.foot }
    val gap = 10.dp
    Column(modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(gap)) {
        val handRows = if (hands.size > 3) hands.chunked(2) else listOf(hands)
        for (row in handRows) {
            if (row.isEmpty()) continue
            Row(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) {
                for (lane in row) {
                    DrumPad(lane, nowNanos, Modifier.weight(1f).fillMaxHeight(), onHit = onHit)
                }
            }
        }
        for (lane in feet) {
            DrumPad(
                lane, nowNanos,
                Modifier
                    .fillMaxWidth()
                    .weight(if (hands.isEmpty()) 1f else 0.7f),
                onHit = onHit,
            )
        }
    }
}
