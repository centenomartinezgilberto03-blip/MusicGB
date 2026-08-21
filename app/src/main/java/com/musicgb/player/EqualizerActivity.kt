package com.musicgb.player

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import com.musicgb.player.dsp.PresetManager
import com.musicgb.player.audio.MusicPlayerService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

class EqualizerActivity : AppCompatActivity() {
    
    private var musicService: MusicPlayerService? = null
    private var isBound = false
    private lateinit var presetManager: PresetManager
    private val bandSeekBars = mutableListOf<SeekBar>()
    private val bandLabels = mutableListOf<TextView>()
    
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.MusicBinder
            musicService = binder.getService()
            isBound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            musicService = null
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equalizer)
        
        presetManager = PresetManager(this)
        
        // Vincular servicio
        val intent = Intent(this, MusicPlayerService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        
        createEQBands()
        createPresets()
        setupControls()
    }
    
    private fun createEQBands() {
        val container = findViewById<LinearLayout>(R.id.eqBandsContainer)
        val eq = musicService?.getEqualizerManager()
        val frequencies = eq?.getFrequencies() ?: FloatArray(32) { 20f * it }
        
        for (i in 0 until 32) {
            // Crear contenedor vertical para cada banda
            val bandContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(8, 0, 8, 0)
            }
            
            // SeekBar vertical
            val seekBar = SeekBar(this).apply {
                max = 24
                progress = 12
                rotation = -90f
                layoutParams = LinearLayout.LayoutParams(
                    120,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            val gainDb = (progress - 12).toFloat()
                            eq?.setBandGain(i, gainDb)
                        }
                    }
                    override fun onStartTrackingTouch(sb: SeekBar?) {}
                    override fun onStopTrackingTouch(sb: SeekBar?) {}
                })
            }
            
            // Etiqueta de frecuencia
            val label = TextView(this).apply {
                text = formatFrequency(frequencies[i])
                textSize = 8f
                setTextColor(Color.parseColor("#B3B3B3"))
                gravity = Gravity.CENTER
            }
            
            bandContainer.addView(seekBar)
            bandContainer.addView(label)
            
            bandSeekBars.add(seekBar)
            bandLabels.add(label)
            container.addView(bandContainer)
        }
    }
    
    private fun createPresets() {
        val container = findViewById<LinearLayout>(R.id.presetContainer)
        val presets = presetManager.getDefaultPresets()
        
        presets.forEach { preset ->
            val button = Button(this).apply {
                text = preset.name
                textSize = 12f
                setBackgroundColor(Color.parseColor("#282828"))
                setTextColor(Color.WHITE)
                setPadding(8, 4, 8, 4)
                setOnClickListener {
                    applyPreset(preset.name)
                }
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(4, 0, 4, 0)
            }
            button.layoutParams = params
            container.addView(button)
        }
    }
    
    private fun setupControls() {
        val eq = musicService?.getEqualizerManager()
        val dsp = musicService?.getDSPManager()
        
        findViewById<SeekBar>(R.id.preampSeekBar).setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) eq?.setPreamp((progress - 12).toFloat())
                }
                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            }
        )
        
        findViewById<SeekBar>(R.id.bassSeekBar).setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) dsp?.setBassStrength(progress)
                }
                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            }
        )
        
        findViewById<SeekBar>(R.id.trebleSeekBar).setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) eq?.setTrebleBoost(progress / 100f)
                }
                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            }
        )
    }
    
    private fun applyPreset(name: String) {
        val presets = presetManager.getDefaultPresets()
        val preset = presets.find { it.name == name }
        
        preset?.let { p ->
            p.gains.forEachIndexed { index, gain ->
                bandSeekBars[index].progress = (gain + 12).toInt()
                musicService?.getEqualizerManager()?.setBandGain(index, gain)
            }
        }
    }
    
    private fun formatFrequency(freq: Float): String {
        return when {
            freq >= 1000 -> String.format("%.1fk", freq / 1000)
            else -> String.format("%.0f", freq)
        }
    }
    
    override fun onDestroy() {
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        super.onDestroy()
    }
}
