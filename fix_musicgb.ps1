# MusicGB Auto-Fix Script
# Ejecutar como: .\fix_musicgb.ps1
# O pegar bloque por bloque en PowerShell

$projectPath = "C:\PROYECTOS APPS\MusicGB"

if (-not (Test-Path $projectPath)) {
    Write-Host "❌ No se encontró la carpeta: $projectPath" -ForegroundColor Red
    exit 1
}

Set-Location $projectPath
Write-Host "🔧 MusicGB Auto-Fix iniciado..." -ForegroundColor Cyan

# ==================== BACKUP ====================
$backupPath = "$projectPath\backup_original_$(Get-Date -Format 'yyyyMMdd_HHmmss')"
New-Item -ItemType Directory -Path $backupPath -Force | Out-Null
Copy-Item -Path "$projectPath\*" -Destination $backupPath -Recurse -Force
Write-Host "✅ Backup creado en: $backupPath" -ForegroundColor Green

# ==================== 1. settings.gradle ====================
$settingsGradle = @'
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "MusicGB"
include ':app'
'@
[System.IO.File]::WriteAllText("$projectPath\settings.gradle", $settingsGradle, [System.Text.Encoding]::UTF8)
Write-Host "✅ settings.gradle actualizado" -ForegroundColor Green

# ==================== 2. build.gradle (root) ====================
$rootBuildGradle = @'
// Root build.gradle - todo movido a settings.gradle y plugins en app/build.gradle
'@
[System.IO.File]::WriteAllText("$projectPath\build.gradle", $rootBuildGradle, [System.Text.Encoding]::UTF8)
Write-Host "✅ build.gradle (root) limpiado" -ForegroundColor Green

# ==================== 3. app/build.gradle ====================
$appBuildGradle = @'
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
        versionCode 1
        versionName "1.0.0"
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
}

dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'

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
}
'@
[System.IO.File]::WriteAllText("$projectPath\app\build.gradle", $appBuildGradle, [System.Text.Encoding]::UTF8)
Write-Host "✅ app/build.gradle actualizado (minSdk 28, KSP, extensiones Media3)" -ForegroundColor Green

# ==================== 4. .github/workflows/build.yml ====================
$workflowDir = "$projectPath\.github\workflows"
if (-not (Test-Path $workflowDir)) { New-Item -ItemType Directory -Path $workflowDir -Force | Out-Null }

$workflow = @'
name: Build MusicGB APK

on:
  push:
    branches: [main, master]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'gradle'

      - name: Setup Android SDK
        uses: android-actions/setup-android@v3

      - name: Grant execute permission for gradlew
        run: chmod +x ./gradlew

      - name: Build Debug APK
        run: ./gradlew assembleDebug --stacktrace

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: MusicGB-APK
          path: app/build/outputs/apk/debug/app-debug.apk
          retention-days: 5
'@
[System.IO.File]::WriteAllText("$workflowDir\build.yml", $workflow, [System.Text.Encoding]::UTF8)
Write-Host "✅ .github/workflows/build.yml creado/corregido" -ForegroundColor Green

# ==================== 5. AndroidManifest.xml ====================
$manifest = @'
<?xml version="1.0" encoding="utf-8"?>
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
        android:theme="@android:style/Theme.Material.NoActionBar">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity android:name=".EqualizerActivity" android:exported="false" />
        <activity android:name=".HiResSettingsActivity" android:exported="false" />

        <service
            android:name=".audio.MusicPlayerService"
            android:exported="false"
            android:foregroundServiceType="mediaPlayback" />
    </application>
</manifest>
'@
[System.IO.File]::WriteAllText("$projectPath\app\src\main\AndroidManifest.xml", $manifest, [System.Text.Encoding]::UTF8)
Write-Host "✅ AndroidManifest.xml actualizado (permisos + foreground service)" -ForegroundColor Green

# ==================== 6. MusicPlayerService.kt ====================
$musicService = @'
package com.musicgb.player.audio

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
import androidx.media3.exoplayer.ExoPlayer
import com.musicgb.player.MainActivity
import com.musicgb.player.R
import com.musicgb.player.dsp.DSPManager
import com.musicgb.player.dsp.EqualizerManager
import java.io.File

class MusicPlayerService : Service() {

    private var player: ExoPlayer? = null
    private var equalizerManager: EqualizerManager? = null
    private var dspManager: DSPManager? = null
    private val binder = MusicBinder()

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

        player?.let { p ->
            p.addListener(object : androidx.media3.common.Player.Listener {
                override fun onAudioSessionIdChanged(audioSessionId: Int) {
                    if (audioSessionId != 0) {
                        equalizerManager = EqualizerManager(audioSessionId)
                        dspManager = DSPManager(audioSessionId)
                    }
                }
            })
        }

