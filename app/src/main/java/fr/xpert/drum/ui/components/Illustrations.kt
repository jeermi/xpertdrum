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
        rect(31f, 52f, 26f, 4f, M, fill = true, r = 2f)
        if (seatHighlight) rect(44f, 51.5f, 13f, 5f, O, fill = true, r = 2f)
        line(44f, 56f, 44f, 80f, M, 1.6f)
        line(44f, 80f, 35f, 84f, M, 1.4f)
        line(44f, 80f, 53f, 84f, M, 1.4f)
    }

    fun snareSide(x: Float = 80f, top: Float = 47f, highlight: Boolean = false) {
        rect(x, top, 26f, 7f, if (highlight) O else SHELL, fill = true, r = 1f)
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
        poly(74f, 82f, 100f, 78f, c = M, w = 1.6f)
        line(100f, 78f, 112f, 64f, M, 1.2f)
        // mailloche
        circle(113.5f, 62.5f, 2.2f, HEAD, fill = true)
    }

    fun drummer(p: Pose = Pose(), c: Color = W, armColor: Color = c, thighColor: Color = c, backColor: Color = c) {
        // jambe
        line(p.hip.x, p.hip.y, p.knee.x, p.knee.y, thighColor, 3.2f)
        line(p.knee.x, p.knee.y, p.ankle.x, p.ankle.y, c, 3f)
        line(p.ankle.x, p.ankle.y, p.toe.x, p.toe.y, c, 2.6f)
        // tronc et tête
        line(p.hip.x, p.hip.y, p.shoulder.x, p.shoulder.y, backColor, 3.6f)
        circle(p.shoulder.x + 2f, p.shoulder.y - 9f, 6f, c, fill = true)
        // bras
        line(p.shoulder.x, p.shoulder.y, p.elbow.x, p.elbow.y, armColor, 2.6f)
        line(p.elbow.x, p.elbow.y, p.hand.x, p.hand.y, armColor, 2.4f)
        circle(p.hand.x, p.hand.y, 1.8f, SKIN, fill = true)
        stick(p.hand.x - 3f, p.hand.y - 1f, p.stickTip.x, p.stickTip.y, 1.6f)
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
        drummer(pose, W, armColor, thighColor, backColor)
    }

    // ------------------------------------------------------------------ Batteur de face

    fun frontFigure(cx: Float, elbowsOut: Boolean, shouldersUp: Boolean = false, c: Color = W) {
        val sy = if (shouldersUp) 24f else 28f
        circle(cx, 14f, 7f, c, fill = true)
        // épaules et tronc
        line(cx - 13f, sy, cx + 13f, sy, c, 3f)
        poly(cx - 8f, sy + 1f, cx - 6f, 62f, cx + 6f, 62f, cx + 8f, sy + 1f, c = c, w = 2.4f)
        // bras
        val ex = if (elbowsOut) 24f else 15f
        val ey = if (elbowsOut) 42f else 46f
        line(cx - 13f, sy, cx - ex, ey, c, 2.6f)
        line(cx - ex, ey, cx - 6f, 56f, c, 2.4f)
        line(cx + 13f, sy, cx + ex, ey, c, 2.6f)
        line(cx + ex, ey, cx + 6f, 56f, c, 2.4f)
        circle(cx - 6f, 56f, 1.8f, SKIN, fill = true)
        circle(cx + 6f, 56f, 1.8f, SKIN, fill = true)
        stick(cx - 6f, 56f, cx - 1f, 66f, 1.4f)
        stick(cx + 6f, 56f, cx + 1f, 66f, 1.4f)
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

    /** Main qui tient la baguette (vue de côté), pivot en (px, py). */
    fun hand(px: Float, py: Float, tight: Boolean = false) {
        // paume
        oval(px - 2f, py + 4f, 9f, 6.5f, SKIN, fill = true)
        // doigts enroulés (arcs) sous la baguette
        val gap = if (tight) 0.2f else 1.6f
        for (k in 0..2) {
            val fx = px + 4f + k * 5f
            oval(fx, py + 2.6f + gap, 2.6f, 3.4f, SKIN, fill = true)
        }
        // index enroulé
        oval(px + 0.5f, py + 2.2f, 3f, 3f, SKIN, fill = true)
        // pouce à plat sur le côté
        oval(px - 1f, py - 2.2f, 5.5f, 2f, Color(0xFFE0AE88), fill = true)
        if (tight) {
            for (k in 0..2) line(px - 12f + k * 2f, py - 8f + k * 1f, px - 9f + k * 2f, py - 6f + k * 1f, R, 1f)
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
                // pad
                oval(118f, 70f, 26f, 4f, Color(0xFF555A9E), fill = true)
                stick(30f, 40f, 112f, 66f, 2.6f)
                // pince pouce-index
                oval(56f, 45f, 3.6f, 2.6f, SKIN, fill = true)
                oval(56f, 51f, 3.6f, 2.6f, SKIN, fill = true)
                // rebonds
                arc(116f, 60f, 6f, 180f, 180f, Y, 1.2f)
                arc(124f, 62f, 4f, 180f, 180f, Y, 1f)
                arc(130f, 64f, 2.5f, 180f, 180f, Y, 0.8f)
                text("pince entre pouce et index,", 60f, 14f, W, 5.6f, bold = true)
                text("laisse rebondir", 60f, 21f, W, 5.6f, bold = true)
                text("ça rebondit longtemps = bon endroit", 98f, 84f, Y, 5f)
            }
            Illus.FULCRUM_SLIDE -> {
                stick(12f, 46f, 148f, 46f, 3.2f)
                oval(57f, 42.5f, 4f, 2.6f, SKIN, fill = true)
                oval(57f, 49.5f, 4f, 2.6f, SKIN, fill = true)
                arrow(44f, 30f, 70f, 30f, Y, 1.4f, both = true)
                text("fais glisser de quelques mm", 80f, 18f, Y, 5.8f, bold = true)
                text("jusqu'au rebond maximal : c'est ton pivot", 80f, 68f, W, 5.4f)
            }
            Illus.THUMB_INDEX -> {
                stick(14f, 46f, 148f, 46f, 3f)
                hand(58f, 46f)
                text("pouce à plat sur le côté", 44f, 20f, Y, 5.6f, bold = true)
                arrow(40f, 25f, 55f, 42f, Y, 1.1f)
                text("index enroulé, pas pointé dessus", 154f, 72f, Y, 5.6f, bold = true, align = 2)
                arrow(96f, 68f, 61f, 51f, Y, 1.1f)
            }
            Illus.FINGERS_WRAP -> {
                stick(14f, 40f, 148f, 40f, 3f)
                hand(58f, 40f)
                text("3 doigts refermés souplement :", 80f, 66f, W, 5.6f, bold = true)
                text("ils guident, ils ne serrent pas", 80f, 74f, W, 5.6f)
                arc(70f, 45f, 9f, 20f, 120f, G, 1f)
            }
            Illus.BUTT_OUT -> {
                stick(30f, 46f, 148f, 46f, 3f)
                hand(58f, 46f)
                arrow(44f, 30f, 32f, 42f, Y, 1.2f)
                text("le talon dépasse un peu de la paume", 66f, 22f, Y, 5.6f, bold = true)
            }
            Illus.GRIPS_THREE -> {
                val labels = listOf("Allemande", "Américaine", "Française")
                val subs = listOf("paume vers le sol", "mains à 45°", "pouce en haut")
                val angles = listOf(0f, 45f, 90f)
                for (k in 0..2) {
                    val cx = 28f + k * 52f
                    // avant-bras (cercle) et main en rectangle tourné
                    d.rotate(angles[k], o(cx, 40f)) {
                        rect(cx - 12f, 36f, 24f, 8f, SKIN, fill = true, r = 3f)
                        circle(cx - 6f, 33.5f, 2.2f, Color(0xFFE0AE88), fill = true) // pouce
                    }
                    stick(cx, 40f, cx, 14f, 2.2f)
                    oval(cx, 66f, 18f, 3f, HEAD, fill = true)
                    text(labels[k], cx, 76f, if (k == 1) Y else W, 6f, bold = true)
                    text(subs[k], cx, 84f, M, 4.8f)
                }
                rect(80f - 25f, 4f, 50f, 85f, Y, r = 6f, sw = 1f)
                text("pour commencer", 80f, 9f, Y, 4.6f, bold = true)
            }
            Illus.TOO_TIGHT -> {
                stick(4f, 40f, 74f, 40f, 2.6f)
                hand(30f, 40f, tight = true)
                cross(40f, 70f)
                text("crispé", 40f, 82f, M, 5.6f)
                stick(86f, 40f, 156f, 40f, 2.6f)
                hand(112f, 40f)
                check(120f, 70f)
                text("détendu", 120f, 82f, W, 5.6f, bold = true)
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
                stick(100f, 30f, 112f, 62f, 2f)
                stick(100f, 30f, 128f, 22f, 2f, WOOD.copy(alpha = 0.35f))
                arc(112f, 38f, 18f, 75f, -85f, G, 1.2f, arrowEnd = true)
                text("la baguette", 112f, 12f, W, 5.4f, bold = true)
                text("lancer… et laisser remonter", 80f, 88f, Y, 5.4f, bold = true)
            }
            Illus.START_HEIGHT -> {
                floor()
                snareSide(70f, 60f)
                stick(70f, 22f, 86f, 40f, 2.4f)
                arrow(98f, 40f, 98f, 60f, Y, 1.2f, both = true)
                text("≈ 20 cm", 116f, 50f, Y, 6.4f, bold = true)
                text("départ : olive à 20 cm de la peau", 80f, 10f, W, 5.4f)
            }
            Illus.WRIST_MOTION -> {
                // avant-bras horizontal, poignet pivot
                line(14f, 52f, 70f, 52f, SKIN, 6f)
                circle(72f, 52f, 3f, Y, fill = true)
                stick(72f, 52f, 140f, 64f, 2.4f)
                stick(72f, 52f, 132f, 24f, 2.4f, WOOD.copy(alpha = 0.35f))
                arc(72f, 52f, 58f, -25f, 33f, Y, 1.4f, arrowEnd = true)
                text("le poignet d'abord,", 4f, 16f, Y, 5.8f, bold = true, align = 1)
                text("l'avant-bras accompagne", 4f, 24f, Y, 5.8f, bold = true, align = 1)
                text("pivot", 72f, 62f, M, 5f)
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
                stick(18f, 54f, 30f, 62f, 2f)
                arc(30f, 64f, 5f, 200f, 140f, M, 1f)
                text("baguette basse = doux", 4f, 40f, M, 5.6f, align = 1)
                snareSide(98f, 64f)
                stick(96f, 18f, 112f, 62f, 2f)
                arc(112f, 64f, 6f, 200f, 140f, Y, 1.2f)
                arc(112f, 64f, 12f, 200f, 140f, Y, 1.2f)
                arc(112f, 64f, 18f, 200f, 140f, Y, 1.2f)
                text("baguette haute = fort", 156f, 10f, Y, 5.6f, bold = true, align = 2)
            }
            Illus.SAME_HEIGHT -> {
                floor()
                snareSide(66f, 64f)
                line(30f, 26f, 130f, 26f, Y, 1f, dash = true)
                stick(58f, 44f, 70f, 26f, 2.2f)
                stick(102f, 44f, 90f, 26f, 2.2f)
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
                    line(4f, 30f, 40f, 34f, W, 3.4f)   // cuisse
                    line(40f, 34f, 56f, 62f, W, 3.2f)  // tibia
                    poly(56f, 62f, 62f, 70f, 90f, 75f, c = W, w = 2.8f) // talon levé, pointe sur la pédale
                    arrow(40f, 22f, 40f, 40f, Y, 1.2f, both = true)
                    text("talon levé : la jambe aide", 100f, 8f, Y, 5.8f, bold = true)
                    text("plus de puissance", 100f, 16f, M, 5f)
                } else {
                    line(4f, 34f, 38f, 34f, W, 3.4f)   // cuisse
                    line(38f, 34f, 44f, 70f, W, 3.2f)  // tibia
                    poly(44f, 70f, 44f, 80f, 90f, 75f, c = W, w = 2.8f) // talon posé
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
