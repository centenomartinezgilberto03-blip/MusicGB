package com.musicgb.player.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val trackIds: String,
    val dateCreated: Long = System.currentTimeMillis()
)