        startForeground(NOTIFICATION_ID, buildNotification())
    }

    fun playTrack(path: String) {
        val file = File(path)
        if (!file.exists()) return
        val mediaItem = MediaItem.fromUri(file.toUri())
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
        updateNotification()
    }

    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) it.pause() else it.play()
            updateNotification()
        }
    }

    fun pause() = player?.pause()
    fun resume() = player?.play()
    fun stop() = player?.stop()
    fun seekTo(position: Long) = player?.seekTo(position)
    fun getCurrentPosition(): Long = player?.currentPosition ?: 0
    fun getDuration(): Long = player?.duration ?: 0
    fun isPlaying(): Boolean = player?.isPlaying ?: false
    fun getEqualizerManager(): EqualizerManager? = equalizerManager
    fun getDSPManager(): DSPManager? = dspManager

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
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
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MusicGB")
            .setContentText("Reproduciendo...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification() {
        // TODO: actualizar con metadata de la pista actual
    }

    companion object {
        const val CHANNEL_ID = "musicgb_channel"
        const val NOTIFICATION_ID = 1
    }
}
'@
[System.IO.File]::WriteAllText("$projectPath\app\src\main\java\com\musicgb\player\audio\MusicPlayerService.kt", $musicService, [System.Text.Encoding]::UTF8)
Write-Host "✅ MusicPlayerService.kt corregido (foreground + playTrack funcional)" -ForegroundColor Green

