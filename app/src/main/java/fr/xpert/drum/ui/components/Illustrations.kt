package fr.xpert.drum.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import fr.xpert.drum.content.Illus
import fr.xpert.drum.ui.theme.X
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/** Une illustration de leçon : schéma vectoriel 16:9, aux couleurs XPert. */
@Composable
fun Illustration(illus: Illus, modifier: Modifier = Modifier) {
    val tm = rememberTextMeasurer()
    Canvas(
        modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
    ) {
        Sketch(this, tm).draw(illus)
    }
}

// Palette des schémas
private val W = X.Texte
private val M = X.Texte2
private val Y = X.Jaune
private val O = X.Orange
private val OT = X.OrangeTexte
private val G = X.Vert
private val R = X.Rouge
private val WOOD = Color(0xFFE6C08A)
private val SHELL = Color(0xFF3B3E8C)
private val HEAD = Color(0xFFDADBF0)
private val CYM = Color(0xFFD9B44A)
private val SKIN = Color(0xFFF1C7A5)
private val SKIN_L = Color(0xFFF8DCC4)
private val SKIN_T = Color(0xFFEAB995)
private val SKIN_D = Color(0xFFD9A07E)
private val SKIN_DD = Color(0xFFB97E5E)
private val SKIN_E = Color(0xFF8E5A40)
private val NAIL = Color(0xFFF9E3D6)
private val HAIR = Color(0xFF5B3A26)
private val SHIRT = Color(0xFFE8501E)
private val SHIRT_D = Color(0xFFC63F14)
private val PANTS = Color(0xFF7A80C4)
private val PANTS_D = Color(0xFF555A9E)
private val SHOE = Color(0xFFE9E9F2)
private val SOLE = Color(0xFF8A8FC9)
private val DIM = Color(0xFF34357A)

/**
 * Petit outil de dessin en coordonnées virtuelles 160 × 90 (sol à y = 84).
 */
private class Sketch(val d: DrawScope, val tm: TextMeasurer) {
    val u = d.size.width / 160f

    fun o(x: Float, y: Float) = Offset(x * u, y * u)

    fun line(x1: Float, y1: Float, x2: Float, y2: Float, c: Color = W, w: Float = 1.6f, dash: Boolean = false) {
        d.drawLine(
            c, o(x1, y1), o(x2, y2), w * u, StrokeCap.Round,
            pathEffect = if (dash) PathEffect.dashPathEffect(floatArrayOf(2.5f * u, 2f * u)) else null,
        )
    }

    fun circle(x: Float, y: Float, r: Float, c: Color = W, fill: Boolean = false, w: Float = 1.4f) {
        if (fill) d.drawCircle(c, r * u, o(x, y)) else d.drawCircle(c, r * u, o(x, y), style = Stroke(w * u))
    }

    fun oval(x: Float, y: Float, rx: Float, ry: Float, c: Color = W, fill: Boolean = false, w: Float = 1.4f) {
        val tl = o(x - rx, y - ry)
        val sz = Size(2 * rx * u, 2 * ry * u)
        if (fill) d.drawOval(c, tl, sz) else d.drawOval(c, tl, sz, style = Stroke(w * u))
    }

    fun rect(x: Float, y: Float, w: Float, h: Float, c: Color = W, fill: Boolean = false, r: Float = 1f, sw: Float = 1.4f) {
        val tl = o(x, y)
        val sz = Size(w * u, h * u)
        val cr = CornerRadius(r * u, r * u)
        if (fill) d.drawRoundRect(c, tl, sz, cr) else d.drawRoundRect(c, tl, sz, cr, style = Stroke(sw * u))
    }

    fun poly(vararg pts: Float, c: Color = W, w: Float = 1.6f, fill: Boolean = false, close: Boolean = false) {
        val p = Path()
        p.moveTo(pts[0] * u, pts[1] * u)
        var i = 2
        while (i < pts.size) {
            p.lineTo(pts[i] * u, pts[i + 1] * u); i += 2
        }
        if (close || fill) p.close()
        if (fill) d.drawPath(p, c) else d.drawPath(p, c, style = Stroke(w * u, cap = StrokeCap.Round))
    }

    /** Arc de cercle (degrés, 0 = droite, sens horaire). */
    fun arc(x: Float, y: Float, r: Float, start: Float, sweep: Float, c: Color = Y, w: Float = 1.4f, arrowEnd: Boolean = false) {
        d.drawArc(c, start, sweep, false, o(x - r, y - r), Size(2 * r * u, 2 * r * u), style = Stroke(w * u, cap = StrokeCap.Round))
        if (arrowEnd) {
            val a = (start + sweep) * PI.toFloat() / 180f
            val ex = x + r * cos(a)
            val ey = y + r * sin(a)
            val dir = a + (if (sweep > 0) PI.toFloat() / 2 else -PI.toFloat() / 2)
            head(ex, ey, dir, c, w)
        }
    }

    private fun head(x: Float, y: Float, dir: Float, c: Color, w: Float) {
        val l = 3.2f
        line(x, y, x - l * cos(dir - 0.5f), y - l * sin(dir - 0.5f), c, w)
        line(x, y, x - l * cos(dir + 0.5f), y - l * sin(dir + 0.5f), c, w)
    }

    fun arrow(x1: Float, y1: Float, x2: Float, y2: Float, c: Color = Y, w: Float = 1.4f, both: Boolean = false) {
        line(x1, y1, x2, y2, c, w)
        val a = atan2(y2 - y1, x2 - x1)
        head(x2, y2, a, c, w)
        if (both) head(x1, y1, a + PI.toFloat(), c, w)
    }

    fun text(s: String, x: Float, y: Float, c: Color = W, size: Float = 6f, bold: Boolean = false, align: Int = 0) {
        val style = TextStyle(
            color = c,
            fontSize = with(d) { (size * u).toSp() },
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
        )
        val layout = tm.measure(s, style)
        val w = layout.size.width
        val h = layout.size.height
        val left = when (align) {
            0 -> x * u - w / 2f
            1 -> x * u
            else -> x * u - w
        }
        d.drawText(layout, topLeft = Offset(left, y * u - h / 2f))
    }

    fun check(x: Float, y: Float) {
        circle(x, y, 5f, G, fill = true)
        poly(x - 2.4f, y + 0.2f, x - 0.6f, y + 2f, x + 2.6f, y - 1.8f, c = X.Fond, w = 1.5f)
    }

    fun cross(x: Float, y: Float) {
        circle(x, y, 5f, R, fill = true)
        line(x - 2f, y - 2f, x + 2f, y + 2f, X.Fond, 1.5f)
        line(x - 2f, y + 2f, x + 2f, y - 2f, X.Fond, 1.5f)
    }

    fun floor() = line(4f, 84f, 156f, 84f, DIM, 1.2f)

    /** Baguette de (x1,y1) [talon] à (x2,y2) [olive]. */
    fun stick(x1: Float, y1: Float, x2: Float, y2: Float, w: Float = 2.4f, c: Color = WOOD) {
        line(x1, y1, x2, y2, c, w)
        val a = atan2(y2 - y1, x2 - x1)
        oval(x2 + cos(a) * 0.6f, y2 + sin(a) * 0.6f, w * 0.65f, w * 0.65f, c, fill = true)
    }

    // ------------------------------------------------------------------ Formes « réalistes »

    /** Segment de membre effilé de a (largeur wa) à b (largeur wb), extrémités arrondies. */
    fun limb(ax: Float, ay: Float, bx: Float, by: Float, wa: Float, wb: Float, c: Color) {
        val dx = bx - ax
        val dy = by - ay
        val len = kotlin.math.sqrt(dx * dx + dy * dy).coerceAtLeast(0.01f)
        val nx = -dy / len
        val ny = dx / len
        val p = Path()
        p.moveTo((ax + nx * wa / 2) * u, (ay + ny * wa / 2) * u)
        p.lineTo((bx + nx * wb / 2) * u, (by + ny * wb / 2) * u)
        p.lineTo((bx - nx * wb / 2) * u, (by - ny * wb / 2) * u)
        p.lineTo((ax - nx * wa / 2) * u, (ay - ny * wa / 2) * u)
        p.close()
        d.drawPath(p, c)
        d.drawCircle(c, wa / 2 * u, o(ax, ay))
        d.drawCircle(c, wb / 2 * u, o(bx, by))
    }

