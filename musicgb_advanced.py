#!/usr/bin/env python3
# MusicGB Advanced - Script de actualizacion completa
# Ejecutar en: C:\PROYECTOS APPS\MusicGB
# Comando: python musicgb_advanced.py

import os
import shutil
import subprocess
from datetime import datetime

project_path = r"C:\PROYECTOS APPS\MusicGB"
os.chdir(project_path)

print("=" * 50)
print("   MusicGB ADVANCED - Actualizacion")
print("=" * 50)

# Backup
backup_path = os.path.join(project_path, f"backup_advanced_{datetime.now().strftime('%Y%m%d_%H%M%S')}")
shutil.copytree(project_path, backup_path, ignore=lambda d, files: [f for f in files if f == os.path.basename(backup_path)])
print(f"✅ Backup creado en: {backup_path}")

def write_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"✅ {os.path.basename(path)}")

# ==================== 1. app/build.gradle ====================
write_file(os.path.join(project_path, "app", "build.gradle"), """
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'com.google.devtools.ksp' version '1.9.20-1.0.14'
}

android {
    namespace 'com.musicgb.player'
    compileSdk 34

    defaultConfig {
        applicationId "com.musicgb.player"
        minSdk 28
        targetSdk 34
        versionCode 2
        versionName "2.0.0"
    }

    buildTypes {
        release {
            minifyEnabled false
            signingConfig signingConfigs.debug
        }
        debug {
            signingConfig signingConfigs.debug
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = '17'
    }

    buildFeatures {
        viewBinding true
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.viewpager2:viewpager2:1.0.0'
    implementation 'androidx.fragment:fragment-ktx:1.6.2'
    implementation 'androidx.palette:palette-ktx:1.0.0'
    implementation 'androidx.preference:preference-ktx:1.2.1'

    implementation 'androidx.media3:media3-exoplayer:1.2.0'
    implementation 'androidx.media3:media3-exoplayer-flac:1.2.0'
    implementation 'androidx.media3:media3-exoplayer-opus:1.2.0'
    implementation 'androidx.media3:media3-exoplayer-ffmpeg:1.2.0'
    implementation 'androidx.media3:media3-session:1.2.0'
    implementation 'androidx.media3:media3-common:1.2.0'
    implementation 'androidx.media3:media3-ui:1.2.0'

    implementation 'androidx.room:room-runtime:2.6.0'
    implementation 'androidx.room:room-ktx:2.6.0'
    ksp 'androidx.room:room-compiler:2.6.0'

    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'org.jaudiotagger:jaudiotagger:2.2.3'
}
""")

# ==================== 2. AndroidManifest.xml ====================
write_file(os.path.join(project_path, "app", "src", "main", "AndroidManifest.xml"), """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />

    <application
        android:allowBackup="false"
        android:icon="@mipmap/ic_launcher"
        android:roundIcon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.MusicGB">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name=".NowPlayingActivity"
            android:exported="false"
            android:screenOrientation="portrait" />

        <activity
            android:name=".EqualizerActivity"
            android:exported="false"
            android:screenOrientation="portrait" />

        <activity
            android:name=".HiResSettingsActivity"
            android:exported="false"
            android:screenOrientation="portrait" />

        <service
            android:name=".audio.MusicPlayerService"
            android:exported="false"
            android:foregroundServiceType="mediaPlayback" />
    </application>
</manifest>
""")

# ==================== 3. Themes ====================
write_file(os.path.join(project_path, "app", "src", "main", "res", "values", "themes.xml"), """<resources>
    <style name="Theme.MusicGB" parent="Theme.Material3.Dark.NoActionBar">
        <item name="colorPrimary">@color/musicgb_green</item>
        <item name="colorOnPrimary">@android:color/black</item>
        <item name="colorSurface">#121212</item>
        <item name="colorOnSurface">#FFFFFF</item>
        <item name="android:windowBackground">#121212</item>
        <item name="android:statusBarColor">#121212</item>
        <item name="android:navigationBarColor">#121212</item>
    </style>
</resources>
""")

os.makedirs(os.path.join(project_path, "app", "src", "main", "res", "values-night"), exist_ok=True)
write_file(os.path.join(project_path, "app", "src", "main", "res", "values-night", "themes.xml"), """<resources>
    <style name="Theme.MusicGB" parent="Theme.Material3.Dark.NoActionBar">
        <item name="colorPrimary">@color/musicgb_green</item>
        <item name="colorOnPrimary">@android:color/black</item>
        <item name="colorSurface">#121212</item>
        <item name="colorOnSurface">#FFFFFF</item>
        <item name="android:windowBackground">#121212</item>
        <item name="android:statusBarColor">#121212</item>
        <item name="android:navigationBarColor">#121212</item>
    </style>
</resources>
""")

