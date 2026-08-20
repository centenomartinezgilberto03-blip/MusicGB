package com.musicgb.player.dsp

import android.media.audiofx.BassBoost
import android.media.audiofx.EnvironmentalReverb
import android.media.audiofx.Virtualizer
import android.util.Log

class DSPManager(audioSessionId: Int) {
    
    companion object {
        const val TAG = "DSPManager"
    }
    
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var environmentalReverb: EnvironmentalReverb? = null
    
    init {
        try {
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = true
                setStrength(0)
            }
        } catch (e: Exception) {
            Log.e(TAG, "BassBoost not supported: ")
        }
        
        try {
            virtualizer = Virtualizer(0, audioSessionId).apply {
                enabled = true
                setStrength(0)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Virtualizer not supported: ")
        }
        
        try {
            environmentalReverb = EnvironmentalReverb(0, audioSessionId).apply {
                enabled = true
                setDecayTime(1000)
            }
        } catch (e: Exception) {
            Log.e(TAG, "EnvironmentalReverb not supported: ")
        }
    }
    
    fun setBassStrength(strength: Int) {
        try {
            bassBoost?.setStrength(strength.toShort())
        } catch (e: Exception) {
            Log.e(TAG, "Error setting bass: ")
        }
    }
    
    fun setVirtualizerStrength(strength: Int) {
        try {
            virtualizer?.setStrength(strength.toShort())
        } catch (e: Exception) {
            Log.e(TAG, "Error setting virtualizer: ")
        }
    }
    
    fun setReverbDecay(decayMs: Int) {
        try {
            environmentalReverb?.setDecayTime(decayMs)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting reverb: ")
        }
    }
    
    fun enableAll() {
        bassBoost?.enabled = true
        virtualizer?.enabled = true
        environmentalReverb?.enabled = true
    }
    
    fun disableAll() {
        bassBoost?.enabled = false
        virtualizer?.enabled = false
        environmentalReverb?.enabled = false
    }
    
    fun release() {
        try {
            bassBoost?.release()
            virtualizer?.release()
            environmentalReverb?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing: ")
        }
    }
}
