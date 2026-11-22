package fr.xpert.drum.ui.components

import androidx.compose.ui.graphics.Color
import fr.xpert.drum.practice.Lane

/** Couleur associée à chaque élément de la batterie. */
fun laneColor(lane: Lane): Color = when (lane) {
    Lane.CRASH -> Color(0xFFFFAB19)
    Lane.RIDE -> Color(0xFF4FD1C5)
    Lane.HHFOOT -> Color(0xFF2BB673)
    Lane.HIHAT -> Color(0xFFFBC63C)
    Lane.TOM -> Color(0xFF8E7CF0)
    Lane.SNARE -> Color(0xFFE8501E)
    Lane.KICK -> Color(0xFF5B8DEF)
}