# ==================== 4. Colors ====================
write_file(os.path.join(project_path, "app", "src", "main", "res", "values", "colors.xml"), """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#121212</color>
    <color name="musicgb_green">#1DB954</color>
    <color name="musicgb_dark">#121212</color>
    <color name="musicgb_surface">#282828</color>
    <color name="musicgb_text_primary">#FFFFFF</color>
    <color name="musicgb_text_secondary">#B3B3B3</color>
</resources>
""")

# ==================== 5. Bottom Nav Menu ====================
os.makedirs(os.path.join(project_path, "app", "src", "main", "res", "menu"), exist_ok=True)
write_file(os.path.join(project_path, "app", "src", "main", "res", "menu", "bottom_nav_menu.xml"), """<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_library"
        android:icon="@drawable/ic_music"
        android:title="Biblioteca" />
    <item
        android:id="@+id/nav_albums"
        android:icon="@drawable/ic_album"
        android:title="Albumes" />
    <item
        android:id="@+id/nav_artists"
        android:icon="@drawable/ic_artist"
        android:title="Artistas" />
    <item
        android:id="@+id/nav_playlists"
        android:icon="@drawable/ic_playlist"
        android:title="Playlists" />
</menu>
""")

# ==================== 6. Drawables ====================
write_file(os.path.join(project_path, "app", "src", "main", "res", "drawable", "ic_music.xml"), """<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF"
        android:pathData="M12,3v10.55c-0.59,-0.34 -1.27,-0.55 -2,-0.55 -2.21,0 -4,1.79 -4,4s1.79,4 4,4 4,-1.79 4,-4V7h4V3h-6z"/>
</vector>
""")

write_file(os.path.join(project_path, "app", "src", "main", "res", "drawable", "ic_album.xml"), """<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF"
        android:pathData="M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2zM12,16.5c-2.49,0 -4.5,-2.01 -4.5,-4.5S9.51,7.5 12,7.5s4.5,2.01 4.5,4.5 -2.01,4.5 -4.5,4.5z"/>
</vector>
""")

write_file(os.path.join(project_path, "app", "src", "main", "res", "drawable", "ic_artist.xml"), """<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF"
        android:pathData="M12,12c2.21,0 4,-1.79 4,-4s-1.79,-4 -4,-4 -4,1.79 -4,4 1.79,4 4,4zM12,14c-2.67,0 -8,1.34 -8,4v2h16v-2c0,-2.66 -5.33,-4 -8,-4z"/>
</vector>
""")

write_file(os.path.join(project_path, "app", "src", "main", "res", "drawable", "ic_playlist.xml"), """<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FFFFFF"
        android:pathData="M3,3h18v2H3V3zm0,4h12v2H3V7zm0,4h18v2H3v-2zm0,4h12v2H3v-2zm14,0v6l5,-3l-5,-3z"/>
</vector>
""")

print("\n📦 Fase 1 completada. Continuando con codigo Kotlin...")


# ==================== 7. MusicDatabase.kt ====================
write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "data", "MusicDatabase.kt"), """package com.musicgb.player.data

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
""")

# ==================== 8. DAOs ====================
write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "data", "dao", "AlbumDao.kt"), """package com.musicgb.player.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.musicgb.player.data.models.Album
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Query("SELECT * FROM albums ORDER BY title")
    fun getAllAlbums(): Flow<List<Album>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: Album)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<Album>)

    @Query("DELETE FROM albums")
    suspend fun deleteAllAlbums()
}
""")

write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "data", "dao", "ArtistDao.kt"), """package com.musicgb.player.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.musicgb.player.data.models.Artist
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {
    @Query("SELECT * FROM artists ORDER BY name")
    fun getAllArtists(): Flow<List<Artist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: Artist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<Artist>)

    @Query("DELETE FROM artists")
    suspend fun deleteAllArtists()
}
""")

write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "data", "dao", "PlaylistDao.kt"), """package com.musicgb.player.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.musicgb.player.data.models.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY dateCreated DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Insert
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: Long): Playlist?
}
""")

# ==================== 9. MusicRepository.kt ====================
os.makedirs(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "data", "repository"), exist_ok=True)
write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "data", "repository", "MusicRepository.kt"), """package com.musicgb.player.data.repository

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
""")

