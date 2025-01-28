package com.example.musicapplication.activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplication.R;
import com.example.musicapplication.database.FavoritesDatabaseHelper;
import com.example.musicapplication.models.Song;
import com.example.musicapplication.models.User;
import com.example.musicapplication.utils.SessionManager;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class PlayerActivity extends AppCompatActivity {

    private static final String API_KEY = "API_KEY"; // Unesi svoj YouTube API ključ
    private YouTubePlayerView youtubePlayerView;
    private YouTubePlayer youtubePlayer;
    private TextView tvSongTitle;
    private ImageButton btnPlayPause, btnPrevious, btnNext, btnFavorite;
    private boolean isPlaying = true; // Za praćenje stanja reprodukcije

    private String videoId; // Video ID koji se prenosi iz HomeActivity
    private String songTitle; // Naslov pesme
    private String thumbnailUrl;
    private FavoritesDatabaseHelper dbHelper; // Pomoćna klasa za rad sa bazom

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Dobijanje prosleđenih podataka
        videoId = getIntent().getStringExtra("videoId");
        songTitle = getIntent().getStringExtra("songTitle");
        thumbnailUrl = getIntent().getStringExtra("thumbnailUrl");

        // Provera validnosti dobijenih podataka
        if (videoId == null || videoId.isEmpty() ||
                songTitle == null || songTitle.isEmpty()
        ) {

            Toast.makeText(this, "Greška: Podaci o pesmi nisu ispravni.", Toast.LENGTH_SHORT).show();
            finish();
            return;
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
        btnPrevious.setOnClickListener(v -> playPreviousSong());
        btnNext.setOnClickListener(v -> playNextSong());
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
                // Promeni ikonu na 'play' kada se pauzira pesma
                btnPlayPause.setImageResource(R.drawable.ic_play);
            } else {
                youtubePlayer.play();
                // Promeni ikonu na 'pause' kada se pesma pušta
                btnPlayPause.setImageResource(R.drawable.ic_pause);
            }
            isPlaying = !isPlaying;
        }
    }

    private void playPreviousSong() {
        // Ovdje možete implementirati logiku za prethodnu pesmu
        Toast.makeText(this, "PesmaNAZAD", Toast.LENGTH_SHORT).show();
    }

    private void playNextSong() {
        // Ovdje možete implementirati logiku za sledeću pesmu
        Toast.makeText(this, "PesmaNAPRED", Toast.LENGTH_SHORT).show();
    }

    private void addSongToFavorites() {
        if (videoId == null || videoId.isEmpty() || songTitle == null || songTitle.isEmpty()) {
            Toast.makeText(this, "Podaci o pesmi nisu validni.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dohvati trenutnog korisnika
        User currentUser = SessionManager.getCurrentUser(); // Pretpostavka: UserManager upravlja trenutnim korisnikom
        if (currentUser == null) {
            Toast.makeText(this, "Korisnik nije prijavljen.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dodaj pesmu u listu favorita korisnika
        Song favoriteSong = new Song(songTitle, videoId, thumbnailUrl); // Pretpostavka: Postoji klasa Song
        boolean isAdded = currentUser.addFavoriteSong(favoriteSong); // Pretpostavka: Metoda za dodavanje u listu favorita

        if (isAdded) {
            Toast.makeText(this, "Pesma dodata u favorite!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Pesma je već u favoritima.", Toast.LENGTH_SHORT).show();
        }

        // Opcionalno: Sačuvaj ažurirane favorite korisnika u lokalnu bazu
        saveFavoritesToDatabase(currentUser);
    }

    private void saveFavoritesToDatabase(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("favorites", null, null); // Očisti prethodne favorite korisnika

        for (Song song : user.getFavoriteSongs()) {
            ContentValues values = new ContentValues();
            values.put("title", song.getTitle());
            values.put("video_id", song.getVideoId());
            values.put("thumbnail_url", song.getThumbnailUrl());
            db.insert("favorites", null, values);
        }

        db.close();
    }
}
