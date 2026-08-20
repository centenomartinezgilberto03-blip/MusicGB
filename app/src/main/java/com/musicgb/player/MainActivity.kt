package com.musicgb.player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Crear layout programáticamente (sin XML)
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#121212"))
            setPadding(32, 64, 32, 32)
        }
        
        // Título de la app
        val titleText = TextView(this).apply {
            text = "MusicGB"
            textSize = 32f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 32)
        }
        
        // Subtítulo
        val subtitleText = TextView(this).apply {
            text = "Reproductor de Música Avanzado"
            textSize = 18f
            setTextColor(Color.parseColor("#1DB954"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 48)
        }
        
        // Lista de características
        val featuresText = TextView(this).apply {
            text = """
                Características:
                
                • Ecualizador de 32 bandas
                • Efectos DSP avanzados
                • Soporte Hi-Res Audio
                • Crossfade configurable
                • Gapless Playback
                • Replay Gain
                • Biblioteca local completa
                • 100% Offline
            """.trimIndent()
            textSize = 16f
            setTextColor(Color.parseColor("#B3B3B3"))
            setPadding(16, 16, 16, 16)
        }
        
        rootLayout.addView(titleText)
        rootLayout.addView(subtitleText)
        rootLayout.addView(featuresText)
        
        setContentView(rootLayout)
    }
}