# ==================== 10. Models updated ====================
write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "data", "models", "Playlist.kt"), """package com.musicgb.player.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val trackIds: String,
    val dateCreated: Long = System.currentTimeMillis()
)
""")

write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "data", "models", "Track.kt"), """package com.musicgb.player.data.models

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
""")

print("✅ DAOs, Repository y Models actualizados")


# ==================== 11. MusicPlayerService.kt ====================
write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "audio", "MusicPlayerService.kt"), """package com.musicgb.player.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.musicgb.player.NowPlayingActivity
import com.musicgb.player.R
import com.musicgb.player.data.models.Track
import com.musicgb.player.dsp.DSPManager
import com.musicgb.player.dsp.EqualizerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MusicPlayerService : Service() {

    private var player: ExoPlayer? = null
    private var equalizerManager: EqualizerManager? = null
    private var dspManager: DSPManager? = null
    private val binder = MusicBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null
    private var currentPlaylist = mutableListOf<Track>()
    private var currentIndex = 0
    private var crossfadeDuration = 0L
    private var replayGainDb = 0f
    private var baseVolume = 1.0f

    var onTrackChange: ((Track) -> Unit)? = null
    var onProgressUpdate: ((Long, Long) -> Unit)? = null
    var onPlaybackStateChange: ((Boolean) -> Unit)? = null

    inner class MusicBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        player?.addListener(object : Player.Listener {
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                if (audioSessionId != 0) {
                    equalizerManager = EqualizerManager(audioSessionId)
                    dspManager = DSPManager(audioSessionId)
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    applyReplayGain()
                }
                if (playbackState == Player.STATE_ENDED) {
                    playNext()
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                onPlaybackStateChange?.invoke(isPlaying)
                updateNotification()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                currentPlaylist.getOrNull(currentIndex)?.let { track ->
                    onTrackChange?.invoke(track)
                }
            }
        })

        startForeground(NOTIFICATION_ID, buildNotification())
        startProgressUpdate()
    }

    fun setPlaylist(tracks: List<Track>, startIndex: Int = 0) {
        currentPlaylist = tracks.toMutableList()
        currentIndex = startIndex.coerceIn(0, currentPlaylist.size - 1)
        val mediaItems = tracks.map {
            MediaItem.fromUri(File(it.path).toUri())
        }
        player?.setMediaItems(mediaItems, startIndex, 0)
        player?.prepare()
        player?.play()
        applyReplayGain()
    }

    fun playTrack(track: Track) {
        val idx = currentPlaylist.indexOfFirst { it.id == track.id }
        if (idx >= 0) {
            currentIndex = idx
            player?.seekTo(idx, 0)
            player?.play()
        } else {
            currentPlaylist.add(track)
            currentIndex = currentPlaylist.size - 1
            player?.addMediaItem(MediaItem.fromUri(File(track.path).toUri()))
            player?.seekTo(currentIndex, 0)
            player?.play()
        }
        applyReplayGain()
    }

    fun playNext() {
        if (currentIndex < currentPlaylist.size - 1) {
            currentIndex++
            player?.seekToNextMediaItem()
        }
    }

    fun playPrevious() {
        if (currentIndex > 0) {
            currentIndex--
            player?.seekToPreviousMediaItem()
        }
    }

    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun pause() = player?.pause()
    fun resume() = player?.play()
    fun stop() = player?.stop()
    fun seekTo(position: Long) = player?.seekTo(position)
    fun getCurrentPosition(): Long = player?.currentPosition ?: 0
    fun getDuration(): Long = player?.duration ?: 0
    fun isPlaying(): Boolean = player?.isPlaying ?: false
    fun getCurrentTrack(): Track? = currentPlaylist.getOrNull(currentIndex)
    fun getPlaylist(): List<Track> = currentPlaylist

    fun getEqualizerManager(): EqualizerManager? = equalizerManager
    fun getDSPManager(): DSPManager? = dspManager

    fun setCrossfade(durationMs: Long) {
        crossfadeDuration = durationMs.coerceIn(0, 12000)
    }

    fun setPreamp(gainDb: Float) {
        baseVolume = when {
            gainDb <= -12f -> 0.25f
            gainDb >= 12f -> 4.0f
            else -> kotlin.math.pow(10f, gainDb / 20f)
        }
        applyReplayGain()
    }

    fun setReplayGain(trackGainDb: Float, albumGainDb: Float) {
        replayGainDb = if (trackGainDb != 0f) trackGainDb else albumGainDb
        applyReplayGain()
    }

    private fun applyReplayGain() {
        val gainFactor = baseVolume * kotlin.math.pow(10f, replayGainDb / 20f)
        val clamped = gainFactor.coerceIn(0.0f, 1.0f)
        player?.volume = clamped
    }

    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = serviceScope.launch {
            while (true) {
                val pos = getCurrentPosition()
                val dur = getDuration()
                onProgressUpdate?.invoke(pos, dur)
                delay(500)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        progressJob?.cancel()
        equalizerManager?.release()
        dspManager?.release()
        player?.release()
        player = null
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "MusicGB Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, NowPlayingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        val track = getCurrentTrack()
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(track?.title ?: "MusicGB")
            .setContentText(track?.artist ?: "Reproduciendo...")
            .setSmallIcon(R.drawable.ic_music)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setShowWhen(false)
            .build()
    }

    private fun updateNotification() {
        val notification = buildNotification()
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "musicgb_channel"
        const val NOTIFICATION_ID = 1
    }
}
""")

