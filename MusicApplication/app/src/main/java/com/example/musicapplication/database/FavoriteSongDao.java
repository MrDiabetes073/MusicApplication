package com.example.musicapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.musicapplication.models.FavoriteSong;

import java.util.List;

@Dao
public interface FavoriteSongDao {
    @Insert
    void insert(FavoriteSong favoriteSong);

    @Query("SELECT * FROM favorite_songs")
    List<FavoriteSong> getAllFavorites();

    @Query("DELETE FROM favorite_songs WHERE videoId = :videoId")
    void removeFavorite(String videoId);
}

