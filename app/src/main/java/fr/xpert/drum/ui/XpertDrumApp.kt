package fr.xpert.drum.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import fr.xpert.drum.audio.AudioEngine
import fr.xpert.drum.data.ProgressStore
import fr.xpert.drum.ui.components.XpertBackground
import fr.xpert.drum.ui.screens.CalibrationScreen
import fr.xpert.drum.ui.screens.ExerciseScreen
import fr.xpert.drum.ui.screens.ExercisesScreen
import fr.xpert.drum.ui.screens.FreePadsScreen
import fr.xpert.drum.ui.screens.HomeScreen
import fr.xpert.drum.ui.screens.LessonScreen
import fr.xpert.drum.ui.screens.LessonsScreen
import fr.xpert.drum.ui.screens.MetronomeScreen
import fr.xpert.drum.ui.screens.MicSetupScreen
import fr.xpert.drum.ui.screens.ProgressScreen

sealed interface Screen {
    data object Home : Screen
    data object Lessons : Screen
    data class Lesson(val id: String) : Screen
    data object Exercises : Screen
    data class Exercise(val id: String) : Screen
    data object Metronome : Screen
    data object Pads : Screen
    data object Progress : Screen
    data object Calibration : Screen
    data object MicSetup : Screen
}

class Nav(private val get: () -> List<Screen>, private val set: (List<Screen>) -> Unit) {
    fun go(s: Screen) = set(get() + s)
    fun back() {
        val st = get()
        if (st.size > 1) set(st.dropLast(1))
    }
}

@Composable
fun XpertDrumApp(engine: AudioEngine, store: ProgressStore) {
    var stack by remember { mutableStateOf(listOf<Screen>(Screen.Home)) }
    val nav = remember { Nav({ stack }, { stack = it }) }
    BackHandler(enabled = stack.size > 1) { nav.back() }

    XpertBackground {
        Box(Modifier.fillMaxSize().safeDrawingPadding()) {
            when (val s = stack.last()) {
                Screen.Home -> HomeScreen(store, nav)
                Screen.Lessons -> LessonsScreen(store, nav)
                is Screen.Lesson -> LessonScreen(s.id, store, nav)
                Screen.Exercises -> ExercisesScreen(store, nav)
                is Screen.Exercise -> ExerciseScreen(s.id, engine, store, nav)
                Screen.Metronome -> MetronomeScreen(engine, store, nav)
                Screen.Pads -> FreePadsScreen(engine, nav)
                Screen.Progress -> ProgressScreen(store, nav)
                Screen.Calibration -> CalibrationScreen(engine, store, nav)
                Screen.MicSetup -> MicSetupScreen(engine, store, nav)
            }
        }
    }
}