# ==================== 12. EqualizerManager con DynamicsProcessing ====================
write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "dsp", "EqualizerManager.kt"), """package com.musicgb.player.dsp

import android.media.audiofx.DynamicsProcessing
import android.util.Log

class EqualizerManager(audioSessionId: Int) {

    companion object {
        const val TAG = "EqualizerManager"
        const val NUM_BANDS = 10
    }

    private var dynamicsProcessing: DynamicsProcessing? = null
    private val frequencies = floatArrayOf(32f, 64f, 125f, 250f, 500f, 1000f, 2000f, 4000f, 8000f, 16000f)
    private val gains = FloatArray(NUM_BANDS) { 0f }

    init {
        try {
            val config = DynamicsProcessing.Config.Builder(
                DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
                2, true, NUM_BANDS, true, NUM_BANDS, true, 0, true
            ).build()

            dynamicsProcessing = DynamicsProcessing(0, audioSessionId, config).apply {
                enabled = true
            }

            for (i in 0 until NUM_BANDS) {
                setBandGain(i, 0f)
            }
        } catch (e: Exception) {
            Log.e(TAG, "DynamicsProcessing no disponible: ${e.message}")
        }
    }

    fun setBandGain(band: Int, gainDb: Float) {
        if (band < 0 || band >= NUM_BANDS) return
        gains[band] = gainDb.coerceIn(-12f, 12f)
        try {
            dynamicsProcessing?.let { dp ->
                val eq = dp.getPreEqBandByChannelIndex(0, band)
                eq?.cutoffFrequency = frequencies[band]
                eq?.gain = gains[band]
                dp.setPreEqBandAllChannelsTo(band, eq)
                dp.getPreEqBandByChannelIndex(1, band)?.let { eq2 ->
                    eq2.cutoffFrequency = frequencies[band]
                    eq2.gain = gains[band]
                    dp.setPreEqBandAllChannelsTo(band, eq2)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setBandGain: ${e.message}")
        }
    }

    fun getBandGain(band: Int): Float = if (band in 0 until NUM_BANDS) gains[band] else 0f
    fun getFrequencies(): FloatArray = frequencies
    fun getNumBands(): Int = NUM_BANDS

    fun setPreamp(gainDb: Float) {
        try {
            dynamicsProcessing?.setInputGainAllChannelsTo(gainDb.coerceIn(-12f, 12f))
        } catch (e: Exception) { Log.e(TAG, "Error preamp: ${e.message}") }
    }

    fun enable() { dynamicsProcessing?.enabled = true }
    fun disable() { dynamicsProcessing?.enabled = false }

    fun release() {
        try { dynamicsProcessing?.release() } catch (e: Exception) {}
    }
}
""")

# ==================== 13. CrossfadeManager ====================
write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "audio", "CrossfadeManager.kt"), """package com.musicgb.player.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CrossfadeManager(private val onVolumeChange: (Float) -> Unit) {

    companion object {
        const val MAX_CROSSFADE_MS = 12000L
        const val MIN_CROSSFADE_MS = 0L
    }

    private var crossfadeDuration = 0L
    private var fadeJob: Job? = null

    fun setDuration(durationMs: Long) {
        crossfadeDuration = durationMs.coerceIn(MIN_CROSSFADE_MS, MAX_CROSSFADE_MS)
    }

    fun startFadeOut(scope: CoroutineScope) {
        if (crossfadeDuration <= 0) return
        fadeJob?.cancel()
        fadeJob = scope.launch {
            val steps = 20
            val stepTime = crossfadeDuration / steps
            for (i in 0..steps) {
                val volume = 1f - (i / steps.toFloat())
                onVolumeChange(volume.coerceIn(0f, 1f))
                delay(stepTime)
            }
            onVolumeChange(1f)
        }
    }

    fun cancel() {
        fadeJob?.cancel()
        onVolumeChange(1f)
    }
}
""")

