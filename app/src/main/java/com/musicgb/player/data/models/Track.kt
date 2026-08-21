package com.musicgb.player.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val artistId: Long,
    val genre: String,
    val path: String,
    val duration: Long,
    val year: Int,
    val trackNumber: Int,
    val bitrate: Int,
    val sampleRate: Int,
    val isFavorite: Boolean = false,
    val albumArtPath: String? = null,
    val replayGainTrack: Float = 0f,
    val replayGainAlbum: Float = 0f
)
