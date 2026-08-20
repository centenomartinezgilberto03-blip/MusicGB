package com.musicgb.player.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.musicgb.player.R
import com.musicgb.player.data.models.Track

class TrackAdapter(
    private var tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    
    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.trackTitle)
        val artistText: TextView = itemView.findViewById(R.id.trackArtist)
        val durationText: TextView = itemView.findViewById(R.id.trackDuration)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.titleText.text = track.title
        holder.artistText.text = track.artist
        holder.durationText.text = formatDuration(track.duration)
        holder.itemView.setOnClickListener { onTrackClick(track) }
    }
    
    override fun getItemCount(): Int = tracks.size
    
    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
    
    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}