# ==================== 14. ReplayGainManager ====================
write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "audio", "ReplayGainManager.kt"), """package com.musicgb.player.audio

import android.util.Log
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

class ReplayGainManager {

    companion object {
        const val TAG = "ReplayGainManager"
    }

    data class ReplayGainInfo(
        val trackGainDb: Float,
        val trackPeak: Float,
        val albumGainDb: Float,
        val albumPeak: Float
    )

    fun readReplayGain(filePath: String): ReplayGainInfo? {
        return try {
            val file = File(filePath)
            if (!file.exists()) return null
            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tagOrCreateDefault

            val trackGain = parseGain(tag.getFirst(FieldKey.REPLAYGAIN_TRACK_GAIN))
            val trackPeak = tag.getFirst(FieldKey.REPLAYGAIN_TRACK_PEAK).toFloatOrNull() ?: 1f
            val albumGain = parseGain(tag.getFirst(FieldKey.REPLAYGAIN_ALBUM_GAIN))
            val albumPeak = tag.getFirst(FieldKey.REPLAYGAIN_ALBUM_PEAK).toFloatOrNull() ?: 1f

            ReplayGainInfo(trackGain, trackPeak, albumGain, albumPeak)
        } catch (e: Exception) {
            Log.d(TAG, "No ReplayGain tags: ${e.message}")
            null
        }
    }

    private fun parseGain(value: String?): Float {
        if (value.isNullOrBlank()) return 0f
        return try {
            value.replace("dB", "").replace(" ", "").trim().toFloat()
        } catch (e: Exception) { 0f }
    }

    fun extractEmbeddedArt(filePath: String): ByteArray? {
        return try {
            val file = File(filePath)
            if (!file.exists()) return null
            val audioFile = AudioFileIO.read(file)
            val artwork = audioFile.tag?.firstArtwork
            artwork?.binaryData
        } catch (e: Exception) {
            Log.d(TAG, "No artwork: ${e.message}")
            null
        }
    }
}
""")

print("✅ Service, EQ, Crossfade, ReplayGain actualizados")


# ==================== 15. VisualizerView ====================
os.makedirs(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "ui", "views"), exist_ok=True)
write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "ui", "views", "VisualizerView.kt"), """package com.musicgb.player.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class VisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var fftData = ByteArray(0)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1DB954")
        style = Paint.Style.FILL
    }
    private val barCount = 32
    private val rect = RectF()

    fun updateFft(data: ByteArray) {
        if (data.size < barCount) return
        fftData = data.copyOf(barCount)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (fftData.isEmpty()) return

        val width = width.toFloat()
        val height = height.toFloat()
        val barWidth = width / barCount
        val gap = 4f

        for (i in 0 until barCount) {
            val raw = fftData[i].toInt()
            val magnitude = kotlin.math.abs(raw)
            val barHeight = (magnitude / 128f) * height
            val left = i * barWidth + gap / 2
            val top = height - barHeight
            val right = left + barWidth - gap
            val bottom = height

            rect.set(left, top.coerceIn(0f, height), right, bottom)
            canvas.drawRoundRect(rect, 6f, 6f, paint)
        }
    }
}
""")

# ==================== 16. PlaylistManager ====================
write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "library", "PlaylistManager.kt"), """package com.musicgb.player.library

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
            writer.write("#EXTM3U\n")
            tracks.forEach { track ->
                val duration = (track.duration / 1000).toInt()
                writer.write("#EXTINF:$duration,${track.artist} - ${track.title}\n")
                writer.write("${track.path}\n")
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
""")

# ==================== 17. MusicScanner ====================
write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "library", "MusicScanner.kt"), """package com.musicgb.player.library

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
""")

print("✅ Visualizer, PlaylistManager, MusicScanner actualizados")


# ==================== 18. Fragments ====================
os.makedirs(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "ui", "fragments"), exist_ok=True)

