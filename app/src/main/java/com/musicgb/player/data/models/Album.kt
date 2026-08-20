package com.musicgb.player.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class Album(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val year: Int,
    val trackCount: Int,
    val albumArtPath: String? = null
)
