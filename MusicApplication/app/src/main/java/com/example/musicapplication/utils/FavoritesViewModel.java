package com.example.musicapplication.utils;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.musicapplication.database.FavoriteDatabase;
import com.example.musicapplication.models.FavoriteSong;
import com.example.musicapplication.utils.FavoritesRepository;

import java.util.List;

public class FavoritesViewModel extends AndroidViewModel {

    private final FavoritesRepository favoritesRepository;
    private final LiveData<List<FavoriteSong>> allFavorites;

    public FavoritesViewModel(Application application) {
        super(application);
        favoritesRepository = new FavoritesRepository(FavoriteDatabase.getDatabase(application).favoriteSongDao());
        allFavorites = favoritesRepository.getAllFavorites();
    }

    public LiveData<List<FavoriteSong>> getAllFavorites() {
        return allFavorites;
    }

    public void insert(FavoriteSong favoriteSong) {
        favoritesRepository.insert(favoriteSong);
    }

    public void delete(FavoriteSong favoriteSong) {
        favoritesRepository.delete(favoriteSong);
    }
}
