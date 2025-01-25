package com.example.musicapplication.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.R;
import com.example.musicapplication.adapters.FavoritesAdapter;
import com.example.musicapplication.database.FavoritesDatabaseHelper;
import com.example.musicapplication.models.Song;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView favoritesRecyclerView;
    private FavoritesAdapter favoritesAdapter;
    private FavoritesDatabaseHelper dbHelper;
    private List<Song> favoriteSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Inicijalizacija RecyclerView
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new FavoritesDatabaseHelper(this);

        loadFavorites(); // Učitaj pesme iz baze

        // Postavljanje adaptera
        favoritesAdapter = new FavoritesAdapter(favoriteSongs, song -> {
            // Klik na stavku - pokretanje PlayerActivity
            Intent intent = new Intent(FavoritesActivity.this, PlayerActivity.class);
            intent.putExtra("videoId", song.getVideoId());
            intent.putExtra("songTitle", song.getTitle());
            startActivity(intent);
        });
        favoritesRecyclerView.setAdapter(favoritesAdapter);
    }

    private void loadFavorites() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("favorites", null, null, null, null, null, null);

        favoriteSongs = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex("title");
            int videoIdColumnIndex = cursor.getColumnIndex("video_id");
            int thumbnailUrlColumnIndex = cursor.getColumnIndex("thumbnail_url");

            // Proveri da li su validni indeksi
            if (titleColumnIndex == -1 || videoIdColumnIndex == -1 || thumbnailUrlColumnIndex == -1) {
                Toast.makeText(this, "Greška u bazi podataka. Kolone nisu pronađene.", Toast.LENGTH_SHORT).show();
                cursor.close();
                return;
            }

            do {
                String title = cursor.getString(titleColumnIndex);
                String videoId = cursor.getString(videoIdColumnIndex);
                String thumbnailUrl = cursor.getString(thumbnailUrlColumnIndex);

                // Dodaj pesmu sa svim potrebnim informacijama
                favoriteSongs.add(new Song(title, videoId, thumbnailUrl));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }


}
