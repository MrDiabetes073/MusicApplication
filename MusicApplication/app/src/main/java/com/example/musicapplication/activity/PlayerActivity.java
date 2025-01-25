package com.example.musicapplication.activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplication.R;
import com.example.musicapplication.database.FavoritesDatabaseHelper;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class PlayerActivity extends AppCompatActivity {

    private static final String API_KEY = "API_KEY"; // Unesi svoj YouTube API ključ
    private YouTubePlayerView youtubePlayerView;
    private YouTubePlayer youtubePlayer;
    private TextView tvSongTitle;
    private Button btnPlayPause, btnPrevious, btnNext, btnFavorite;
    private boolean isPlaying = true; // Za praćenje stanja reprodukcije

    private String videoId; // Video ID koji se prenosi iz HomeActivity
    private String songTitle; // Naslov pesme
    private FavoritesDatabaseHelper dbHelper; // Pomoćna klasa za rad sa bazom

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Dobijanje prosleđenih podataka
        videoId = getIntent().getStringExtra("videoId");
        songTitle = getIntent().getStringExtra("songTitle");

        // Provera validnosti dobijenih podataka
        if (videoId == null || videoId.isEmpty()) {
            Toast.makeText(this, "Greška: Video ID nije prosleđen ili je prazan.", Toast.LENGTH_SHORT).show();
            finish(); // Zatvaramo aktivnost ako nema validnog video ID-a
            return;
        }

        if (songTitle == null || songTitle.isEmpty()) {
            Toast.makeText(this, "Greška: Naslov pesme nije prosleđen ili je prazan.", Toast.LENGTH_SHORT).show();
            songTitle = "Nepoznata pesma"; // Default vrednost ako naslov nedostaje
        }

        // Ostatak koda za inicijalizaciju
        tvSongTitle = findViewById(R.id.tv_song_title);
        youtubePlayerView = findViewById(R.id.youtube_player_view);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        btnFavorite = findViewById(R.id.button_favorite);

        // Prikaz naslova pesme
        tvSongTitle.setText(songTitle);

        // Inicijalizacija baze podataka
        dbHelper = new FavoritesDatabaseHelper(this);

        // Inicijalizacija YouTube plejera
        initializeYouTubePlayer();

        // Postavljanje listenera za dugmad
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnPrevious.setOnClickListener(v -> {
            Toast.makeText(this, "PesmaNAZAD", Toast.LENGTH_SHORT).show();
        });
        btnNext.setOnClickListener(v -> {
            Toast.makeText(this, "PesmaNAPRED", Toast.LENGTH_SHORT).show();
        });

        btnFavorite.setOnClickListener(v -> addSongToFavorites());
    }

    private void initializeYouTubePlayer() {
        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youtubePlayer = youTubePlayer;

                // Proveri da li je videoId validan pre učitavanja videa
                if (videoId != null && !videoId.isEmpty()) {
                    youtubePlayer.cueVideo(videoId, 0); // Učitaj video na početku
                } else {
                    Toast.makeText(PlayerActivity.this, "Greška: Video ID nije validan.", Toast.LENGTH_SHORT).show();
                }
            }


            public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull String error) {
                Toast.makeText(PlayerActivity.this, "Greška pri učitavanju plejera: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void togglePlayPause() {
        if (youtubePlayer != null) {
            if (isPlaying) {
                youtubePlayer.pause();
                btnPlayPause.setText("Pusti");
            } else {
                youtubePlayer.play();
                btnPlayPause.setText("Pauza");
            }
            isPlaying = !isPlaying;
        }
    }

    private void addSongToFavorites() {
        if (videoId == null || videoId.isEmpty() || songTitle == null || songTitle.isEmpty()) {
            Toast.makeText(this, "Podaci o pesmi nisu validni.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", songTitle);
        values.put("video_id", videoId);

        long result = db.insert("favorites", null, values);
        if (result != -1) {
            Toast.makeText(this, "Pesma dodata u favorite!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Greška pri dodavanju pesme u favorite.", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
}
