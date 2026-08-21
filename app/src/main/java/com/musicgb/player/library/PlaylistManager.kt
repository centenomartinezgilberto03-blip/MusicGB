package com.musicgb.player.library

import android.content.Context
import com.musicgb.player.data.models.Playlist
import com.musicgb.player.data.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PlaylistManager(private val context: Context) {

    suspend fun createM3U8(playlist: Playlist, tracks: List<Track>, fileName: String): File = withContext(Dispatchers.IO) {
        val file = File(context.getExternalFilesDir(null), "$fileName.m3u8")
        file.bufferedWriter().use { writer ->
            writer.write("#EXTM3U
")
            tracks.forEach { track ->
                val duration = (track.duration / 1000).toInt()
                writer.write("#EXTINF:$duration,${track.artist} - ${track.title}
")
                writer.write("${track.path}
")
            }
        }
        file
    }

    suspend fun parseM3U8(filePath: String): List<Pair<String, String?>> = withContext(Dispatchers.IO) {
        val file = File(filePath)
        if (!file.exists()) return@withContext emptyList()

        val entries = mutableListOf<Pair<String, String?>>()
        var currentInfo: String? = null

        file.readLines().forEach { line ->
            when {
                line.startsWith("#EXTINF:") -> {
                    currentInfo = line.substringAfter("#EXTINF:").substringAfter(",").trim()
                }
                line.startsWith("#") || line.isBlank() -> { }
                else -> {
                    entries.add(line.trim() to currentInfo)
                    currentInfo = null
                }
            }
        }
        entries
    }

    fun getPlaylistFile(name: String): File {
        return File(context.getExternalFilesDir(null), "$name.m3u8")
    }
}
