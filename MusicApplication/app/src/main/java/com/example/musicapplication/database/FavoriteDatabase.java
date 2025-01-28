package com.example.musicapplication.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.musicapplication.models.FavoriteSong;

@Database(entities = {FavoriteSong.class}, version = 1, exportSchema = false)
public abstract class FavoriteDatabase extends RoomDatabase {

    private static FavoriteDatabase INSTANCE;

    public abstract FavoriteSongDao favoriteSongDao();

    public static FavoriteDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FavoriteDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    FavoriteDatabase.class, "favorites_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
