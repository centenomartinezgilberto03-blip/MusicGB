package com.musicgb.player.dsp

import android.media.audiofx.Equalizer
import android.util.Log

class EqualizerManager(audioSessionId: Int) {
    
    companion object {
        const val NUM_BANDS = 32
        const val TAG = "EqualizerManager"
    }
    
    private var equalizer: Equalizer? = null
    
    private val frequencies = floatArrayOf(
        20f, 31f, 45f, 63f, 88f, 125f, 175f, 250f,
        350f, 500f, 700f, 1000f, 1400f, 2000f, 2800f, 4000f,
        5600f, 8000f, 10000f, 12000f, 14000f, 16000f, 18000f, 20000f,
        12500f, 15000f, 17500f, 20000f, 22000f, 25000f, 28000f, 32000f
    )
    
    init {
        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Equalizer not supported: ${e.message}")
        }
    }
    
    fun setBandGain(band: Int, gainDb: Float) {
        try {
            equalizer?.setBandLevel(band.toShort(), (gainDb * 1000).toInt().toShort())
        } catch (e: Exception) {}
    }
    
    fun setAllBands(gains: FloatArray) {
        gains.forEachIndexed { index, gain -> setBandGain(index, gain) }
    }
    
    fun getBandGain(band: Int): Float {
        return try { equalizer?.getBandLevel(band.toShort())?.toFloat()?.div(1000) ?: 0f } catch (e: Exception) { 0f }
    }
    
    fun getFrequencies(): FloatArray = frequencies
    fun setBassBoost(strength: Float) { for (i in 0 until 8) setBandGain(i, strength * (1 - i * 0.1f)) }
    fun setTrebleBoost(strength: Float) { for (i in 24 until 32) setBandGain(i, strength * ((i - 23) * 0.1f)) }
    fun setPreamp(gainDb: Float) { equalizer?.let { eq -> for (i in 0 until eq.numberOfBands) eq.setBandLevel(i.toShort(), (gainDb * 1000).toInt().toShort()) } }
    fun enable() { equalizer?.enabled = true }
    fun disable() { equalizer?.enabled = false }
    fun release() { try { equalizer?.release() } catch (e: Exception) {} }
}
