package com.example.musicapplication.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.musicapplication.models.FavoriteSong;

import java.util.List;

@Dao
public interface FavoriteSongDao {

    @Insert
    void insert(FavoriteSong favoriteSong);

    @Delete
    void delete(FavoriteSong favoriteSong);

    @Query("SELECT * FROM favorite_songs")
    LiveData<List<FavoriteSong>> getAllFavorites();
}