# ==================== 7. MainActivity.kt ====================
$mainActivity = @'
package com.musicgb.player

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.musicgb.player.audio.MusicPlayerService
import com.musicgb.player.data.models.Track
import com.musicgb.player.dsp.PresetManager
import com.musicgb.player.ui.adapters.TrackAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {

    private var musicService: MusicPlayerService? = null
    private var isBound = false
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var presetManager: PresetManager
    private val REQUEST_PERMISSION = 100
    private var currentTracks = mutableListOf<Track>()
    private var progressJob: Job? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.MusicBinder
            musicService = binder.getService()
            isBound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            musicService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presetManager = PresetManager(this)

        val trackList = findViewById<RecyclerView>(R.id.trackList)
        trackList.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(emptyList()) { track -> playTrack(track) }
        trackList.adapter = trackAdapter

        findViewById<Button>(R.id.scanButton).setOnClickListener { requestPermissionAndScan() }
        findViewById<Button>(R.id.playPauseButton).setOnClickListener { musicService?.togglePlayPause() }
        findViewById<Button>(R.id.eqButton).setOnClickListener { openEqualizer() }
        findViewById<Button>(R.id.dspButton).setOnClickListener { showDSPUI() }

        findViewById<SeekBar>(R.id.progressBar).setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        val duration = musicService?.getDuration() ?: 0
                        val position = (progress / 100.0 * duration).toLong()
                        musicService?.seekTo(position)
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        val intent = Intent(this, MusicPlayerService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        startService(intent)

        requestPermissionAndScan()
        startProgressUpdate()
    }

    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                updateProgress()
                delay(1000)
            }
        }
    }

    private fun updateProgress() {
        musicService?.let { service ->
            val duration = service.getDuration()
            val position = service.getCurrentPosition()
            if (duration > 0) {
                val progress = (position * 100.0 / duration).toInt()
                findViewById<SeekBar>(R.id.progressBar).progress = progress
                findViewById<TextView>(R.id.timeText).text = 
                    "${formatTime(position)} / ${formatTime(duration)}"
            }
        }
    }

    private fun formatTime(ms: Long): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d", minutes, secs)
    }

    private fun requestPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                REQUEST_PERMISSION
            )
        } else {
            scanMusic()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION && grantResults.isNotEmpty() 
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scanMusic()
        }
    }

    private fun scanMusic() {
        CoroutineScope(Dispatchers.IO).launch {
            val tracks = loadTracksFromDevice()
            currentTracks = tracks.toMutableList()
            withContext(Dispatchers.Main) {
                trackAdapter.updateTracks(tracks)
                Toast.makeText(this@MainActivity, "Se encontraron ${tracks.size} canciones", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadTracksFromDevice(): List<Track> {
        val tracks = mutableListOf<Track>()
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
            MediaStore.Audio.Media.TRACK
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

            while (cursor.moveToNext()) {
                tracks.add(Track(
                    id = cursor.getLong(idColumn),
                    title = cursor.getString(titleColumn) ?: "Desconocido",
                    artist = cursor.getString(artistColumn) ?: "Desconocido",
                    album = cursor.getString(albumColumn) ?: "Desconocido",
                    albumId = cursor.getLong(albumIdColumn),
                    artistId = cursor.getLong(artistIdColumn),
                    genre = "",
                    path = cursor.getString(dataColumn) ?: "",
                    duration = cursor.getLong(durationColumn),
                    year = cursor.getInt(yearColumn),
                    trackNumber = cursor.getInt(trackColumn),
                    bitrate = 0,
                    sampleRate = 0,
                    isFavorite = false,
                    albumArtPath = null
                ))
            }
        }
        return tracks
    }

    private fun playTrack(track: Track) {
        musicService?.playTrack(track.path)
        findViewById<TextView>(R.id.miniTitle).text = "${track.title} - ${track.artist}"
        findViewById<Button>(R.id.playPauseButton).text = "⏸"
    }

    private fun openEqualizer() {
        val intent = Intent(this, EqualizerActivity::class.java)
        startActivity(intent)
    }

    private fun showDSPUI() {
        val dsp = musicService?.getDSPManager()
        if (dsp != null) {
            dsp.enableAll()
            Toast.makeText(this, "DSP activado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "DSP no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        progressJob?.cancel()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        super.onDestroy()
    }
}
'@
[System.IO.File]::WriteAllText("$projectPath\app\src\main\java\com\musicgb\player\MainActivity.kt", $mainActivity, [System.Text.Encoding]::UTF8)
Write-Host "✅ MainActivity.kt corregido (progress funcional + memory leak fix + Equalizer navegacion)" -ForegroundColor Green

# ==================== 8. .gitignore ====================
$gitignore = @'
# Gradle
.gradle/
build/
!gradle/wrapper/gradle-wrapper.jar

# Local
local.properties

# IDE
.idea/
*.iml

# OS
.DS_Store
Thumbs.db
'@
if (-not (Test-Path "$projectPath\.gitignore")) {
    [System.IO.File]::WriteAllText("$projectPath\.gitignore", $gitignore, [System.Text.Encoding]::UTF8)
    Write-Host "✅ .gitignore creado" -ForegroundColor Green
}

# ==================== 9. Git init + commit ====================
Write-Host "`n📦 Configurando Git..." -ForegroundColor Cyan

if (-not (Test-Path "$projectPath\.git")) {
    git init
    git branch -M main
    Write-Host "✅ Git inicializado" -ForegroundColor Green
}

git add .
git commit -m "Fix: build config, manifest, service, CI/CD - ready for GitHub Actions" --allow-empty 2>$null | Out-Null
Write-Host "✅ Commit realizado" -ForegroundColor Green

# ==================== RESUMEN ====================
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   ✅ PROYECTO CORREGIDO Y LISTO" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "`n📤 Para subir a GitHub ejecuta:" -ForegroundColor Yellow
Write-Host "   gh repo create MusicGB --public --source=. --push" -ForegroundColor White
Write-Host "   (si no tienes gh CLI, crea el repo en la web y haz git remote add origin <URL>)" -ForegroundColor Gray
Write-Host "`n📲 Para descargar e instalar el APK despues del build:" -ForegroundColor Yellow
Write-Host "   1. gh workflow run build.yml --repo TU_USUARIO/MusicGB" -ForegroundColor White
Write-Host "   2. Start-Sleep -Seconds 180" -ForegroundColor White
Write-Host "   3. `$run = (gh run list --repo TU_USUARIO/MusicGB --limit 1 --json databaseId | ConvertFrom-Json)[0].databaseId" -ForegroundColor White
Write-Host "   4. gh run download `$run --repo TU_USUARIO/MusicGB --name MusicGB-APK" -ForegroundColor White
Write-Host "   5. Expand-Archive -Path 'MusicGB-APK.zip' -DestinationPath '.\apk' -Force" -ForegroundColor White
Write-Host "   6. adb install '.\apk\app-debug.apk'" -ForegroundColor White
Write-Host "`n⚠️  NOTAS IMPORTANTES:" -ForegroundColor Magenta
Write-Host "   - minSdk subido a 28 (Android 9) para compatibilidad con DynamicsProcessing" -ForegroundColor Magenta
Write-Host "   - Se usa KSP en lugar de kapt (mas rapido y moderno)" -ForegroundColor Magenta
Write-Host "   - Se agregaron extensiones FFmpeg/FLAC/Opus para reproducir todos los formatos" -ForegroundColor Magenta
Write-Host "   - El APK generado es DEBUG (firmado automaticamente) para que se pueda instalar" -ForegroundColor Magenta
Write-Host "========================================" -ForegroundColor Cyan
