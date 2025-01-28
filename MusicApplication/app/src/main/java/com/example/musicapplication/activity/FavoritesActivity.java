package com.example.musicapplication.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.R;
import com.example.musicapplication.adapters.FavoritesAdapter;
import com.example.musicapplication.database.FavoritesDatabaseHelper;
import com.example.musicapplication.models.FavoriteSong;
import com.example.musicapplication.models.Song;
import com.example.musicapplication.models.User;
import com.example.musicapplication.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private FavoritesDatabaseHelper dbHelper;
    private RecyclerView favoritesRecyclerView;
    private List<FavoriteSong> favoriteSongs;
    private FavoritesAdapter favoritesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Inicijalizacija baze podataka
        dbHelper = new FavoritesDatabaseHelper(this);

        // Inicijalizacija RecyclerView-a
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Učitavanje favorita iz baze
        loadFavorites();

        // Kreiranje i postavljanje adaptera
        favoritesAdapter = new FavoritesAdapter(favoriteSongs, song -> {
            // Klik na stavku - pokretanje PlayerActivity
            Intent intent = new Intent(FavoritesActivity.this, PlayerActivity.class);
            intent.putExtra("videoId", song.getVideoId());
            intent.putExtra("songTitle", song.getTitle());
            intent.putExtra("thumbnailUrl", song.getThumbnailUrl());
            startActivity(intent);
        }, this);
        favoritesRecyclerView.setAdapter(favoritesAdapter);
    }

    // Metoda za učitavanje favorita iz baze
    private void loadFavorites() {
        SQLiteDatabase db = dbHelper.getReadableDatabase(); // Koristi dbHelper ovde
        Cursor cursor = db.query("favorites", null, null, null, null, null, null);

        favoriteSongs = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex("title");
            int videoIdColumnIndex = cursor.getColumnIndex("video_id");
            int thumbnailUrlColumnIndex = cursor.getColumnIndex("thumbnail_url");

            do {
                String title = cursor.getString(titleColumnIndex);
                String videoId = cursor.getString(videoIdColumnIndex);
                String thumbnailUrl = cursor.getString(thumbnailUrlColumnIndex);

                // Kreiranje objekta FavoriteSong i dodavanje u listu
                favoriteSongs.add(new FavoriteSong(title, videoId, thumbnailUrl));
            } while (cursor.moveToNext());
        }

        // Zatvaranje kursora i baze
        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }
}
