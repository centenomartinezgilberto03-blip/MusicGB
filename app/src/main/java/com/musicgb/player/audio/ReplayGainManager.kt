package com.musicgb.player.audio

import android.util.Log
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

class ReplayGainManager {

    companion object {
        const val TAG = "ReplayGainManager"
    }

    data class ReplayGainInfo(
        val trackGainDb: Float,
        val trackPeak: Float,
        val albumGainDb: Float,
        val albumPeak: Float
    )

    fun readReplayGain(filePath: String): ReplayGainInfo? {
        return try {
            val file = File(filePath)
            if (!file.exists()) return null
            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tagOrCreateDefault

            val trackGain = parseGain(tag.getFirst(FieldKey.REPLAYGAIN_TRACK_GAIN))
            val trackPeak = tag.getFirst(FieldKey.REPLAYGAIN_TRACK_PEAK).toFloatOrNull() ?: 1f
            val albumGain = parseGain(tag.getFirst(FieldKey.REPLAYGAIN_ALBUM_GAIN))
            val albumPeak = tag.getFirst(FieldKey.REPLAYGAIN_ALBUM_PEAK).toFloatOrNull() ?: 1f

            ReplayGainInfo(trackGain, trackPeak, albumGain, albumPeak)
        } catch (e: Exception) {
            Log.d(TAG, "No ReplayGain tags: ${e.message}")
            null
        }
    }

    private fun parseGain(value: String?): Float {
        if (value.isNullOrBlank()) return 0f
        return try {
            value.replace("dB", "").replace(" ", "").trim().toFloat()
        } catch (e: Exception) { 0f }
    }

    fun extractEmbeddedArt(filePath: String): ByteArray? {
        return try {
            val file = File(filePath)
            if (!file.exists()) return null
            val audioFile = AudioFileIO.read(file)
            val artwork = audioFile.tag?.firstArtwork
            artwork?.binaryData
        } catch (e: Exception) {
            Log.d(TAG, "No artwork: ${e.message}")
            null
        }
    }
}
