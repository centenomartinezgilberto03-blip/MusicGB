package com.musicgb.player.audio

import android.media.audiofx.Visualizer

class VisualizerManager(audioSessionId: Int) {
    
    private var visualizer: Visualizer? = null
    private var waveformData: ByteArray? = null
    private var fftData: ByteArray? = null
    
    init {
        try {
            visualizer = Visualizer(audioSessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1]
                enabled = true
            }
        } catch (e: Exception) {
            // Visualizer not supported
        }
    }
    
    fun getWaveform(): ByteArray? {
        waveformData = ByteArray(visualizer?.captureSize ?: 0)
        visualizer?.getWaveForm(waveformData)
        return waveformData
    }
    
    fun getFft(): ByteArray? {
        fftData = ByteArray(visualizer?.captureSize ?: 0)
        visualizer?.getFft(fftData)
        return fftData
    }
    
    fun setDataCaptureListener(listener: Visualizer.OnDataCaptureListener) {
        visualizer?.setDataCaptureListener(listener, Visualizer.getMaxCaptureRate(), true, true)
    }
    
    fun release() {
        try {
            visualizer?.enabled = false
            visualizer?.release()
        } catch (e: Exception) {
            // Already released
        }
    }
}
