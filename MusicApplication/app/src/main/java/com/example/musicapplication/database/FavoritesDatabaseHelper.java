package com.example.musicapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.musicapplication.models.FavoriteSong;
import com.example.musicapplication.models.Song;

import java.util.ArrayList;
import java.util.List;

public class FavoritesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 5;

    public static final String TABLE_FAVORITES = "favorites";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_SONG_ID = "song_id";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_VIDEO_ID = "video_id";
    public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";

    public FavoritesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                + COLUMN_USER_ID + " TEXT,"
                + COLUMN_SONG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SONG_TITLE + " TEXT,"
                + COLUMN_VIDEO_ID + " TEXT, "
                + COLUMN_THUMBNAIL_URL + " TEXT"
                + ")";
        db.execSQL(CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }

    // Dodavanje pesme u favorite
    public void addFavorite(Song song, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_SONG_TITLE, song.getTitle());
        values.put(COLUMN_VIDEO_ID, song.getVideoId());
        values.put(COLUMN_THUMBNAIL_URL, song.getThumbnailUrl());  // Dodajemo thumbnailUrl

        db.insert(TABLE_FAVORITES, null, values);
        db.close();
    }

    // Učitavanje favorita za određenog korisnika
    public List<FavoriteSong> getFavorites(String userId) {
        List<FavoriteSong> favoriteSongs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES, null, COLUMN_USER_ID + " = ?",
                new String[]{userId}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Uzimamo indekse za sve tri kolone
                int songTitleIndex = cursor.getColumnIndex(COLUMN_SONG_TITLE);
                int videoIdIndex = cursor.getColumnIndex(COLUMN_VIDEO_ID);
                int thumbnailUrlIndex = cursor.getColumnIndex(COLUMN_THUMBNAIL_URL);

                if (songTitleIndex >= 0 && videoIdIndex >= 0 && thumbnailUrlIndex >= 0) {
                    do {
                        String songTitle = cursor.getString(songTitleIndex);
                        String videoId = cursor.getString(videoIdIndex);
                        String thumbnailUrl = cursor.getString(thumbnailUrlIndex);  // Uzimamo thumbnailUrl

                        favoriteSongs.add(new FavoriteSong(songTitle, videoId, thumbnailUrl));  // Kreiramo FavoriteSong objekat
                    } while (cursor.moveToNext());
                } else {
                    // Ako neke od kolona nisu pronađene, prikazujemo grešku
                    Log.e("FavoritesDatabaseHelper", "Nisu pronađene potrebne kolone u bazi");
                }
            }
            cursor.close();
        }

        db.close();
        return favoriteSongs;
    }
    public void deleteFavorite(String videoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("favorites", "video_id = ?", new String[]{videoId});
        db.close();
    }
}

