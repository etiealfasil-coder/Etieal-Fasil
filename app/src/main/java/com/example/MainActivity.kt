package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.audio.SpeechSynthesizer
import com.example.data.ai.GeminiEducationalService
import com.example.data.db.AppDatabase
import com.example.data.repository.EducationalRepository
import com.example.ui.MainApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var speechSynthesizer: SpeechSynthesizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this, lifecycleScope)
        val repository = EducationalRepository(database.educationalProjectDao())
        val geminiService = GeminiEducationalService(this)
        speechSynthesizer = SpeechSynthesizer(this)

        setContent {
            MyApplicationTheme {
                MainApp(
                    repository = repository,
                    geminiService = geminiService,
                    speechSynthesizer = speechSynthesizer
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::speechSynthesizer.isInitialized) {
            speechSynthesizer.release()
        }
    }
}
