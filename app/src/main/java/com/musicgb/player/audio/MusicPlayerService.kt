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