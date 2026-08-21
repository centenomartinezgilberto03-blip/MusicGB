package com.musicgb.player.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.musicgb.player.data.dao.TrackDao
import com.musicgb.player.data.dao.AlbumDao
import com.musicgb.player.data.dao.ArtistDao
import com.musicgb.player.data.dao.PlaylistDao
import com.musicgb.player.data.models.Track
import com.musicgb.player.data.models.Album
import com.musicgb.player.data.models.Artist
import com.musicgb.player.data.models.Playlist

@Database(
    entities = [Track::class, Album::class, Artist::class, Playlist::class],
    version = 2,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: MusicDatabase? = null

        fun getInstance(context: Context): MusicDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MusicDatabase::class.java,
                    "musicgb_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}
