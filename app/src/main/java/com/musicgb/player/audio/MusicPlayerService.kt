package com.musicgb.player.audio

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.musicgb.player.dsp.DSPManager
import com.musicgb.player.dsp.EqualizerManager

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
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
            
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()
            
        player?.let {
            equalizerManager = EqualizerManager(it.audioSessionId)
            dspManager = DSPManager(it.audioSessionId)
        }
    }
    
    fun playTrack(path: String) {
        val mediaItem = MediaItem.fromUri("file://")
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }
    
    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }
    
    fun pause() {
        player?.pause()
    }
    
    fun resume() {
        player?.play()
    }
    
    fun stop() {
        player?.stop()
    }
    
    fun seekTo(position: Long) {
        player?.seekTo(position)
    }
    
    fun getCurrentPosition(): Long {
        return player?.currentPosition ?: 0
    }
    
    fun getDuration(): Long {
        return player?.duration ?: 0
    }
    
    fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }
    
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
}
