package fr.xpert.drum.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import fr.xpert.drum.practice.Lane
import fr.xpert.drum.practice.PracticeSession
import fr.xpert.drum.ui.theme.X

/**
 * Portée de batterie qui défile de droite à gauche. La barre verticale (à gauche)
 * est « maintenant » : frappe quand une note la touche.
 */
@Composable
fun ScrollingStaff(session: PracticeSession, nowNanos: Long, modifier: Modifier = Modifier) {
    val textPaint = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
    }
    Canvas(modifier) {
        val space = 12.dp.toPx()
        val top = 3.8f * space + 4.dp.toPx()
        val playX = size.width * 0.2f
        val pxPerSec = 190.dp.toPx()
        val lineW = 1.4.dp.toPx()

        fun xAt(nanos: Long) = playX + ((nanos - nowNanos) / 1e9f) * pxPerSec
        fun yAt(pos: Int) = top + pos * space / 2f

        for (i in 0..4) {
            drawLine(X.CarteBord, Offset(0f, top + i * space), Offset(size.width, top + i * space), lineW)
        }

        // Barres de mesure
        for (b in session.barMarks) {
            val x = xAt(session.frameNanos(b.frame))
            if (x < -10f || x > size.width + 10f) continue
            drawLine(
                if (b.countIn) X.CarteBord else X.Texte2,
                Offset(x - space * 0.9f, top), Offset(x - space * 0.9f, top + 4 * space), lineW * 1.3f,
            )
        }

        // Zone « maintenant »
        drawRect(
            X.Jaune.copy(alpha = 0.12f),
            Offset(playX - 14.dp.toPx(), top - 3 * space),
            Size(28.dp.toPx(), 10 * space),
        )
        drawLine(X.Jaune, Offset(playX, top - 3 * space), Offset(playX, top + 7 * space), 2.dp.toPx())

        for (n in session.notes) {
            val x = xAt(session.expectedNanos(n))
            if (x < -30f || x > size.width + 30f) continue
            val color = X.judgement(n.result)
            val y = yAt(n.lane.staffPos)
            drawNote(n.lane, x, y, space, color)
            if (n.accent) {
                // Signe d'accent « > » au-dessus de la hampe
                val ay = maxOf(y - 3.3f * space, 0.5f * space)
                val aw = space * 0.55f
                val ah = space * 0.32f
                drawLine(X.Jaune, Offset(x - aw, ay - ah), Offset(x + aw, ay), lineW * 1.4f)
                drawLine(X.Jaune, Offset(x + aw, ay), Offset(x - aw, ay + ah), lineW * 1.4f)
            }
            if (n.sticking != null) {
                textPaint.color = X.Texte2.toArgb()
                textPaint.textSize = 13.dp.toPx()
                val row = if (n.lane == Lane.SNARE || n.lane == Lane.TOM) 1 else 0
                val ty = top + 4 * space + 4.8f * space + row * 1.3f * space
                drawIntoCanvas { it.nativeCanvas.drawText(n.sticking.toString(), x, ty, textPaint) }
            }
        }
    }
}


private fun DrawScope.drawNote(lane: Lane, x: Float, y: Float, space: Float, color: androidx.compose.ui.graphics.Color) {
    val w = space * 1.3f
    val h = space * 0.95f
    val stroke = 2.dp.toPx()
    if (lane == Lane.CRASH) {
        drawLine(color.copy(alpha = 0.7f), Offset(x - w, y), Offset(x + w, y), stroke * 0.7f)
    }

    if (lane.cross) {
        val r = space * 0.45f
        drawLine(color, Offset(x - r, y - r), Offset(x + r, y + r), stroke)
        drawLine(color, Offset(x - r, y + r), Offset(x + r, y - r), stroke)
    } else {
        drawOval(color, Offset(x - w / 2, y - h / 2), Size(w, h))
    }
    // Hampes : vers le haut pour les mains, vers le bas pour le pied
    if (lane.foot) {
        val sx = if (lane.cross) x - space * 0.45f else x - w / 2 + stroke / 2
        drawLine(color, Offset(sx, y), Offset(sx, y + 2.6f * space), stroke * 0.8f)
    } else {
        val sx = if (lane.cross) x + space * 0.45f else x + w / 2 - stroke / 2
        drawLine(color, Offset(sx, y), Offset(sx, y - 2.6f * space), stroke * 0.8f)
    }
}
