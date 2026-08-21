package com.musicgb.player.dsp

import android.media.audiofx.DynamicsProcessing
import android.util.Log

class EqualizerManager(audioSessionId: Int) {

    companion object {
        const val TAG = "EqualizerManager"
        const val NUM_BANDS = 10
    }

    private var dynamicsProcessing: DynamicsProcessing? = null
    private val frequencies = floatArrayOf(32f, 64f, 125f, 250f, 500f, 1000f, 2000f, 4000f, 8000f, 16000f)
    private val gains = FloatArray(NUM_BANDS) { 0f }

    init {
        try {
            val config = DynamicsProcessing.Config.Builder(
                DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
                2, true, NUM_BANDS, true, NUM_BANDS, true, 0, true
            ).build()

            dynamicsProcessing = DynamicsProcessing(0, audioSessionId, config).apply {
                enabled = true
            }

            for (i in 0 until NUM_BANDS) {
                setBandGain(i, 0f)
            }
        } catch (e: Exception) {
            Log.e(TAG, "DynamicsProcessing no disponible: ${e.message}")
        }
    }

    fun setBandGain(band: Int, gainDb: Float) {
        if (band < 0 || band >= NUM_BANDS) return
        gains[band] = gainDb.coerceIn(-12f, 12f)
        try {
            dynamicsProcessing?.let { dp ->
                val eq = dp.getPreEqBandByChannelIndex(0, band)
                eq?.cutoffFrequency = frequencies[band]
                eq?.gain = gains[band]
                dp.setPreEqBandAllChannelsTo(band, eq)
                dp.getPreEqBandByChannelIndex(1, band)?.let { eq2 ->
                    eq2.cutoffFrequency = frequencies[band]
                    eq2.gain = gains[band]
                    dp.setPreEqBandAllChannelsTo(band, eq2)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setBandGain: ${e.message}")
        }
    }

    fun getBandGain(band: Int): Float = if (band in 0 until NUM_BANDS) gains[band] else 0f
    fun getFrequencies(): FloatArray = frequencies
    fun getNumBands(): Int = NUM_BANDS

    fun setPreamp(gainDb: Float) {
        try {
            dynamicsProcessing?.setInputGainAllChannelsTo(gainDb.coerceIn(-12f, 12f))
        } catch (e: Exception) { Log.e(TAG, "Error preamp: ${e.message}") }
    }

    fun enable() { dynamicsProcessing?.enabled = true }
    fun disable() { dynamicsProcessing?.enabled = false }

    fun release() {
        try { dynamicsProcessing?.release() } catch (e: Exception) {}
    }
}
