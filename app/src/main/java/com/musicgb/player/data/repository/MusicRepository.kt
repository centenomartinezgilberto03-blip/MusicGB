package com.musicgb.player.data.repository

import android.content.Context
import com.musicgb.player.data.MusicDatabase
import com.musicgb.player.data.models.Track
import com.musicgb.player.data.models.Album
import com.musicgb.player.data.models.Artist
import com.musicgb.player.data.models.Playlist
import kotlinx.coroutines.flow.Flow

class MusicRepository(context: Context) {
    private val db = MusicDatabase.getInstance(context)
    private val trackDao = db.trackDao()
    private val albumDao = db.albumDao()
    private val artistDao = db.artistDao()
    private val playlistDao = db.playlistDao()

    val allTracks: Flow<List<Track>> = trackDao.getAllTracks()
    val allAlbums: Flow<List<Album>> = albumDao.getAllAlbums()
    val allArtists: Flow<List<Artist>> = artistDao.getAllArtists()
    val allPlaylists: Flow<List<Playlist>> = playlistDao.getAllPlaylists()
    val favoriteTracks: Flow<List<Track>> = trackDao.getFavoriteTracks()

    suspend fun insertTracks(tracks: List<Track>) = trackDao.insertTracks(tracks)
    suspend fun deleteAllTracks() = trackDao.deleteAllTracks()
    suspend fun updateTrack(track: Track) = trackDao.updateTrack(track)

    suspend fun insertAlbums(albums: List<Album>) = albumDao.insertAlbums(albums)
    suspend fun deleteAllAlbums() = albumDao.deleteAllAlbums()

    suspend fun insertArtists(artists: List<Artist>) = artistDao.insertArtists(artists)
    suspend fun deleteAllArtists() = artistDao.deleteAllArtists()

    suspend fun createPlaylist(name: String, trackIds: String): Long {
        val playlist = Playlist(name = name, trackIds = trackIds, dateCreated = System.currentTimeMillis())
        return playlistDao.insertPlaylist(playlist)
    }

    suspend fun updatePlaylist(playlist: Playlist) = playlistDao.updatePlaylist(playlist)
    suspend fun deletePlaylist(playlist: Playlist) = playlistDao.deletePlaylist(playlist)
    suspend fun getPlaylistById(id: Long): Playlist? = playlistDao.getPlaylistById(id)
}
