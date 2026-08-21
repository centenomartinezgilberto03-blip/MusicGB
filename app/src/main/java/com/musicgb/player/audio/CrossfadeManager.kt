package com.musicgb.player.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CrossfadeManager(private val onVolumeChange: (Float) -> Unit) {

    companion object {
        const val MAX_CROSSFADE_MS = 12000L
        const val MIN_CROSSFADE_MS = 0L
    }

    private var crossfadeDuration = 0L
    private var fadeJob: Job? = null

    fun setDuration(durationMs: Long) {
        crossfadeDuration = durationMs.coerceIn(MIN_CROSSFADE_MS, MAX_CROSSFADE_MS)
    }

    fun startFadeOut(scope: CoroutineScope) {
        if (crossfadeDuration <= 0) return
        fadeJob?.cancel()
        fadeJob = scope.launch {
            val steps = 20
            val stepTime = crossfadeDuration / steps
            for (i in 0..steps) {
                val volume = 1f - (i / steps.toFloat())
                onVolumeChange(volume.coerceIn(0f, 1f))
                delay(stepTime)
            }
            onVolumeChange(1f)
        }
    }

    fun cancel() {
        fadeJob?.cancel()
        onVolumeChange(1f)
    }
}