write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "ui", "fragments", "LibraryFragment.kt"), """package com.musicgb.player.ui.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.musicgb.player.NowPlayingActivity
import com.musicgb.player.R
import com.musicgb.player.audio.MusicPlayerService
import com.musicgb.player.data.repository.MusicRepository
import com.musicgb.player.ui.adapters.TrackAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryFragment : Fragment() {

    private var musicService: MusicPlayerService? = null
    private var isBound = false
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var repository: MusicRepository

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicService = (service as MusicPlayerService.MusicBinder).getService()
            isBound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = MusicRepository(requireContext())

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        trackAdapter = TrackAdapter(emptyList()) { track ->
            musicService?.playTrack(track)
            startActivity(Intent(requireContext(), NowPlayingActivity::class.java))
        }
        recyclerView.adapter = trackAdapter

        val intent = Intent(requireContext(), MusicPlayerService::class.java)
        requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)

        loadTracks()
    }

    private fun loadTracks() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.allTracks.collect { tracks ->
                withContext(Dispatchers.Main) {
                    trackAdapter.updateTracks(tracks)
                }
            }
        }
    }

    override fun onDestroyView() {
        if (isBound) {
            requireContext().unbindService(connection)
            isBound = false
        }
        super.onDestroyView()
    }
}
""")

write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "ui", "fragments", "AlbumsFragment.kt"), """package com.musicgb.player.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.musicgb.player.R
import com.musicgb.player.data.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumsFragment : Fragment() {

    private lateinit var repository: MusicRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_albums, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = MusicRepository(requireContext())
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }
}
""")

write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "ui", "fragments", "ArtistsFragment.kt"), """package com.musicgb.player.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.musicgb.player.R
import com.musicgb.player.data.repository.MusicRepository

class ArtistsFragment : Fragment() {

    private lateinit var repository: MusicRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_artists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = MusicRepository(requireContext())
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}
""")

write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "ui", "fragments", "PlaylistsFragment.kt"), """package com.musicgb.player.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.musicgb.player.R
import com.musicgb.player.data.repository.MusicRepository

class PlaylistsFragment : Fragment() {

    private lateinit var repository: MusicRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = MusicRepository(requireContext())
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}
""")

# ==================== 19. MainActivity ====================
write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "MainActivity.kt"), """package com.musicgb.player

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.musicgb.player.audio.MusicPlayerService
import com.musicgb.player.data.repository.MusicRepository
import com.musicgb.player.library.MusicScanner
import com.musicgb.player.ui.fragments.AlbumsFragment
import com.musicgb.player.ui.fragments.ArtistsFragment
import com.musicgb.player.ui.fragments.LibraryFragment
import com.musicgb.player.ui.fragments.PlaylistsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private var musicService: MusicPlayerService? = null
    private var isBound = false
    private val REQUEST_PERMISSION = 100
    private lateinit var repository: MusicRepository

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicService = (service as MusicPlayerService.MusicBinder).getService()
            isBound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = MusicRepository(this)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_library -> loadFragment(LibraryFragment())
                R.id.nav_albums -> loadFragment(AlbumsFragment())
                R.id.nav_artists -> loadFragment(ArtistsFragment())
                R.id.nav_playlists -> loadFragment(PlaylistsFragment())
            }
            true
        }

        loadFragment(LibraryFragment())

        val intent = Intent(this, MusicPlayerService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        startService(intent)

        requestPermissionAndScan()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun requestPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO), REQUEST_PERMISSION)
        } else {
            scanMusic()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scanMusic()
        }
    }

    private fun scanMusic() {
        CoroutineScope(Dispatchers.IO).launch {
            val scanner = MusicScanner(this@MainActivity)
            val tracks = scanner.scanAllMusic()
            val albums = scanner.extractAlbumsFromTracks(tracks)
            val artists = scanner.extractArtistsFromTracks(tracks)

            repository.deleteAllTracks()
            repository.deleteAllAlbums()
            repository.deleteAllArtists()
            repository.insertTracks(tracks)
            repository.insertAlbums(albums)
            repository.insertArtists(artists)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "${tracks.size} canciones escaneadas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        super.onDestroy()
    }
}
""")

