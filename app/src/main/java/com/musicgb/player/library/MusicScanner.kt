package com.musicgb.player.library

import android.content.Context
import android.graphics.BitmapFactory
import android.provider.MediaStore
import com.musicgb.player.data.models.Track
import com.musicgb.player.data.models.Album
import com.musicgb.player.data.models.Artist
import com.musicgb.player.audio.ReplayGainManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.io.FileOutputStream

class MusicScanner(private val context: Context) {

    private val replayGainManager = ReplayGainManager()
    private val artCacheDir = File(context.cacheDir, "album_art").apply { mkdirs() }

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
            MediaStore.Audio.Media.BITRATE
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

            while (cursor.moveToNext()) {
                val path = cursor.getString(dataColumn) ?: ""
                val id = cursor.getLong(idColumn)

                var genre = ""
                var sampleRate = 0
                var replayGainTrack = 0f
                var replayGainAlbum = 0f
                var artPath: String? = null

                try {
                    val file = File(path)
                    if (file.exists()) {
                        val audioFile = AudioFileIO.read(file)
                        val tag = audioFile.tagOrCreateDefault
                        genre = tag.getFirst(FieldKey.GENRE) ?: ""
                        sampleRate = audioFile.audioHeader.sampleRateAsNumber ?: 0

                        val rg = replayGainManager.readReplayGain(path)
                        replayGainTrack = rg?.trackGainDb ?: 0f
                        replayGainAlbum = rg?.albumGainDb ?: 0f

                        val artwork = tag.firstArtwork
                        if (artwork != null) {
                            val artFile = File(artCacheDir, "${id}_art.jpg")
                            FileOutputStream(artFile).use { it.write(artwork.binaryData) }
                            artPath = artFile.absolutePath
                        }
                    }
                } catch (e: Exception) {
                }

                tracks.add(Track(
                    id = id,
                    title = cursor.getString(titleColumn) ?: "Desconocido",
                    artist = cursor.getString(artistColumn) ?: "Desconocido",
                    album = cursor.getString(albumColumn) ?: "Desconocido",
                    albumId = cursor.getLong(albumIdColumn),
                    artistId = cursor.getLong(artistIdColumn),
                    genre = genre,
                    path = path,
                    duration = cursor.getLong(durationColumn),
                    year = cursor.getInt(yearColumn),
                    trackNumber = cursor.getInt(trackColumn),
                    bitrate = cursor.getInt(bitrateColumn),
                    sampleRate = sampleRate,
                    isFavorite = false,
                    albumArtPath = artPath,
                    replayGainTrack = replayGainTrack,
                    replayGainAlbum = replayGainAlbum
                ))
            }
        }
        tracks
    }

    suspend fun extractAlbumsFromTracks(tracks: List<Track>): List<Album> = withContext(Dispatchers.IO) {
        tracks.groupBy { it.albumId }.map { (albumId, trackList) ->
            val first = trackList.first()
            Album(
                id = albumId,
                title = first.album,
                artist = first.artist,
                year = first.year,
                trackCount = trackList.size,
                albumArtPath = trackList.firstNotNullOfOrNull { it.albumArtPath }
            )
        }.sortedBy { it.title }
    }

    suspend fun extractArtistsFromTracks(tracks: List<Track>): List<Artist> = withContext(Dispatchers.IO) {
        tracks.groupBy { it.artistId }.map { (artistId, trackList) ->
            val albums = trackList.map { it.albumId }.distinct().size
            Artist(
                id = artistId,
                name = trackList.first().artist,
                albumCount = albums,
                trackCount = trackList.size
            )
        }.sortedBy { it.name }
    }
}
