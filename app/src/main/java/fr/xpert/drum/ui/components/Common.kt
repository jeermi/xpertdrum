package fr.xpert.drum.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.xpert.drum.ui.theme.X

/**
 * Logo XPert Drum : même construction que logo-xpert.svg (carré marine arrondi,
 * bande orange en biais, symbole blanc, accent jaune), avec une caisse claire à la place de la toque.
 */
@Composable
fun XpertLogo(size: Dp = 40.dp) {
    Canvas(Modifier.size(size)) {
        val u = this.size.width / 100f
        val r = 20f * u
        val clip = Path().apply {
            addRoundRect(androidx.compose.ui.geometry.RoundRect(0f, 0f, 100f * u, 100f * u, CornerRadius(r, r)))
        }
        clipPath(clip) {
            drawRect(X.Marine)
            val band = Path().apply {
                moveTo(0f, 74f * u); lineTo(100f * u, 56f * u); lineTo(100f * u, 100f * u); lineTo(0f, 100f * u); close()
            }
            drawPath(band, X.Orange)
        }
        // baguettes
        drawLine(X.JauneLogo, Offset(30f * u, 14f * u), Offset(46f * u, 34f * u), 4f * u, StrokeCap.Round)
        drawLine(X.JauneLogo, Offset(70f * u, 14f * u), Offset(54f * u, 34f * u), 4f * u, StrokeCap.Round)
        // fût
        drawRect(androidx.compose.ui.graphics.Color.White, Offset(24f * u, 40f * u), Size(52f * u, 20f * u))
        drawOval(androidx.compose.ui.graphics.Color.White, Offset(24f * u, 52f * u), Size(52f * u, 16f * u))
        // peau
        drawOval(androidx.compose.ui.graphics.Color.White, Offset(24f * u, 32f * u), Size(52f * u, 16f * u))
        drawOval(X.Marine, Offset(24f * u, 32f * u), Size(52f * u, 16f * u), style = Stroke(2.2f * u))
        for (x in listOf(34f, 50f, 66f)) {
            drawLine(X.Marine, Offset(x * u, 46f * u), Offset(x * u, 64f * u), 2.2f * u)
        }
    }
}

@Composable
fun XpertWordmark() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        XpertLogo(36.dp)
        Spacer(Modifier.width(10.dp))
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = X.Texte)) { append("XPert ") }
                withStyle(SpanStyle(color = X.OrangeTexte)) { append("Drum") }
            },
            fontSize = 22.sp,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

/** Fond dégradé des applis XPert. */
@Composable
fun XpertBackground(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(X.FondHaut, X.Fond, Color0A)))
    ) { content() }
}

private val Color0A = androidx.compose.ui.graphics.Color(0xFF0A0A30)

@Composable
fun XCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            color = X.Carte,
            border = BorderStroke(1.dp, X.CarteBord),
        ) { Column(Modifier.padding(18.dp), content = content) }
    } else {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            color = X.Carte,
            border = BorderStroke(1.dp, X.CarteBord),
        ) { Column(Modifier.padding(18.dp), content = content) }
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = X.Texte2,
        modifier = modifier.padding(top = 18.dp, bottom = 10.dp),
    )
}

@Composable
fun Pill(text: String, color: androidx.compose.ui.graphics.Color = X.Jaune) {
    Surface(shape = RoundedCornerShape(50), color = X.Pastille) {
        Text(
            text,
            color = color,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
        )
    }
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = X.Orange, contentColor = X.Texte),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 14.dp),
    ) { Text(text, style = MaterialTheme.typography.labelLarge) }
}

@Composable
fun SecondaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        border = BorderStroke(2.dp, X.CarteBord),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = X.Texte),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 14.dp),
    ) { Text(text, style = MaterialTheme.typography.labelLarge) }
}

/** En-tête d'écran : retour + titre. */
@Composable
fun ScreenHeader(title: String, onBack: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = X.Texte)
        }
        Text(title, style = MaterialTheme.typography.titleLarge, color = X.Texte)
    }
}
