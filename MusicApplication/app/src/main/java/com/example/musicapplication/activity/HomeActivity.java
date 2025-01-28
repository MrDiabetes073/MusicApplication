package com.example.musicapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.R;
import com.example.musicapplication.adapters.SongAdapter;
import com.example.musicapplication.api.YouTubeApiClient;
import com.example.musicapplication.models.Song;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private List<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songList = new ArrayList<>();
        songAdapter = new SongAdapter(this, songList, this::onSongClicked);
        recyclerView.setAdapter(songAdapter);

        // Initialize Search Bar and Button
        EditText searchBar = findViewById(R.id.searchBar);
        ImageButton searchButton = findViewById(R.id.searchButton);
        ImageButton btnFavorites = findViewById(R.id.btnFavorites);

        // Search Button Click Action
        searchButton.setOnClickListener(v -> {
            String query = searchBar.getText().toString().trim();

            if (!query.isEmpty()) {
                // Reset list and perform new search
                songList.clear();
                songAdapter.notifyDataSetChanged();
                fetchSongs(query); // Fetch songs with search query
            } else {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            }
        });

        // Favorites Button Click Action
        btnFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        // Initialize YouTube API Client
        initializeYouTubeClient();

        // Fetch songs
        fetchSongs("");
    }

    /**
     * Ensures YouTube API Client is initialized before use.
     */
    private void initializeYouTubeClient() {
        try {
            YouTubeApiClient.initialize();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing YouTube API Client: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches songs from YouTube API and updates the RecyclerView.
     */
    private void fetchSongs(String query) {
        new Thread(() -> {
            try {
                // Fetch music results from YouTube API with filters
                List<SearchResult> results = YouTubeApiClient.searchMusic(query, "video", "10");

                // Process results and update the song list
                for (SearchResult result : results) {
                    String title = result.getSnippet().getTitle();
                    String videoId = result.getId().getVideoId();
                    String thumbnailUrl = result.getSnippet().getThumbnails().getDefault().getUrl();

                    if (videoId != null && !videoId.isEmpty() && thumbnailUrl != null) {
                        songList.add(new Song(title, videoId, thumbnailUrl));
                    } else {
                        Log.w(TAG, "Invalid data found for title: " + title);
                    }
                }

                // Notify adapter about data changes on the main thread
                runOnUiThread(() -> songAdapter.notifyDataSetChanged());

            } catch (IOException e) {
                Log.e(TAG, "Error fetching songs: " + e.getMessage(), e);
                runOnUiThread(() ->
                        Toast.makeText(this, "Error fetching songs. Please try again.", Toast.LENGTH_SHORT).show()
                );
            } catch (IllegalStateException e) {
                Log.e(TAG, "YouTube API Client not initialized: " + e.getMessage(), e);
            }
        }).start();
    }

    /**
     * Handles song item clicks and starts PlayerActivity with the selected song's videoId.
     */
    private void onSongClicked(Song song) {
        if (song.getVideoId() != null && !song.getVideoId().isEmpty()) {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("videoId", song.getVideoId()); // Prosledi ID videa
            intent.putExtra("songTitle", song.getTitle());   // Prosledi naslov pesme
            intent.putExtra("thumbnailUrl", song.getThumbnailUrl());

            startActivity(intent);
        } else {
            Toast.makeText(this, "Invalid video ID.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Attempted to play song with invalid video ID: " + song.getTitle());
        }
    }
}
