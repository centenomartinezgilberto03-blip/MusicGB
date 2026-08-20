package com.musicgb.player.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.musicgb.player.data.models.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks ORDER BY title")
    fun getAllTracks(): Flow<List<Track>>
    
    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrackById(id: Long): Track?
    
    @Query("SELECT * FROM tracks WHERE isFavorite = 1")
    fun getFavoriteTracks(): Flow<List<Track>>
    
    @Insert
    suspend fun insertTrack(track: Track)
    
    @Insert
    suspend fun insertTracks(tracks: List<Track>)
    
    @Update
    suspend fun updateTrack(track: Track)
    
    @Delete
    suspend fun deleteTrack(track: Track)
    
    @Query("DELETE FROM tracks")
    suspend fun deleteAllTracks()
    
    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun getTrackCount(): Int
}