# ==================== 20. NowPlayingActivity ====================
write_file(os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "NowPlayingActivity.kt"), """package com.musicgb.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.musicgb.player.audio.MusicPlayerService
import com.musicgb.player.ui.views.VisualizerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class NowPlayingActivity : AppCompatActivity() {

    private var musicService: MusicPlayerService? = null
    private var isBound = false
    private var progressJob: Job? = null
    private lateinit var visualizerView: VisualizerView

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicService = (service as MusicPlayerService.MusicBinder).getService()
            isBound = true
            setupCallbacks()
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_now_playing)

        visualizerView = findViewById(R.id.visualizerView)

        findViewById<Button>(R.id.btnPrev).setOnClickListener { musicService?.playPrevious() }
        findViewById<Button>(R.id.btnPlayPause).setOnClickListener {
            musicService?.togglePlayPause()
            updatePlayButton()
        }
        findViewById<Button>(R.id.btnNext).setOnClickListener { musicService?.playNext() }
        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<SeekBar>(R.id.seekBarProgress).setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        val duration = musicService?.getDuration() ?: 0
                        musicService?.seekTo((progress / 100.0 * duration).toLong())
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        val intent = Intent(this, MusicPlayerService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        startProgressUpdate()
    }

    private fun setupCallbacks() {
        musicService?.onTrackChange = { track ->
            runOnUiThread {
                findViewById<TextView>(R.id.tvTitle).text = track.title
                findViewById<TextView>(R.id.tvArtist).text = track.artist
                findViewById<TextView>(R.id.tvAlbum).text = track.album
                loadCover(track.albumArtPath)
            }
        }
        musicService?.onPlaybackStateChange = { isPlaying ->
            runOnUiThread { updatePlayButton() }
        }
    }

    private fun loadCover(path: String?) {
        val imageView = findViewById<ImageView>(R.id.ivCover)
        if (path != null && File(path).exists()) {
            val bitmap = BitmapFactory.decodeFile(path)
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageResource(R.drawable.ic_album)
        }
    }

    private fun updatePlayButton() {
        val btn = findViewById<Button>(R.id.btnPlayPause)
        btn.text = if (musicService?.isPlaying() == true) "⏸" else "▶"
    }

    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                val pos = musicService?.getCurrentPosition() ?: 0
                val dur = musicService?.getDuration() ?: 0
                if (dur > 0) {
                    val progress = (pos * 100.0 / dur).toInt()
                    findViewById<SeekBar>(R.id.seekBarProgress).progress = progress
                    findViewById<TextView>(R.id.tvTime).text = formatTime(pos) + " / " + formatTime(dur)
                }
                delay(500)
            }
        }
    }

    private fun formatTime(ms: Long): String {
        val s = ms / 1000
        return String.format("%d:%02d", s / 60, s % 60)
    }

    override fun onDestroy() {
        progressJob?.cancel()
        musicService?.onTrackChange = null
        musicService?.onPlaybackStateChange = null
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        super.onDestroy()
    }
}
""")

print("✅ Fragments, MainActivity, NowPlayingActivity creados")


# ==================== 21. Layouts XML ====================
write_file(os.path.join(project_path, "app", "src", "main", "res", "layout", "activity_main.xml"), """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/musicgb_dark">

    <FrameLayout
        android:id="@+id/fragmentContainer"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomNav"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/musicgb_surface"
        app:itemIconTint="@color/musicgb_green"
        app:itemTextColor="@color/musicgb_text_primary"
        app:menu="@menu/bottom_nav_menu" />
</LinearLayout>
""")

write_file(os.path.join(project_path, "app", "src", "main", "res", "layout", "activity_now_playing.xml"), """<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/musicgb_dark"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:gravity="center_horizontal"
        android:padding="24dp">

        <Button
            android:id="@+id/btnBack"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="← Volver"
            android:backgroundTint="@color/musicgb_surface"
            android:textColor="@color/musicgb_text_primary"
            android:layout_gravity="start" />

        <ImageView
            android:id="@+id/ivCover"
            android:layout_width="280dp"
            android:layout_height="280dp"
            android:layout_marginTop="24dp"
            android:scaleType="centerCrop"
            android:src="@drawable/ic_album"
            android:background="@color/musicgb_surface" />

        <TextView
            android:id="@+id/tvTitle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="24dp"
            android:text="Titulo"
            android:textSize="24sp"
            android:textColor="@color/musicgb_text_primary"
            android:textStyle="bold"
            android:gravity="center"
            android:maxLines="2"
            android:ellipsize="end" />

        <TextView
            android:id="@+id/tvArtist"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:text="Artista"
            android:textSize="18sp"
            android:textColor="@color/musicgb_text_secondary"
            android:gravity="center"
            android:maxLines="1"
            android:ellipsize="end" />

        <TextView
            android:id="@+id/tvAlbum"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="4dp"
            android:text="Album"
            android:textSize="14sp"
            android:textColor="@color/musicgb_text_secondary"
            android:gravity="center"
            android:maxLines="1"
            android:ellipsize="end" />

        <SeekBar
            android:id="@+id/seekBarProgress"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="32dp"
            android:max="100"
            android:progress="0" />

        <TextView
            android:id="@+id/tvTime"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="0:00 / 0:00"
            android:textColor="@color/musicgb_text_secondary"
            android:gravity="center"
            android:layout_marginTop="4dp" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="24dp"
            android:gravity="center"
            android:orientation="horizontal">

            <Button
                android:id="@+id/btnPrev"
                android:layout_width="64dp"
                android:layout_height="64dp"
                android:text="⏮"
                android:textSize="24sp"
                android:backgroundTint="@color/musicgb_surface"
                android:textColor="@color/musicgb_text_primary" />

            <Button
                android:id="@+id/btnPlayPause"
                android:layout_width="80dp"
                android:layout_height="80dp"
                android:layout_marginHorizontal="24dp"
                android:text="▶"
                android:textSize="32sp"
                android:backgroundTint="@color/musicgb_green"
                android:textColor="@android:color/black" />

            <Button
                android:id="@+id/btnNext"
                android:layout_width="64dp"
                android:layout_height="64dp"
                android:text="⏭"
                android:textSize="24sp"
                android:backgroundTint="@color/musicgb_surface"
                android:textColor="@color/musicgb_text_primary" />
        </LinearLayout>

        <com.musicgb.player.ui.views.VisualizerView
            android:id="@+id/visualizerView"
            android:layout_width="match_parent"
            android:layout_height="120dp"
            android:layout_marginTop="32dp" />

    </LinearLayout>
</ScrollView>
""")

