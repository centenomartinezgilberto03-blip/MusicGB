package com.musicgb.player

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
