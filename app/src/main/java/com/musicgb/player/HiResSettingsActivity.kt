package com.musicgb.player

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class HiResSettingsActivity : AppCompatActivity() {
    
    private val sampleRates = listOf(
        "44.1 kHz" to 44100,
        "48 kHz" to 48000,
        "96 kHz" to 96000,
        "192 kHz" to 192000
    )
    
    private val bitDepths = listOf(
        "16-bit" to 16,
        "24-bit" to 24,
        "32-bit float" to 32
    )
    
    private var selectedSampleRate = 48000
    private var selectedBitDepth = 24
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#121212"))
            setPadding(32, 64, 32, 32)
        }
        
        val title = TextView(this).apply {
            text = "AJUSTES Hi-Res"
            textSize = 24f
            setTextColor(Color.parseColor("#1DB954"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 32)
        }
        rootLayout.addView(title)
        
        // Selector de sample rate
        val srLabel = TextView(this).apply {
            text = "Frecuencia de muestreo:"
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(0, 16, 0, 8)
        }
        rootLayout.addView(srLabel)
        
        sampleRates.forEach { (name, rate) ->
            val button = Button(this).apply {
                text = if (rate == selectedSampleRate) "✅ " else name
                textSize = 14f
                setBackgroundColor(Color.parseColor("#282828"))
                setTextColor(Color.WHITE)
                gravity = Gravity.START
                setOnClickListener {
                    selectedSampleRate = rate
                    Toast.makeText(this@HiResSettingsActivity, "Seleccionado: ", Toast.LENGTH_SHORT).show()
                    recreate()
                }
            }
            rootLayout.addView(button)
        }
        
        // Selector de bit depth
        val bdLabel = TextView(this).apply {
            text = "\nProfundidad de bits:"
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(0, 24, 0, 8)
        }
        rootLayout.addView(bdLabel)
        
        bitDepths.forEach { (name, depth) ->
            val button = Button(this).apply {
                text = if (depth == selectedBitDepth) "✅ " else name
                textSize = 14f
                setBackgroundColor(Color.parseColor("#282828"))
                setTextColor(Color.WHITE)
                gravity = Gravity.START
                setOnClickListener {
                    selectedBitDepth = depth
                    Toast.makeText(this@HiResSettingsActivity, "Seleccionado: ", Toast.LENGTH_SHORT).show()
                    recreate()
                }
            }
            rootLayout.addView(button)
        }
        
        // Botón aplicar
        val applyButton = Button(this).apply {
            text = "\nAPLICAR AJUSTES"
            textSize = 16f
            setBackgroundColor(Color.parseColor("#1DB954"))
            setTextColor(Color.BLACK)
            setPadding(32, 16, 32, 16)
            setOnClickListener {
                Toast.makeText(
                    this@HiResSettingsActivity,
                    "Ajustes aplicados:  Hz / -bit",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
        rootLayout.addView(applyButton)
        
        setContentView(rootLayout)
    }
}
