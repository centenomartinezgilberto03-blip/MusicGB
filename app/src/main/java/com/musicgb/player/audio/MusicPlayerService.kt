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
