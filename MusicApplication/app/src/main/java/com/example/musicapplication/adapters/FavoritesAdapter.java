package com.example.musicapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.R;
import com.example.musicapplication.database.FavoritesDatabaseHelper;
import com.example.musicapplication.models.FavoriteSong;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private List<FavoriteSong> favoriteSongs;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public FavoritesAdapter(List<FavoriteSong> favoriteSongs, OnItemClickListener onItemClickListener, Context context) {
        this.favoriteSongs = favoriteSongs;
        this.onItemClickListener = onItemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteSong song = favoriteSongs.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return favoriteSongs.size();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {

        private TextView songTitleTextView;
        private ImageView songThumbnailImageView;
        private ImageButton removeFavoriteButton;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitleTextView = itemView.findViewById(R.id.songTitleTextView);
            songThumbnailImageView = itemView.findViewById(R.id.songThumbnailImageView);
            removeFavoriteButton = itemView.findViewById(R.id.removeFavoriteButton);
        }

        public void bind(FavoriteSong song) {
            songTitleTextView.setText(song.getTitle());
            Glide.with(context).load(song.getThumbnailUrl()).into(songThumbnailImageView);

            // Klik na pesmu
            itemView.setOnClickListener(view -> onItemClickListener.onItemClick(song));

            // Klik na dugme za uklanjanje
            removeFavoriteButton.setOnClickListener(view -> {
                // Uklanjanje iz baze
                FavoritesDatabaseHelper dbHelper = new FavoritesDatabaseHelper(context);
                dbHelper.deleteFavorite(song.getVideoId());

                // Uklanjanje iz liste i obaveštavanje adaptera
                favoriteSongs.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                notifyItemRangeChanged(getAdapterPosition(), favoriteSongs.size());
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(FavoriteSong song);
    }
}