    /** Membre avec un fin contour plus foncé (doigts, pouce) pour bien les distinguer. */
    fun flimb(ax: Float, ay: Float, bx: Float, by: Float, wa: Float, wb: Float, c: Color, edge: Color = SKIN_DD) {
        limb(ax, ay, bx, by, wa + 0.9f, wb + 0.9f, edge)
        limb(ax, ay, bx, by, wa, wb, c)
    }

    /** Membre avec contour de mise en évidence (jaune) quand [hl]. */
    fun limbHl(ax: Float, ay: Float, bx: Float, by: Float, wa: Float, wb: Float, c: Color, hl: Boolean) {
        if (hl) limb(ax, ay, bx, by, wa + 2.4f, wb + 2.4f, Y)
        limb(ax, ay, bx, by, wa, wb, c)
    }

    /** Forme pleine passant par des points (lissée par des courbes quadratiques). */
    fun blob(c: Color, vararg pts: Float) = blobE(c, null, *pts)

    fun blobE(c: Color, edge: Color?, vararg pts: Float) {
        val n = pts.size / 2
        val p = Path()
        fun mx(i: Int) = (pts[(2 * i) % pts.size] + pts[(2 * i + 2) % pts.size]) / 2f
        fun my(i: Int) = (pts[(2 * i + 1) % pts.size] + pts[(2 * i + 3) % pts.size]) / 2f
        p.moveTo(mx(0) * u, my(0) * u)
        for (k in 1..n) {
            val i = k % n
            p.quadraticBezierTo(pts[2 * i] * u, pts[2 * i + 1] * u, mx(i) * u, my(i) * u)
        }
        p.close()
        d.drawPath(p, c)
        if (edge != null) d.drawPath(p, edge, style = Stroke(0.7f * u))
    }

    /** Tête de profil tournée vers la droite (cheveux, oreille, nez, œil). */
    fun headSide(hx: Float, hy: Float, r: Float = 6f) {
        circle(hx - r * 0.12f, hy - r * 0.1f, r * 1.02f, HAIR, fill = true)
        blob(
            SKIN,
            hx - r * 0.35f, hy - r * 0.45f,
            hx + r * 0.75f, hy - r * 0.75f,
            hx + r * 1.02f, hy - r * 0.05f,
            hx + r * 0.95f, hy + r * 0.7f,
            hx + r * 0.2f, hy + r * 1.08f,
            hx - r * 0.55f, hy + r * 0.6f,
        )
        // nez et menton
        poly(hx + r * 0.95f, hy - r * 0.2f, hx + r * 1.25f, hy + r * 0.25f, hx + r * 0.95f, hy + r * 0.32f, c = SKIN, fill = true)
        // mèche de cheveux sur le front
        blob(HAIR, hx - r * 0.9f, hy - r * 0.6f, hx + r * 0.2f, hy - r * 1.15f, hx + r * 0.9f, hy - r * 0.7f, hx + r * 0.1f, hy - r * 0.45f)
        oval(hx - r * 0.15f, hy + r * 0.15f, r * 0.2f, r * 0.3f, SKIN_D, fill = true) // oreille
        circle(hx + r * 0.6f, hy - r * 0.12f, r * 0.11f, X.Fond, fill = true) // œil
    }

    /** Tête de face. */
    fun headFront(hx: Float, hy: Float, r: Float = 6.5f) {
        circle(hx, hy - r * 0.08f, r * 1.04f, HAIR, fill = true)
        oval(hx - r * 0.98f, hy + r * 0.15f, r * 0.2f, r * 0.32f, SKIN_D, fill = true)
        oval(hx + r * 0.98f, hy + r * 0.15f, r * 0.2f, r * 0.32f, SKIN_D, fill = true)
        oval(hx, hy + r * 0.15f, r * 0.88f, r * 0.98f, SKIN, fill = true)
        blob(HAIR, hx - r * 0.95f, hy - r * 0.2f, hx - r * 0.6f, hy - r * 1.1f, hx + r * 0.7f, hy - r * 1.1f, hx + r * 0.95f, hy - r * 0.2f, hx, hy - r * 0.55f)
        circle(hx - r * 0.33f, hy + r * 0.1f, r * 0.1f, X.Fond, fill = true)
        circle(hx + r * 0.33f, hy + r * 0.1f, r * 0.1f, X.Fond, fill = true)
        line(hx - r * 0.25f, hy + r * 0.62f, hx + r * 0.25f, hy + r * 0.62f, SKIN_D, 0.6f)
    }

    /** Petit poing qui tient la baguette, vu de côté (pouce dessus, doigts enroulés dessous). */
    fun fistSide(hx: Float, hy: Float, angleDeg: Float, s: Float = 1f) {
        d.rotate(angleDeg, o(hx, hy)) {
            // doigts repliés sous la baguette
            blob(SKIN_D, hx - 3.2f * s, hy - 0.5f * s, hx + 1.8f * s, hy - 0.8f * s, hx + 2.6f * s, hy + 2.6f * s, hx - 2.6f * s, hy + 3f * s)
            for (k in 0..2) line(hx - 2f * s + k * 1.4f * s, hy + 0.6f * s, hx - 2.2f * s + k * 1.4f * s, hy + 2.6f * s, SKIN_DD, 0.35f * s)
            // dos de la main
            blob(SKIN, hx - 4.4f * s, hy - 1.6f * s, hx - 1f * s, hy - 3.6f * s, hx + 2.2f * s, hy - 2.4f * s, hx + 1.6f * s, hy + 0.4f * s, hx - 3.6f * s, hy + 1f * s)
            // pouce posé le long de la baguette
            limb(hx - 2.6f * s, hy - 1.6f * s, hx + 2.6f * s, hy - 0.9f * s, 1.5f * s, 1.1f * s, SKIN_T)
        }
    }

    // ------------------------------------------------------------------ Batteur de profil

    class Pose(
        val hip: Offset = Offset(46f, 50f),
        val shoulder: Offset = Offset(48f, 24f),
        val elbow: Offset = Offset(53f, 39f),
        val hand: Offset = Offset(70f, 41f),
        val stickTip: Offset = Offset(88f, 47f),
        val knee: Offset = Offset(71f, 54f),
        val ankle: Offset = Offset(76f, 76f),
        val toe: Offset = Offset(88f, 78f),
        val heelUp: Boolean = false,
    )

    fun throne(seatHighlight: Boolean = false) {
        rect(31f, 51f, 26f, 5f, Color(0xFF2A2A40), fill = true, r = 2.5f)
        rect(31f, 51f, 26f, 2f, Color(0xFF4A4A6A), fill = true, r = 1f)
        if (seatHighlight) rect(44f, 50.5f, 13f, 6f, O, fill = true, r = 2.5f)
        line(44f, 56f, 44f, 80f, M, 1.8f)
        line(44f, 80f, 35f, 84f, M, 1.4f)
        line(44f, 80f, 53f, 84f, M, 1.4f)
    }

    fun snareSide(x: Float = 80f, top: Float = 47f, highlight: Boolean = false) {
        rect(x, top, 26f, 7f, if (highlight) O else SHELL, fill = true, r = 1f)
        rect(x, top + 0.8f, 26f, 1.2f, Color(0xFFB8BCE0).copy(alpha = 0.5f), fill = true, r = 0.5f)
        rect(x, top + 5f, 26f, 1.2f, Color(0xFFB8BCE0).copy(alpha = 0.5f), fill = true, r = 0.5f)
        line(x - 0.5f, top, x + 26.5f, top, HEAD, 1.6f)
        line(x + 13f, top + 7f, x + 13f, 80f, M, 1.2f)
        line(x + 13f, 80f, x + 6f, 84f, M, 1.2f)
        line(x + 13f, 80f, x + 20f, 84f, M, 1.2f)
    }

