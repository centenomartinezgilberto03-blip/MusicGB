package com.musicgb.player.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CrossfadeManager {
    
    companion object {
        const val MAX_CROSSFADE_MS = 12000L
        const val MIN_CROSSFADE_MS = 0L
    }
    
    private var crossfadeDuration = 0L
    private var isCrossfading = false
    
    fun setCrossfadeDuration(durationMs: Long) {
        crossfadeDuration = durationMs.coerceIn(MIN_CROSSFADE_MS, MAX_CROSSFADE_MS)
    }
    
    fun getCrossfadeDuration(): Long = crossfadeDuration
    
    fun startCrossfade(
        currentTrackEndMs: Long,
        nextTrackStart: () -> Unit
    ) {
        if (crossfadeDuration > 0 && !isCrossfading) {
            isCrossfading = true
            
            CoroutineScope(Dispatchers.Main).launch {
                val fadeStartTime = currentTrackEndMs - crossfadeDuration
                if (fadeStartTime > 0) {
                    delay(fadeStartTime)
                    nextTrackStart()
                }
                isCrossfading = false
            }
        } else {
            nextTrackStart()
        }
    }
    
    fun stopCrossfade() {
        isCrossfading = false
    }
}
