package com.musicgb.player.library

import android.content.Context
import com.musicgb.player.data.models.Playlist
import com.musicgb.player.data.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PlaylistManager(private val context: Context) {
    
    suspend fun createPlaylist(name: String, tracks: List<Track>): Playlist = withContext(Dispatchers.IO) {
        val trackIds = tracks.joinToString(",") { it.id.toString() }
        Playlist(
            name = name,
            trackIds = trackIds,
            dateCreated = System.currentTimeMillis()
        )
    }
    
    suspend fun exportPlaylist(playlist: Playlist, tracks: List<Track>): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(context.getExternalFilesDir(null), playlist.name + ".m3u")
            val content = buildString {
                appendLine("#EXTM3U")
                tracks.forEach { track ->
                    appendLine("#EXTINF:" + (track.duration / 1000) + "," + track.artist + " - " + track.title)
                    appendLine(track.path)
                }
            }
            file.writeText(content)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun importPlaylist(filePath: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            file.readLines().filter { line ->
                line.startsWith("/") || line.startsWith("file://")
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