    fun kickSide(cx: Float = 136f, cy: Float = 65f, highlight: Boolean = false, r: Float = 19f) {
        circle(cx, cy, r, if (highlight) O else SHELL, fill = true)
        circle(cx, cy, r, HEAD, w = 1.6f)
        circle(cx, cy, r * 0.7f, DIM, w = 1f)
    }

    fun pedal(heelUp: Boolean = false) {
        // semelle de pédale inclinée
        poly(74f, 82f, 100f, 78f, c = M, w = 1.8f)
        line(100f, 78f, 112f, 64f, M, 1.2f)
        // mailloche
        circle(113.5f, 62.5f, 2.2f, HEAD, fill = true)
    }

    /** Chaussure de profil : talon en (hx,hy), pointe en (tx,ty). */
    fun shoe(hx: Float, hy: Float, tx: Float, ty: Float) {
        val dx = tx - hx
        val dy = ty - hy
        val len = kotlin.math.sqrt(dx * dx + dy * dy)
        val nx = dy / len
        val ny = -dx / len // normale vers le haut
        blob(
            SHOE,
            hx - dx * 0.06f, hy - dy * 0.06f,
            hx + nx * 6.6f, hy + ny * 6.6f,
            hx + dx * 0.32f + nx * 7.4f, hy + dy * 0.32f + ny * 7.4f,
            hx + dx * 0.66f + nx * 4.4f, hy + dy * 0.66f + ny * 4.4f,
            tx + nx * 2.8f, ty + ny * 2.8f,
            tx + dx * 0.04f, ty + dy * 0.04f,
        )
        line(hx - dx * 0.02f, hy - dy * 0.02f, tx, ty, SOLE, 1.3f)
    }

    /**
     * Batteur de profil (tourné vers la droite), en formes pleines : jambe, chaussure,
     * buste, tête, bras ; [armHl], [thighHl], [backHl] entourent la partie à regarder.
     */
    fun drummer(p: Pose = Pose(), armHl: Boolean = false, thighHl: Boolean = false, backHl: Boolean = false) {
        // bras gauche (derrière, plus sombre)
        limb(p.shoulder.x - 1f, p.shoulder.y + 1f, p.elbow.x + 1f, p.elbow.y + 1f, 5f, 4.2f, SHIRT_D)
        limb(p.elbow.x + 1f, p.elbow.y + 1f, p.hand.x + 3f, p.hand.y - 1f, 3.8f, 2.8f, SKIN_D)
        stick(p.hand.x + 2f, p.hand.y - 1.5f, p.stickTip.x + 4f, p.stickTip.y - 3f, 1.3f, WOOD.copy(alpha = 0.7f))
        // jambe
        limbHl(p.hip.x - 1f, p.hip.y, p.knee.x, p.knee.y, 8f, 5.6f, PANTS, thighHl)
        limb(p.knee.x, p.knee.y, p.ankle.x, p.ankle.y, 5.4f, 3.8f, PANTS)
        shoe(p.ankle.x - 1f, p.ankle.y + 1f, p.toe.x + 2f, p.toe.y)
        // buste (dos et torse) et cou
        val bx = p.shoulder.x - p.hip.x
        val by = p.shoulder.y - p.hip.y
        val bl = kotlin.math.sqrt(bx * bx + by * by)
        val fx = -by / bl // vers l'avant
        val fy = bx / bl
        val f = if (fx < 0) -1f else 1f
        if (backHl) limb(p.hip.x - 2.5f, p.hip.y - 1f, p.shoulder.x - 2f, p.shoulder.y + 1f, 4.6f, 4.6f, Y)
        limb(p.shoulder.x + 2f, p.shoulder.y - 2f, p.shoulder.x + 3f, p.shoulder.y - 6f, 3.6f, 3.4f, SKIN_D)
        blob(
            SHIRT,
            p.hip.x - 3.5f, p.hip.y + 1f,
            p.hip.x - 3.2f + bx * 0.5f, p.hip.y + by * 0.5f,
            p.shoulder.x - 2.5f, p.shoulder.y - 1f,
            p.shoulder.x + 2f, p.shoulder.y - 3.4f,
            p.shoulder.x + f * fx * 9f, p.shoulder.y + f * fy * 9f + 3f,
            p.hip.x + bx * 0.45f + f * fx * 9.5f, p.hip.y + by * 0.45f + f * fy * 9.5f,
            p.hip.x + f * fx * 7f, p.hip.y + 1.5f,
        )
        // ceinture
        limb(p.hip.x - 3f, p.hip.y + 0.5f, p.hip.x + f * fx * 7f, p.hip.y + 1f, 1.6f, 1.6f, PANTS_D)
        headSide(p.shoulder.x + 4f, p.shoulder.y - 10f)
        // bras droit (devant)
        limbHl(p.shoulder.x, p.shoulder.y + 1f, p.elbow.x, p.elbow.y, 5.6f, 4.6f, SHIRT_D, armHl)
        limbHl(p.elbow.x, p.elbow.y, p.hand.x, p.hand.y, 4.2f, 3f, SKIN, armHl)
        stick(p.hand.x - 3f, p.hand.y - 0.3f, p.stickTip.x, p.stickTip.y, 1.6f)
        val ang = Math.toDegrees(atan2(p.stickTip.y - p.hand.y, p.stickTip.x - p.hand.x).toDouble()).toFloat()
        fistSide(p.hand.x, p.hand.y, ang, 0.9f)
    }

    fun sideScene(
        seatHighlight: Boolean = false,
        snareHighlight: Boolean = false,
        kickHighlight: Boolean = false,
        pose: Pose = Pose(),
        armColor: Color = W,
        thighColor: Color = W,
        backColor: Color = W,
    ) {
        floor()
        kickSide(highlight = kickHighlight)
        pedal()
        throne(seatHighlight)
        snareSide(highlight = snareHighlight)
        drummer(pose, armHl = armColor != W, thighHl = thighColor != W, backHl = backColor != W)
    }

    // ------------------------------------------------------------------ Batteur de face

    fun frontFigure(cx: Float, elbowsOut: Boolean, shouldersUp: Boolean = false, c: Color = W) {
        val sy = if (shouldersUp) 23f else 27f
        // tabouret et jambes (cuisses vues de face, en raccourci)
        // cou et tête
        limb(cx, sy - 2f, cx, sy - 7f, 4.6f, 4.2f, SKIN_D)
        headFront(cx, if (shouldersUp) 12.5f else 13.5f)
        // buste : épaules arrondies, taille plus fine
        blob(
            SHIRT,
            cx - 13.5f, sy + 2f,
            cx - 11f, sy - 1.5f,
            cx, sy - 2.5f,
            cx + 11f, sy - 1.5f,
            cx + 13.5f, sy + 2f,
            cx + 10f, sy + 18f,
            cx + 8.5f, 63f,
            cx - 8.5f, 63f,
            cx - 10f, sy + 18f,
        )
        if (shouldersUp) {
            // trapèzes contractés
            limb(cx - 4f, sy - 1.5f, cx - 11f, sy + 0.5f, 3.4f, 4f, SHIRT_D)
            limb(cx + 4f, sy - 1.5f, cx + 11f, sy + 0.5f, 3.4f, 4f, SHIRT_D)
        }
        // bras
        val ex = if (elbowsOut) 25f else 13.5f
        val ey = if (elbowsOut) 40f else 45f
        for (sgn in listOf(-1f, 1f)) {
            limb(cx + sgn * 12f, sy + 1.5f, cx + sgn * ex, ey, 5.4f, 4.6f, SHIRT_D)
            limb(cx + sgn * ex, ey, cx + sgn * 6.5f, 56f, 4.2f, 3.2f, SKIN)
            stick(cx + sgn * 6.5f, 54f, cx + sgn * 1.5f, 66f, 1.4f)
            oval(cx + sgn * 6.3f, 56.3f, 2.4f, 2.1f, SKIN, fill = true)
            oval(cx + sgn * 5.6f, 57.8f, 1.8f, 1.1f, SKIN_D, fill = true)
        }
        // caisse claire vue de face
        rect(cx - 13f, 66f, 26f, 6f, SHELL, fill = true)
        line(cx - 13.5f, 66f, cx + 13.5f, 66f, HEAD, 1.4f)
    }