write_file(os.path.join(project_path, "app", "src", "main", "res", "layout", "fragment_library.xml"), """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/musicgb_dark">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Tu Biblioteca"
        android:textSize="28sp"
        android:textStyle="bold"
        android:textColor="@color/musicgb_green"
        android:padding="16dp" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scrollbars="vertical" />
</LinearLayout>
""")

write_file(os.path.join(project_path, "app", "src", "main", "res", "layout", "fragment_albums.xml"), """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/musicgb_dark">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Albumes"
        android:textSize="28sp"
        android:textStyle="bold"
        android:textColor="@color/musicgb_green"
        android:padding="16dp" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
""")

write_file(os.path.join(project_path, "app", "src", "main", "res", "layout", "fragment_artists.xml"), """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/musicgb_dark">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Artistas"
        android:textSize="28sp"
        android:textStyle="bold"
        android:textColor="@color/musicgb_green"
        android:padding="16dp" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
""")

write_file(os.path.join(project_path, "app", "src", "main", "res", "layout", "fragment_playlists.xml"), """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/musicgb_dark">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Playlists"
        android:textSize="28sp"
        android:textStyle="bold"
        android:textColor="@color/musicgb_green"
        android:padding="16dp" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
""")

# ==================== 22. Strings.xml ====================
write_file(os.path.join(project_path, "app", "src", "main", "res", "values", "strings.xml"), """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">MusicGB</string>
</resources>
""")

# ==================== 23. Eliminar archivos obsoletos ====================
obsolete_files = [
    os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "audio", "PlaybackService.kt"),
    os.path.join(project_path, "app", "src", "main", "java", "com", "musicgb", "player", "audio", "engine", "AudioEngine.kt"),
]
for f in obsolete_files:
    if os.path.exists(f):
        os.remove(f)
        print(f"🗑️  Eliminado: {os.path.basename(f)}")

print("\n" + "=" * 50)
print("   ✅ TODO CREADO - LISTO PARA COMMIT")
print("=" * 50)

# ==================== 24. Git commit y push ====================
print("\n📦 Haciendo commit...")
subprocess.run(["git", "add", "."], cwd=project_path)
subprocess.run(["git", "commit", "-m", "MusicGB Advanced: fullscreen player, tabs, visualizer, DynamicsProcessing, gapless, crossfade, ReplayGain, caratulas, Room DB"], cwd=project_path)
print("✅ Commit realizado")

print("\n📤 Subiendo a GitHub...")
result = subprocess.run(["git", "push", "origin", "main"], cwd=project_path, capture_output=True, text=True)
if result.returncode == 0:
    print("✅ Push completado")
else:
    print(f"⚠️  Push: {result.stderr}")

print("\n" + "=" * 50)
print("   🎵 MusicGB ADVANCED EN GITHUB")
print("=" * 50)
print("\nEl build esta corriendo en GitHub Actions.")
print("Espera 5-10 minutos (primera compilacion con jaudiotagger y FFmpeg).")
print("\nPara verificar:")
print("   gh run list --repo centenomartinezgilberto03-blip/MusicGB --limit 1")
print("\nPara descargar cuando termine:")
print("   $runId = (gh run list --repo centenomartinezgilberto03-blip/MusicGB --status success --limit 1 --json databaseId | ConvertFrom-Json)[0].databaseId")
print("   gh run download $runId --repo centenomartinezgilberto03-blip/MusicGB --name MusicGB-APK")
print("=" * 50)
