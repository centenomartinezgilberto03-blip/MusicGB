package com.musicgb.player.ui.fragments

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