    // ------------------------------------------------------------------ Batterie vue de dessus

    enum class Part { NONE, KICK, SNARE, HIHAT, HIHAT_FOOT, TOMS, CRASH, RIDE, ALL }

    fun kitTop(hl: Part, ox: Float = 0f, oy: Float = 0f, s: Float = 1f, mirror: Boolean = false, labels: Boolean = true) {
        fun cym(cx: Float, cy: Float, r: Float, on: Boolean) {
            circle(cx, cy, r, if (on) O else CYM.copy(alpha = 0.28f), fill = true)
            circle(cx, cy, r, if (on) O else CYM, w = 1.2f * s)
            if (!labels) circle(cx, cy, 1.3f * s, W, fill = true)
        }
        fun X(x: Float) = ox + (if (mirror) 160f - x else x) * s
        fun Yc(y: Float) = oy + y * s
        fun col(p: Part) = if (hl == p || hl == Part.ALL) O else SHELL
        fun lab(p: Part) = if (hl == p || hl == Part.ALL) W else W.copy(alpha = 0.75f)
        // tabouret
        circle(X(80f), Yc(80f), 7f * s, M, fill = true)
        // grosse caisse (vue de dessus = rectangle)
        rect(X(if (mirror) 92f else 68f), Yc(18f), 24f * s, 26f * s, col(Part.KICK), fill = true, r = 2f)
        line(X(68f), Yc(18f), X(92f), Yc(18f), HEAD, 1.4f * s)
        line(X(68f), Yc(44f), X(92f), Yc(44f), HEAD, 1.4f * s)
        // pédale de grosse caisse
        rect(X(if (mirror) 83f else 77f), Yc(46f), 6f * s, 12f * s, M, fill = true, r = 1f)
        // toms
        circle(X(72f), Yc(24f), 7f * s, col(Part.TOMS), fill = true)
        circle(X(88f), Yc(24f), 7f * s, col(Part.TOMS), fill = true)
        circle(X(110f), Yc(60f), 11f * s, col(Part.TOMS), fill = true)
        circle(X(72f), Yc(24f), 7f * s, HEAD, w = 1f)
        circle(X(88f), Yc(24f), 7f * s, HEAD, w = 1f)
        circle(X(110f), Yc(60f), 11f * s, HEAD, w = 1f)
        // caisse claire
        circle(X(80f), Yc(62f), 9f * s, col(Part.SNARE), fill = true)
        circle(X(80f), Yc(62f), 9f * s, HEAD, w = 1.2f)
        // charleston + pédale
        cym(X(56f), Yc(52f), 10f * s, hl == Part.HIHAT || hl == Part.ALL)
        rect(X(if (mirror) 64f else 58f), Yc(66f), 6f * s, 12f * s, if (hl == Part.HIHAT_FOOT) O else M, fill = true, r = 1f)
        // crash et ride
        cym(X(50f), Yc(20f), 13f * s, hl == Part.CRASH || hl == Part.ALL)
        cym(X(114f), Yc(28f), 15f * s, hl == Part.RIDE || hl == Part.ALL)
        if (labels) {
            val ts = 5f * s
            text("Crash", X(50f), Yc(20f), lab(Part.CRASH), ts, bold = hl == Part.CRASH)
            text("Ride", X(114f), Yc(28f), lab(Part.RIDE), ts, bold = hl == Part.RIDE)
            text("GC", X(80f), Yc(36f), lab(Part.KICK), ts, bold = hl == Part.KICK)
            text("Toms", X(80f), Yc(12f), lab(Part.TOMS), ts, bold = hl == Part.TOMS)
            text("CC", X(80f), Yc(62f), lab(Part.SNARE), ts, bold = hl == Part.SNARE)
            text("HH", X(56f), Yc(52f), lab(Part.HIHAT), ts, bold = hl == Part.HIHAT)
            text("Tom basse", X(110f), Yc(76.5f), lab(Part.TOMS), ts * 0.85f, bold = hl == Part.TOMS)
            if (hl == Part.HIHAT_FOOT) text("pédale", X(54f), Yc(72f), W, ts, bold = true, align = if (mirror) 1 else 2)
        }
    }

    // ------------------------------------------------------------------ Main et baguette

    /**
     * Gros plan d'une main droite qui tient la baguette, vue de côté (côté pouce).
     * Le point d'appui (pouce / index) est en (px, py) ; la baguette part vers la droite.
     * [butt] = longueur qui dépasse derrière, [tip] = longueur devant, [s] = échelle.
     */
    fun grip(
        px: Float, py: Float, s: Float = 1f,
        tight: Boolean = false,
        butt: Float = 34f, tip: Float = 80f,
        angleDeg: Float = 0f,
        fulcrum: Boolean = false,
        forearm: Boolean = true,
    ) {
        fun X(dx: Float) = px + dx * s
        fun Yy(dy: Float) = py + dy * s
        d.rotate(angleDeg, o(px, py)) {
            // avant-bras
            if (forearm) {
                limb(X(-64f), Yy(-1f), X(-22f), Yy(-6f), 15f * s, 12.5f * s, SKIN_D)
                limb(X(-64f), Yy(-4.5f), X(-24f), Yy(-9f), 5f * s, 3f * s, SKIN.copy(alpha = 0.55f))
            }
            // majeur, annulaire, auriculaire : derrière la baguette, enroulés dessous
            val curl = if (tight) 0.75f else 1f
            for (k in 2 downTo 0) {
                val bx = -5.5f - 4.6f * k
                val by = -4f + 0.9f * k
                val c = if (k == 0) SKIN_D else SKIN_DD
                flimb(X(bx), Yy(by), X(bx + 5.4f * curl), Yy(3.2f), 4.8f * s, 4.3f * s, c, SKIN_E)
                flimb(X(bx + 5.4f * curl), Yy(3.2f), X(bx + 3.2f * curl), Yy(3.2f + 5.2f * curl), 4.3f * s, 3.9f * s, c, SKIN_E)
                flimb(X(bx + 3.2f * curl), Yy(3.2f + 5.2f * curl), X(bx - 1.2f), Yy(3.6f + 6.2f * curl), 3.9f * s, 3.4f * s, c, SKIN_E)
            }
            // baguette
            stick(X(-butt), Yy(0f), X(tip), Yy(0f), 3.4f * s)
            // dos de la main
            blobE(
                SKIN, SKIN_DD,
                X(-27f), Yy(-10.5f),
                X(-13f), Yy(-12.5f),
                X(-3f), Yy(-11.5f),
                X(2.5f), Yy(-6.5f),
                X(-3f), Yy(-1.6f),
                X(-16f), Yy(2.5f),
                X(-27f), Yy(2.5f),
            )
            // reflets des tendons
            for (k in 0..2) line(X(-22f + k * 1.2f), Yy(-9.5f + k * 1.6f), X(-6f + k * 0.8f), Yy(-11f + k * 2f), SKIN_L, 0.5f * s)
            // index : enroulé autour de la baguette (2e phalange sous la baguette)
            flimb(X(3.8f), Yy(5.6f), X(-1.5f), Yy(7.2f), 4.5f * s, 3.9f * s, SKIN_T)
            flimb(X(4.6f), Yy(-0.8f), X(3.8f), Yy(5.6f), 4.9f * s, 4.5f * s, SKIN)
            flimb(X(-3f), Yy(-6.5f), X(4.6f), Yy(-0.8f), 5.4f * s, 4.9f * s, SKIN)
            line(X(2.6f), Yy(-2.4f), X(6.2f), Yy(-0.6f), SKIN_DD, 0.45f * s)
            line(X(2f), Yy(4.6f), X(5.4f), Yy(5.4f), SKIN_DD, 0.45f * s)
            // pouce : à plat le long de la baguette
            flimb(X(-17f), Yy(-8.5f), X(-6f), Yy(-4f), 7f * s, 5.8f * s, SKIN_T)
            flimb(X(-6f), Yy(-4f), X(3.2f), Yy(-2.6f), 5.6f * s, 4.6f * s, SKIN_T)
            oval(X(2f), Yy(-3.6f), 2f * s, 1.1f * s, NAIL, fill = true)
            line(X(-6.5f), Yy(-5.6f), X(-5.2f), Yy(-2.6f), SKIN_DD, 0.45f * s)
            if (tight) {
                // phalanges blanchies + traits de tension
                circle(X(4.6f), Yy(-0.8f), 1.2f * s, Color.White.copy(alpha = 0.6f), fill = true)
                circle(X(-3f), Yy(-6.5f), 1.2f * s, Color.White.copy(alpha = 0.6f), fill = true)
                for (k in 0..2) {
                    val a = (-140f + k * 30f) * PI.toFloat() / 180f
                    line(X(-6f + 15f * cos(a)), Yy(-6f + 15f * sin(a)), X(-6f + 19f * cos(a)), Yy(-6f + 19f * sin(a)), R, 1.1f)
                }
            }
            if (fulcrum) {
                circle(X(3f), Yy(0f), 3.6f * s, Y, w = 1.3f)
            }
        }
    }

