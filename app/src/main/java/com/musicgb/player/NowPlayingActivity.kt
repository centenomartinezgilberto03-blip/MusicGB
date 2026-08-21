package com.musicgb.player

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
