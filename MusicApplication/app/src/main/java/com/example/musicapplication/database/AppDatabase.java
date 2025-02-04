package com.example.musicapplication.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.musicapplication.models.FavoriteSong;

@Database(entities = {FavoriteSong.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FavoriteSongDao favoriteSongDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "favorite_songs_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