    /**
     * Poing vu de l'avant (comme si la baguette venait vers toi) : sert à montrer
     * l'orientation de la main (allemande, américaine, française). 0° = dos de la main vers le haut.
     */
    fun fistFront(cx: Float, cy: Float, angleDeg: Float, s: Float = 1f) {
        d.rotate(angleDeg, o(cx, cy)) {
            // avant-bras en arrière-plan
            oval(cx, cy + 2f * s, 9f * s, 7f * s, SKIN_DD, fill = true)
            // dos de la main (vu de face : bande des articulations)
            blob(SKIN, cx - 11f * s, cy - 3f * s, cx - 9f * s, cy - 8f * s, cx + 9f * s, cy - 8f * s, cx + 11f * s, cy - 3f * s, cx + 10f * s, cy + 3f * s, cx - 10f * s, cy + 3f * s)
            // 4 doigts repliés (vus du bout)
            for (k in 0..3) {
                val fx = cx + (-6.6f + k * 4.4f) * s
                oval(fx, cy + 3.6f * s, 2.3f * s, 3.2f * s, SKIN_D, fill = true)
                line(fx - 1.2f * s, cy + 3.4f * s, fx + 1.2f * s, cy + 3.4f * s, SKIN_DD, 0.4f * s)
            }
            // pouce sur le côté intérieur
            limb(cx - 10f * s, cy + 1f * s, cx - 6f * s, cy + 6.5f * s, 4.6f * s, 3.8f * s, SKIN_T)
        }
    }

    // ------------------------------------------------------------------ Portée

    fun staff(x1: Float, x2: Float, top: Float, sp: Float = 5f) {
        for (i in 0..4) line(x1, top + i * sp, x2, top + i * sp, M, 0.8f)
    }

    /** Note sur la portée : pos en demi-interlignes depuis la ligne du haut. */
    fun note(x: Float, top: Float, pos: Int, cross: Boolean, stemUp: Boolean = true, sp: Float = 5f, c: Color = W, stemLen: Float = 3.2f) {
        val y = top + pos * sp / 2f
        if (cross) {
            val r = sp * 0.42f
            line(x - r, y - r, x + r, y + r, c, 1.3f)
            line(x - r, y + r, x + r, y - r, c, 1.3f)
        } else {
            d.rotate(-20f, o(x, y)) { oval(x, y, sp * 0.62f, sp * 0.45f, c, fill = true) }
        }
        val sx = if (stemUp) x + sp * 0.55f else x - sp * 0.55f
        val ey = if (stemUp) y - stemLen * sp else y + stemLen * sp
        line(sx, y, sx, ey, c, 0.9f)
    }

    fun beam(x1: Float, x2: Float, y: Float, count: Int, sp: Float = 5f, c: Color = W) {
        for (k in 0 until count) {
            val yy = y + k * sp * 0.55f
            d.drawLine(c, o(x1, yy), o(x2, yy), sp * 0.32f * u)
        }
    }

    // ================================================================== Dessins

