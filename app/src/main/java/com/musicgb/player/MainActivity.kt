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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.musicgb.player.audio.MusicPlayerService
import com.musicgb.player.data.models.Track
import com.musicgb.player.ui.adapters.TrackAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    
    private var musicService: MusicPlayerService? = null
    private var isBound = false
    private lateinit var trackAdapter: TrackAdapter
    private val REQUEST_PERMISSION = 100
    
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
        
        // Configurar RecyclerView
        val trackList = findViewById<RecyclerView>(R.id.trackList)
        trackList.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(emptyList()) { track ->
            playTrack(track)
        }
        trackList.adapter = trackAdapter
        
        // Botón de escaneo
        findViewById<Button>(R.id.scanButton).setOnClickListener {
            requestPermissionAndScan()
        }
        
        // Botón play/pause
        findViewById<Button>(R.id.playPauseButton).setOnClickListener {
            musicService?.togglePlayPause()
        }
        
        // Vincular servicio
        val intent = Intent(this, MusicPlayerService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        
        // Solicitar permiso
        requestPermissionAndScan()
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
            withContext(Dispatchers.Main) {
                trackAdapter.updateTracks(tracks)
                Toast.makeText(
                    this@MainActivity,
                    "Se encontraron  canciones",
                    Toast.LENGTH_SHORT
                ).show()
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
                val track = Track(
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
                )
                tracks.add(track)
            }
        }
        return tracks
    }
    
    private fun playTrack(track: Track) {
        musicService?.playTrack(track.path)
        findViewById<TextView>(R.id.miniTitle).text = " - "
        findViewById<Button>(R.id.playPauseButton).text = "⏸"
    }
    
    override fun onDestroy() {
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        super.onDestroy()
    }
}
