package com.musicgb.player.library

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import com.musicgb.player.data.models.Track
import com.musicgb.player.data.models.Album
import com.musicgb.player.data.models.Artist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicScanner(private val context: Context) {
    
    suspend fun scanAllMusic(): List<Track> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<Track>()
        val contentResolver = context.contentResolver
        
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.BITRATE,
            MediaStore.Audio.Media.SAMPLING_RATE,
            MediaStore.Audio.Media.GENRE
        )
        
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        
        contentResolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val bitrateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BITRATE)
            val sampleRateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SAMPLING_RATE)
            val genreColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
            
            while (cursor.moveToNext()) {
                val track = Track(
                    id = cursor.getLong(idColumn),
                    title = cursor.getString(titleColumn) ?: "Desconocido",
                    artist = cursor.getString(artistColumn) ?: "Desconocido",
                    album = cursor.getString(albumColumn) ?: "Desconocido",
                    albumId = cursor.getLong(albumIdColumn),
                    artistId = cursor.getLong(artistIdColumn),
                    genre = cursor.getString(genreColumn) ?: "Desconocido",
                    path = cursor.getString(dataColumn) ?: "",
                    duration = cursor.getLong(durationColumn),
                    year = cursor.getInt(yearColumn),
                    trackNumber = cursor.getInt(trackColumn),
                    bitrate = cursor.getInt(bitrateColumn),
                    sampleRate = cursor.getInt(sampleRateColumn),
                    isFavorite = false,
                    albumArtPath = null
                )
                tracks.add(track)
            }
        }
        tracks
    }
    
    suspend fun getAlbums(tracks: List<Track>): List<Album> = withContext(Dispatchers.IO) {
        val albumMap = mutableMapOf<Long, Album>()
        
        tracks.forEach { track ->
            val album = albumMap.getOrPut(track.albumId) {
                Album(
                    id = track.albumId,
                    title = track.album,
                    artist = track.artist,
                    year = track.year,
                    trackCount = 0
                )
            }
            albumMap[track.albumId] = album.copy(trackCount = album.trackCount + 1)
        }
        
        albumMap.values.toList()
    }
    
    suspend fun getArtists(tracks: List<Track>): List<Artist> = withContext(Dispatchers.IO) {
        val artistMap = mutableMapOf<Long, Artist>()
        
        tracks.forEach { track ->
            val artist = artistMap.getOrPut(track.artistId) {
                Artist(
                    id = track.artistId,
                    name = track.artist,
                    albumCount = 0,
                    trackCount = 0
                )
            }
            artistMap[track.artistId] = artist.copy(trackCount = artist.trackCount + 1)
        }
        
        artistMap.values.toList()
    }
}