    fun draw(i: Illus) {
        when (i) {
            Illus.SEAT_HEIGHT -> {
                sideScene(thighColor = Y)
                line(46f, 50f, 78f, 50f, M, 1f, dash = true)
                text("hanches", 30f, 46f, M, 5f)
                text("cuisses ≈ horizontales,", 118f, 8f, Y, 5.6f, bold = true)
                text("genoux un peu plus bas", 118f, 15f, Y, 5.6f, bold = true)
                arrow(98f, 18f, 74f, 50f, Y)
            }
            Illus.SEAT_FRONT -> {
                sideScene(seatHighlight = true)
                text("assis sur la", 4f, 26f, OT, 5.4f, bold = true, align = 1)
                text("moitié avant", 4f, 33f, OT, 5.4f, bold = true, align = 1)
                arrow(20f, 37f, 48f, 51f, OT, 1.1f)
                text("pas au fond", 4f, 66f, M, 5f, align = 1)
                arrow(14f, 62f, 33f, 55f, M, 1f)
            }
            Illus.KNEE_ANGLE -> {
                sideScene()
                arc(71f, 54f, 9f, 95f, 95f, Y, 1.6f)
                text("90° à 110°", 128f, 12f, Y, 6.5f, bold = true)
                arrow(116f, 16f, 80f, 52f, Y)
                text("pied à plat sur la pédale", 112f, 88f, M, 5f)
            }
            Illus.BACK_STRAIGHT -> {
                sideScene(backColor = Y)
                line(50f, 0f, 50f, 8f, Y, 1f, dash = true)
                text("comme tiré par un fil", 118f, 6f, Y, 5.6f, bold = true)
                arrow(84f, 7f, 55f, 5f, Y, 1f)
                text("dos droit, pas raide", 118f, 22f, W, 5.4f)
            }
            Illus.SHOULDERS_RELAXED -> {
                frontFigure(48f, elbowsOut = false)
                arrow(34f, 18f, 34f, 26f, G, 1.2f)
                arrow(62f, 18f, 62f, 26f, G, 1.2f)
                check(48f, 82f)
                frontFigure(112f, elbowsOut = false, shouldersUp = true)
                line(96f, 22f, 99f, 18f, R, 1f); line(128f, 22f, 125f, 18f, R, 1f)
                cross(112f, 82f)
                text("épaules basses", 48f, 3f, W, 5.2f, bold = true)
                text("épaules aux oreilles", 112f, 3f, M, 5.2f)
            }
            Illus.ELBOWS -> {
                frontFigure(48f, elbowsOut = false)
                check(48f, 82f)
                text("coudes près du corps", 48f, 3f, W, 5.2f, bold = true)
                frontFigure(112f, elbowsOut = true)
                cross(112f, 82f)
                text("« ailes de poulet »", 112f, 3f, M, 5.2f)
            }
            Illus.FOREARMS -> {
                sideScene(armColor = Y)
                line(52f, 40f, 96f, 40f, M, 1f, dash = true)
                text("avant-bras presque", 126f, 18f, Y, 5.6f, bold = true)
                text("parallèles au sol", 126f, 25f, Y, 5.6f, bold = true)
                arrow(112f, 28f, 66f, 40f, Y)
            }
            Illus.SNARE_PLACEMENT -> {
                sideScene(snareHighlight = true)
                line(30f, 47f, 78f, 47f, M, 1f, dash = true)
                text("hauteur de la ceinture,", 118f, 10f, OT, 5.6f, bold = true)
                text("à plat ou un peu inclinée", 118f, 17f, OT, 5.6f, bold = true)
                arrow(104f, 20f, 94f, 45f, OT)
            }
            Illus.KICK_PLACEMENT -> {
                kitTop(Part.KICK, ox = -18f)
                text("grosse caisse", 138f, 52f, OT, 5f, bold = true)
                text("droit devant,", 138f, 59f, OT, 5f, bold = true)
                text("pédale sous", 138f, 66f, W, 5f)
                text("le pied droit", 138f, 73f, W, 5f)
            }
            Illus.HIHAT_PLACEMENT -> {
                floor()
                snareSide(70f, 50f)
                // pied et cymbales de charleston
                line(42f, 30f, 42f, 82f, M, 1.4f)
                line(42f, 82f, 34f, 84f, M, 1.2f); line(42f, 82f, 50f, 84f, M, 1.2f)
                poly(30f, 31f, 42f, 28f, 54f, 31f, c = CYM, w = 2f)
                poly(30f, 32.5f, 42f, 35f, 54f, 32.5f, c = CYM, w = 2f)
                arrow(62f, 33f, 62f, 50f, Y, 1.2f, both = true)
                text("10 à 15 cm", 82f, 41f, Y, 6f, bold = true)
                text("charleston", 42f, 22f, W, 5.4f, bold = true)
                text("caisse claire", 98f, 63f, M, 5f, align = 1)
                text("à gauche pour un droitier", 120f, 12f, M, 5f)
            }
            Illus.REACH -> {
                kitTop(Part.NONE, ox = -18f, labels = false)
                arc(62f, 80f, 58f, 200f, 140f, Y, 1.2f)
                text("tout est", 142f, 56f, Y, 5.6f, bold = true)
                text("à portée sans", 142f, 63f, Y, 5.6f, bold = true)
                text("te pencher", 142f, 70f, Y, 5.6f, bold = true)
            }
            Illus.LEFTY -> {
                kitTop(Part.NONE, ox = 0f, oy = 6f, s = 0.5f, labels = false)
                kitTop(Part.NONE, ox = 80f, oy = 6f, s = 0.5f, mirror = true, labels = false)
                text("droitier", 40f, 58f, W, 6f, bold = true)
                text("gaucher (en miroir)", 120f, 58f, W, 6f, bold = true)
                text("HH à gauche", 40f, 66f, M, 5f)
                text("HH à droite", 120f, 66f, M, 5f)
                line(80f, 8f, 80f, 70f, DIM, 1f, dash = true)
            }
            Illus.PRACTICE_PAD -> {
                floor()
                // pad sur pied
                line(80f, 52f, 80f, 82f, M, 1.4f)
                line(80f, 82f, 72f, 84f, M, 1.2f); line(80f, 82f, 88f, 84f, M, 1.2f)
                oval(80f, 50f, 20f, 4f, Color(0xFF555A9E), fill = true)
                oval(80f, 49f, 14f, 2.6f, Color(0xFF8A8FC9), fill = true)
                stick(56f, 30f, 74f, 47f, 2f); stick(104f, 30f, 86f, 47f, 2f)
                // téléphone qui écoute
                rect(124f, 56f, 12f, 22f, M, fill = true, r = 2f)
                rect(125.5f, 58f, 9f, 17f, X.Fond, fill = true, r = 1f)
                arc(124f, 64f, 6f, 160f, 40f, Y, 1f); arc(124f, 64f, 10f, 160f, 40f, Y, 1f)
                text("pad d'entraînement", 80f, 20f, W, 5.6f, bold = true)
                text("le micro l'entend", 130f, 50f, Y, 5f)
            }

            Illus.STICK_SIZES -> {
                stick(14f, 30f, 146f, 30f, 3.6f)
                stick(14f, 60f, 146f, 60f, 2.6f)
                text("5A : le standard pour débuter", 80f, 20f, W, 6f, bold = true)
                text("7A : plus fine, pour petites mains", 80f, 50f, M, 6f)
            }
            Illus.STICK_THIRD -> {
                stick(12f, 46f, 148f, 46f, 3.4f)
                line(57f, 38f, 57f, 54f, M, 0.8f, dash = true)
                line(102f, 38f, 102f, 54f, M, 0.8f, dash = true)
                circle(57f, 46f, 4.5f, Y, w = 1.6f)
                text("⅓", 35f, 58f, M, 6f); text("⅓", 80f, 58f, M, 6f); text("⅓", 125f, 58f, M, 6f)
                text("talon", 14f, 36f, M, 5.4f, align = 1)
                text("olive", 146f, 36f, M, 5.4f, align = 2)
                text("point d'équilibre ≈ ⅓ depuis le talon", 80f, 74f, Y, 6f, bold = true)
                arrow(57f, 68f, 57f, 52f, Y, 1.2f)
            }
            Illus.STICK_BOUNCE -> {
                oval(126f, 72f, 26f, 4f, Color(0xFF555A9E), fill = true)
                oval(126f, 71f, 18f, 2.4f, Color(0xFF8A8FC9), fill = true)
                grip(46f, 40f, 0.78f, angleDeg = 19f, tip = 96f, butt = 30f, fulcrum = true)
                arc(122f, 62f, 6f, 180f, 180f, Y, 1.2f)
                arc(132f, 64f, 4f, 180f, 180f, Y, 1f)
                arc(140f, 66f, 2.5f, 180f, 180f, Y, 0.8f)
                text("pince entre pouce et index,", 104f, 12f, W, 5.6f, bold = true)
                text("laisse rebondir", 104f, 19f, W, 5.6f, bold = true)
                text("ça rebondit longtemps = bon endroit", 80f, 86f, Y, 5f)
            }
            Illus.FULCRUM_SLIDE -> {
                grip(70f, 48f, fulcrum = true)
                arrow(58f, 26f, 82f, 26f, Y, 1.4f, both = true)
                text("fais glisser de quelques mm", 80f, 12f, Y, 5.8f, bold = true)
                text("jusqu'au rebond maximal : c'est ton pivot", 80f, 80f, W, 5.4f)
            }
            Illus.THUMB_INDEX -> {
                grip(66f, 46f, fulcrum = true)
                text("pouce à plat, le long", 116f, 12f, Y, 5.6f, bold = true)
                text("de la baguette", 116f, 19f, Y, 5.6f, bold = true)
                arrow(100f, 22f, 64f, 41f, Y, 1.1f)
                text("index enroulé dessous,", 116f, 70f, Y, 5.6f, bold = true)
                text("pas pointé dessus", 116f, 77f, Y, 5.6f, bold = true)
                arrow(96f, 68f, 72f, 52f, Y, 1.1f)
            }
            Illus.FINGERS_WRAP -> {
                grip(66f, 40f)
                arc(56f, 46f, 13f, 20f, 140f, G, 1.3f)
                text("3 doigts refermés souplement :", 80f, 72f, W, 5.6f, bold = true)
                text("ils guident, ils ne serrent pas", 80f, 80f, W, 5.6f)
            }
            Illus.BUTT_OUT -> {
                grip(76f, 48f, butt = 44f, tip = 70f)
                arrow(24f, 26f, 32f, 44f, Y, 1.2f)
                text("le talon dépasse de 2-3 cm", 80f, 12f, Y, 5.6f, bold = true)
                text("derrière la paume", 80f, 19f, Y, 5.6f, bold = true)
            }
            Illus.GRIPS_THREE -> {
                val labels = listOf("Allemande", "Américaine", "Française")
                val subs = listOf("dos de main en haut", "mains à 45°", "pouce en haut")
                val angles = listOf(0f, 45f, 90f)
                for (k in 0..2) {
                    val cx = 28f + k * 52f
                    oval(cx, 66f, 18f, 3f, HEAD, fill = true)
                    // la baguette sort entre le pouce et l'index
                    val a = angles[k] * PI.toFloat() / 180f
                    val sx = cx + (-4.5f * cos(a) - 3f * sin(a)) * 0.95f
                    val sy = 34f + (-4.5f * sin(a) + 3f * cos(a)) * 0.95f
                    stick(sx, sy, cx + 2f, 63f, 2.2f)
                    fistFront(cx, 34f, angles[k], 0.95f)
                    text(labels[k], cx, 76f, if (k == 1) Y else W, 6f, bold = true)
                    text(subs[k], cx, 84f, M, 4.8f)
                }
                rect(80f - 25f, 4f, 50f, 85f, Y, r = 6f, sw = 1f)
                text("pour commencer", 80f, 9f, Y, 4.6f, bold = true)
            }
            Illus.TOO_TIGHT -> {
                grip(50f, 44f, 0.7f, tight = true, tip = 30f, butt = 24f)
                cross(30f, 70f)
                text("crispé", 30f, 80f, M, 5.6f)
                text("doigts serrés, poignet bloqué", 40f, 87f, M, 4.4f)
                grip(128f, 44f, 0.7f, tip = 30f, butt = 24f)
                check(110f, 70f)
                text("détendu", 110f, 80f, W, 5.6f, bold = true)
                text("un peu d'air dans la main", 118f, 87f, M, 4.4f)
                text("tiens-la comme un oiseau", 80f, 10f, Y, 6f, bold = true)
            }

            Illus.DRIBBLE -> {
                floor()
                // ballon
                circle(36f, 70f, 7f, OT, fill = true)
                arrow(36f, 28f, 36f, 58f, Y, 1.2f)
                arrow(44f, 58f, 44f, 28f, G, 1.2f)
                text("le ballon", 40f, 12f, M, 5.4f)
                // baguette
                snareSide(98f, 64f)
                stick(100f, 30f, 128f, 22f, 2f, WOOD.copy(alpha = 0.35f))
                stick(98f, 26f, 112f, 62f, 2f)
                fistSide(101f, 33f, 69f, 1.5f)
                arc(112f, 38f, 18f, 75f, -85f, G, 1.2f, arrowEnd = true)
                text("la baguette", 112f, 12f, W, 5.4f, bold = true)
                text("lancer… et laisser remonter", 80f, 88f, Y, 5.4f, bold = true)
            }
            Illus.START_HEIGHT -> {
                floor()
                snareSide(70f, 60f)
                stick(66f, 18f, 86f, 40f, 2.4f)
                fistSide(71f, 23.5f, 48f, 1.6f)
                arrow(98f, 40f, 98f, 60f, Y, 1.2f, both = true)
                text("≈ 20 cm", 116f, 50f, Y, 6.4f, bold = true)
                text("départ : olive à 20 cm de la peau", 80f, 10f, W, 5.4f)
            }
            Illus.WRIST_MOTION -> {
                stick(72f, 54f, 128f, 18f, 2.4f, WOOD.copy(alpha = 0.3f))
                grip(72f, 54f, 0.8f, angleDeg = 8f, tip = 84f, butt = 30f)
                arc(72f, 54f, 52f, -30f, 34f, Y, 1.4f, arrowEnd = true)
                circle(51.5f, 50f, 2.4f, Y, fill = true)
                text("pivot : le poignet", 40f, 70f, Y, 5f, bold = true)
                text("le poignet d'abord,", 4f, 12f, Y, 5.8f, bold = true, align = 1)
                text("l'avant-bras accompagne", 4f, 20f, Y, 5.8f, bold = true, align = 1)
            }
            Illus.REBOUND -> {
                floor()
                snareSide(66f, 66f)
                line(40f, 24f, 130f, 24f, M, 1f, dash = true)
                stick(54f, 40f, 66f, 24f, 2f, WOOD.copy(alpha = 0.5f))
                arrow(70f, 28f, 74f, 62f, OT, 1.4f)
                arrow(90f, 62f, 94f, 28f, G, 1.4f)
                text("1. frappe", 66f, 46f, OT, 5.6f, bold = true, align = 2)
                text("2. remonte seule", 98f, 46f, G, 5.6f, bold = true, align = 1)
                text("même hauteur de départ", 140f, 18f, M, 5f, align = 2)
            }
            Illus.CENTER_HIT -> {
                circle(80f, 46f, 34f, HEAD, fill = true)
                circle(80f, 46f, 34f, SHELL, w = 3f)
                circle(80f, 46f, 12f, O.copy(alpha = 0.35f), fill = true)
                circle(80f, 46f, 4f, OT, fill = true)
                text("vise le centre : son plein et régulier", 80f, 86f, W, 5.6f, bold = true)
            }
            Illus.HEIGHT_VOLUME -> {
                floor()
                snareSide(16f, 64f)
                stick(14f, 51f, 30f, 62f, 2f)
                fistSide(19f, 54.5f, 34f, 1.4f)
                arc(30f, 64f, 5f, 200f, 140f, M, 1f)
                text("baguette basse = doux", 4f, 40f, M, 5.6f, align = 1)
                snareSide(98f, 64f)
                stick(94f, 13f, 112f, 62f, 2f)
                fistSide(96.5f, 19f, 70f, 1.4f)
                arc(112f, 64f, 6f, 200f, 140f, Y, 1.2f)
                arc(112f, 64f, 12f, 200f, 140f, Y, 1.2f)
                arc(112f, 64f, 18f, 200f, 140f, Y, 1.2f)
                text("baguette haute = fort", 156f, 10f, Y, 5.6f, bold = true, align = 2)
            }
            Illus.SAME_HEIGHT -> {
                floor()
                snareSide(66f, 64f)
                line(30f, 26f, 130f, 26f, Y, 1f, dash = true)
                stick(56f, 47f, 70f, 26f, 2.2f)
                stick(104f, 47f, 90f, 26f, 2.2f)
                fistSide(59f, 42f, -56f, 1.4f)
                d.scale(-1f, 1f, o(101f, 42f)) { fistSide(101f, 42f, -56f, 1.4f) }
                text("G", 54f, 50f, W, 6f, bold = true)
                text("D", 106f, 50f, W, 6f, bold = true)
                text("mêmes hauteurs = même son", 80f, 14f, Y, 5.8f, bold = true)
            }
            Illus.HEEL_DOWN, Illus.HEEL_UP -> {
                val up = i == Illus.HEEL_UP
                floor()
                kickSide(130f, 60f)
                // pédale
                poly(40f, 82f, 96f, 74f, c = M, w = 2f)
                line(96f, 74f, 106f, 54f, M, 1.4f)
                circle(107f, 52f, 2.6f, HEAD, fill = true)
                // jambe et pied
                if (up) {
                    limb(0f, 30f, 40f, 34f, 10f, 8f, PANTS)   // cuisse
                    limb(40f, 34f, 60f, 60f, 7.5f, 5.5f, PANTS)  // tibia
                    shoe(57f, 66f, 84f, 76f) // talon levé, pointe sur la pédale
                    arrow(40f, 22f, 40f, 40f, Y, 1.2f, both = true)
                    text("talon levé : la jambe aide", 100f, 8f, Y, 5.8f, bold = true)
                    text("plus de puissance", 100f, 16f, M, 5f)
                } else {
                    limb(0f, 34f, 38f, 34f, 10f, 8f, PANTS)   // cuisse
                    limb(38f, 34f, 46f, 71f, 7.5f, 5.5f, PANTS)  // tibia
                    shoe(42f, 80.5f, 72f, 77f) // talon posé
                    arc(46f, 74f, 12f, -70f, 45f, Y, 1.4f, arrowEnd = true)
                    text("talon posé : la cheville travaille", 100f, 8f, Y, 5.8f, bold = true)
                    text("plus de contrôle, idéal pour débuter", 100f, 16f, M, 5f)
                }
            }
            Illus.PEDAL_RELEASE -> {
                floor()
                kickSide(124f, 58f)
                poly(40f, 82f, 92f, 74f, c = M, w = 2f)
                line(92f, 74f, 100f, 52f, M, 1.4f)
                circle(101f, 50f, 2.6f, HEAD, fill = true)
                limb(0f, 40f, 34f, 40f, 10f, 8f, PANTS)
                limb(34f, 40f, 44f, 72f, 7.5f, 5.5f, PANTS)
                shoe(41f, 81f, 70f, 77.5f)
                arc(92f, 74f, 26f, -75f, -40f, G, 1.4f, arrowEnd = true)
                text("frappe… puis relâche :", 52f, 18f, W, 5.8f, bold = true)
                text("la mailloche ne reste pas collée", 52f, 26f, Y, 5.6f, bold = true)
            }
            Illus.MIRROR -> {
                sideScene()
                rect(140f, 24f, 12f, 22f, M, fill = true, r = 2f)
                rect(141.5f, 26f, 9f, 17f, X.Fond, fill = true, r = 1f)
                circle(146f, 34.5f, 2.6f, OT, fill = true)
                text("filme-toi ou joue", 156f, 8f, Y, 5.4f, bold = true, align = 2)
                text("devant un miroir", 156f, 15f, Y, 5.4f, bold = true, align = 2)
            }

            Illus.KIT_FULL -> kitTop(Part.ALL)
            Illus.KIT_KICK -> kitTop(Part.KICK)
            Illus.KIT_SNARE -> kitTop(Part.SNARE)
            Illus.KIT_HIHAT -> kitTop(Part.HIHAT)
            Illus.KIT_HIHAT_FOOT -> kitTop(Part.HIHAT_FOOT)
            Illus.KIT_TOMS -> kitTop(Part.TOMS)
            Illus.KIT_CRASH -> kitTop(Part.CRASH)
            Illus.KIT_RIDE -> kitTop(Part.RIDE)

            Illus.TIME_SIGNATURE -> {
                val top = 26f
                staff(8f, 152f, top)
                text("4", 18f, top + 5f, W, 11f, bold = true)
                text("4", 18f, top + 15f, W, 11f, bold = true)
                for (k in 0..3) {
                    val x = 44f + k * 28f
                    note(x, top, 3, false)
                    text("${k + 1}", x, top + 32f, if (k == 0) OT else W, 7f, bold = true)
                }
                line(152f, top, 152f, top + 20f, W, 1f)
                text("4 temps par mesure", 80f, 10f, Y, 5.8f, bold = true)
                text("temps 1 = clic aigu", 52f, 84f, OT, 5f)
            }
            Illus.QUARTERS -> {
                val top = 30f
                staff(8f, 152f, top)
                for (k in 0..3) {
                    val x = 26f + k * 34f
                    note(x, top, 3, false)
                    text("${k + 1}", x, top + 34f, W, 7f, bold = true)
                }
                text("Noires : 1 coup par temps", 80f, 10f, Y, 6f, bold = true)
            }
            Illus.EIGHTHS -> {
                val top = 30f
                staff(8f, 152f, top)
                val counts = listOf("1", "et", "2", "et", "3", "et", "4", "et")
                for (k in 0..7) {
                    val x = 20f + k * 17f
                    note(x, top, 3, false)
                    text(counts[k], x, top + 34f, if (k % 2 == 0) W else M, 6f, bold = k % 2 == 0)
                }
                for (g in 0..3) beam(20f + g * 34f + 2.75f, 37f + g * 34f + 2.75f, top + 7.5f - 16f, 1)
                text("Croches : 2 coups par temps", 80f, 8f, Y, 6f, bold = true)
            }
            Illus.SIXTEENTHS -> {
                val top = 30f
                staff(6f, 154f, top)
                val counts = listOf("1", "e", "et", "a")
                for (k in 0..15) {
                    val x = 12f + k * 9f
                    note(x, top, 3, false)
                    text(if (k % 4 == 0) "${k / 4 + 1}" else counts[k % 4], x, top + 34f, if (k % 4 == 0) W else M, 4.8f, bold = k % 4 == 0)
                }
                for (g in 0..3) beam(12f + g * 36f + 2.75f, 39f + g * 36f + 2.75f, top + 7.5f - 16f, 2)
                text("Doubles croches : 4 coups par temps", 80f, 8f, Y, 6f, bold = true)
            }
            Illus.STAFF_MAP -> {
                val top = 36f
                staff(8f, 152f, top, 5f)
                val items = listOf(
                    Triple("CR", -2, true), Triple("RD", 0, true), Triple("HH", -1, true), Triple("T", 1, false),
                    Triple("CC", 3, false), Triple("GC", 7, false), Triple("HP", 9, true),
                )
                items.forEachIndexed { k, (name, pos, cross) ->
                    val x = 22f + k * 19f
                    if (pos == -2) line(x - 5f, top - 5f, x + 5f, top - 5f, M, 0.8f)
                    note(x, top, pos, cross, stemUp = pos < 6, sp = 5f, c = if (pos >= 6) OT else W, stemLen = 2.4f)
                    text(name, x, 84f, if (pos >= 6) OT else Y, 6f, bold = true)
                }
                text("mains : hampe en haut · pieds : hampe en bas", 80f, 6f, M, 5f)
            }
            Illus.STICKING -> {
                val top = 30f
                staff(8f, 152f, top)
                val st = "DGDGDDGG"
                for (k in 0..7) {
                    val x = 20f + k * 17f
                    note(x, top, 3, false)
                    text(st[k].toString(), x, top + 34f, if (st[k] == 'D') Y else OT, 7f, bold = true)
                }
                for (g in 0..3) beam(20f + g * 34f + 2.75f, 37f + g * 34f + 2.75f, top + 7.5f - 16f, 1)
                text("D = main droite · G = main gauche", 80f, 8f, W, 5.8f, bold = true)
            }

            Illus.LAYERS -> {
                val rows = listOf("RD" to "xxxxxxxx", "CC" to "..x...x.", "GC" to "x...x...", "HP" to "..x...x.")
                rows.forEachIndexed { r, (name, hits) ->
                    val y = 18f + r * 17f
                    text("${r + 1}. $name", 14f, y, if (r == 0) Y else W, 5.6f, bold = true)
                    for (k in 0..7) {
                        val x = 40f + k * 15f
                        val on = hits[k] == 'x'
                        circle(x, y, 4.2f, if (on) (if (name == "GC" || name == "HP") OT else Y) else DIM, fill = on)
                        if (!on) circle(x, y, 4.2f, DIM, w = 0.8f)
                    }
                }
                text("ajoute un membre à la fois", 96f, 86f, M, 5f)
            }
            Illus.ROUTINE -> {
                val parts = listOf(Triple("Échauffement", 3, Y), Triple("Exercice du jour", 10, OT), Triple("Plaisir", 5, G), Triple("Calme", 2, M))
                var x = 10f
                parts.forEach { (name, min, c) ->
                    val w = min * 7f
                    rect(x, 34f, w - 1f, 18f, c, fill = true, r = 3f)
                    text(name, x + w / 2, 60f, W, 4.8f, bold = true)
                    text("$min min", x + w / 2, 67f, M, 4.8f)
                    x += w
                }
                text("Une séance de 20 minutes", 80f, 16f, Y, 6f, bold = true)
            }
            Illus.TEMPO_RAMP -> {
                line(16f, 76f, 150f, 76f, M, 1f)
                line(16f, 76f, 16f, 12f, M, 1f)
                var y = 66f
                var x = 18f
                for (k in 0..5) {
                    line(x, y, x + 20f, y, Y, 2f)
                    if (k < 5) line(x + 20f, y, x + 20f, y - 9f, Y, 2f)
                    x += 20f; y -= 9f
                }
                text("tempo", 12f, 8f, M, 5f, align = 1)
                text("temps", 152f, 81f, M, 5f, align = 2)
                text("lent", 28f, 71f, Y, 5.4f, bold = true)
                text("plus rapide", 128f, 14f, Y, 5.4f, bold = true)
                text("on ne monte qu'à 80 % de précision", 70f, 87f, W, 5f)
            }
            Illus.DAILY -> {
                val days = listOf("L", "M", "M", "J", "V", "S", "D")
                days.forEachIndexed { k, dname ->
                    val x = 20f + k * 20f
                    rect(x - 8f, 30f, 16f, 22f, X.Pastille, fill = true, r = 3f)
                    text(dname, x, 36f, M, 5f)
                    check(x, 46f)
                }
                text("15 min chaque jour", 80f, 14f, Y, 6.4f, bold = true)
                text("plutôt qu'1 h 30 le dimanche", 80f, 70f, W, 5.6f)
            }
            Illus.EARPLUGS -> {
                // oreille stylisée
                arc(34f, 44f, 22f, 100f, 250f, SKIN, 5f)
                arc(36f, 46f, 10f, 140f, 200f, SKIN, 3f)
                oval(44f, 50f, 6f, 4.2f, OT, fill = true)
                text("bouchons pour musiciens", 110f, 32f, Y, 5.6f, bold = true)
                text("batterie acoustique : > 100 dB", 110f, 42f, W, 5f)
                text("les dégâts sont définitifs", 110f, 52f, OT, 5f, bold = true)
            }
        }
    }
}
