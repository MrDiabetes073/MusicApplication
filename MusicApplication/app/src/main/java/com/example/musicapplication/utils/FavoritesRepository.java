package com.example.musicapplication.utils;

import androidx.lifecycle.LiveData;

import com.example.musicapplication.database.FavoriteSongDao;
import com.example.musicapplication.models.FavoriteSong;

import java.util.List;

public class FavoritesRepository {

    private final FavoriteSongDao favoriteSongDao;

    public FavoritesRepository(FavoriteSongDao favoriteSongDao) {
        this.favoriteSongDao = favoriteSongDao;
    }

    public LiveData<List<FavoriteSong>> getAllFavorites() {
        return favoriteSongDao.getAllFavorites();
    }

    public void insert(FavoriteSong favoriteSong) {
        favoriteSongDao.insert(favoriteSong);
    }

    public void delete(FavoriteSong favoriteSong) {
        favoriteSongDao.delete(favoriteSong);
    }
}
