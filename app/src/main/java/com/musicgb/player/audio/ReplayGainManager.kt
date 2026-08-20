package com.musicgb.player.audio

import android.media.MediaMetadataRetriever

class ReplayGainManager {
    
    fun readReplayGain(filePath: String): ReplayGainInfo? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            
            val trackGain = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_ALBUM
            )?.toFloatOrNull()
            
            val trackPeak = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_ARTIST
            )?.toFloatOrNull()
            
            retriever.release()
            
            if (trackGain != null && trackPeak != null) {
                ReplayGainInfo(trackGain, trackPeak)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    data class ReplayGainInfo(
        val gainDb: Float,
        val peak: Float
    )
}
