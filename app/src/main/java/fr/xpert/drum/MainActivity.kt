package fr.xpert.drum

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fr.xpert.drum.audio.AudioEngine
import fr.xpert.drum.data.ProgressStore
import fr.xpert.drum.ui.XpertDrumApp
import fr.xpert.drum.ui.theme.XpertTheme

class MainActivity : ComponentActivity() {
    private lateinit var engine: AudioEngine
    private lateinit var store: ProgressStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        engine = AudioEngine(applicationContext)
        store = ProgressStore(applicationContext)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            XpertTheme { XpertDrumApp(engine, store) }
        }
    }

    override fun onStart() {
        super.onStart()
        engine.start()
    }

    override fun onStop() {
        engine.stop()
        super.onStop()
    }
}
