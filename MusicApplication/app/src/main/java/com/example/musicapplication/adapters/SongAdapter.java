package com.example.musicapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.R;
import com.example.musicapplication.models.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private final Context context;
    private final List<Song> songs;
    private final OnSongClickListener onSongClickListener;

    public SongAdapter(Context context, List<Song> songs, OnSongClickListener onSongClickListener) {
        this.context = context;
        this.songs = songs;
        this.onSongClickListener = onSongClickListener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);

        holder.songTitle.setText(song.getTitle());

        // Prikaz sličice pomoću Glide biblioteke
        Glide.with(context)
                .load(song.getThumbnailUrl())
                .placeholder(R.drawable.placeholder_image) // Postavi sliku koja će se prikazivati dok se thumbnail učitava
                .into(holder.thumbnail);

        // Klik listener za svaki item
        holder.itemView.setOnClickListener(v -> onSongClickListener.onSongClicked(song));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;
        ImageView thumbnail;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.songTitle);
            thumbnail = itemView.findViewById(R.id.songThumbnail); // Uveri se da je ID tačan
        }
    }

    public interface OnSongClickListener {
        void onSongClicked(Song song);
    }
}
