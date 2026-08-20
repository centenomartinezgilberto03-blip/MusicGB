package com.musicgb.player.dsp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PresetManager(private val context: Context) {
    
    companion object {
        const val PRESETS_FILE = "equalizer_presets.json"
    }
    
    private val gson = Gson()
    
    data class EqualizerPreset(
        val name: String,
        val gains: FloatArray,
        val bassBoost: Int = 0,
        val trebleBoost: Int = 0,
        val preamp: Float = 0f
    )
    
    fun savePreset(preset: EqualizerPreset): Boolean {
        return try {
            val presets = loadPresets().toMutableList()
            presets.removeAll { it.name == preset.name }
            presets.add(preset)
            
            val json = gson.toJson(presets)
            context.openFileOutput(PRESETS_FILE, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun loadPresets(): List<EqualizerPreset> {
        return try {
            val json = context.openFileInput(PRESETS_FILE).bufferedReader().readText()
            val type = object : TypeToken<List<EqualizerPreset>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getDefaultPresets(): List<EqualizerPreset> {
        return listOf(
            EqualizerPreset("Normal", FloatArray(32) { 0f }),
            EqualizerPreset("Rock", FloatArray(32) { index ->
                when (index) {
                    in 0..5 -> 4f
                    in 6..12 -> 2f
                    in 13..20 -> -1f
                    in 21..26 -> 2f
                    else -> 4f
                }
            }),
            EqualizerPreset("Pop", FloatArray(32) { index ->
                when (index) {
                    in 0..4 -> 3f
                    in 5..12 -> 1f
                    in 13..20 -> 0f
                    in 21..26 -> 3f
                    else -> 2f
                }
            }),
            EqualizerPreset("Jazz", FloatArray(32) { index ->
                when (index) {
                    in 0..5 -> 3f
                    in 6..12 -> 1f
                    in 13..20 -> 0f
                    in 21..26 -> 2f
                    else -> 3f
                }
            }),
            EqualizerPreset("Clásica", FloatArray(32) { index ->
                when (index) {
                    in 0..5 -> 2f
                    in 6..12 -> 0f
                    in 13..20 -> 0f
                    in 21..26 -> 2f
                    else -> 1f
                }
            }),
            EqualizerPreset("Electrónica", FloatArray(32) { index ->
                when (index) {
                    in 0..3 -> 5f
                    in 4..10 -> 1f
                    in 11..16 -> -2f
                    in 17..24 -> 3f
                    else -> 5f
                }
            }),
            EqualizerPreset("Hip-Hop", FloatArray(32) { index ->
                when (index) {
                    in 0..4 -> 6f
                    in 5..10 -> 2f
                    in 11..16 -> -1f
                    in 17..24 -> 1f
                    else -> 3f
                }
            })
        )
    }
}
