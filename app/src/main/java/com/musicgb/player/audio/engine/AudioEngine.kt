package com.musicgb.player.audio.engine

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import com.musicgb.player.data.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AudioEngine(context: Context) {
    
    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    private val _position = MutableStateFlow(0L)
    val position: StateFlow<Long> = _position
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration
    
    private val player: ExoPlayer
    
    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
            
        val renderersFactory = DefaultRenderersFactory(context)
            .setEnableAudioFloatOutput(true)
            .setEnableAudioOffload(true)
            
        player = ExoPlayer.Builder(context, renderersFactory)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setSeekBackIncrementMs(10000)
            .setSeekForwardIncrementMs(10000)
            .build()
            
        player.addListener(object : androidx.media3.exoplayer.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
            
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == androidx.media3.common.Player.STATE_READY) {
                    _duration.value = player.duration
                }
            }
        })
    }
    
    fun playTrack(track: Track) {
        _currentTrack.value = track
        val mediaItem = MediaItem.Builder()
            .setUri("file://")
            .setMediaId(track.id.toString())
            .build()
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }
    
    fun play() {
        player.play()
    }
    
    fun pause() {
        player.pause()
    }
    
    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }
    
    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }
    
    fun seekForward() {
        player.seekTo(player.currentPosition + 10000)
    }
    
    fun seekBackward() {
        player.seekTo(player.currentPosition - 10000)
    }
    
    fun stop() {
        player.stop()
        _currentTrack.value = null
        _isPlaying.value = false
    }
    
    fun getAudioSessionId(): Int {
        return player.audioSessionId
    }
    
    fun release() {
        player.release()
    }
}
