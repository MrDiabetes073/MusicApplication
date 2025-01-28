package com.example.musicapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.musicapplication.models.FavoriteSong;
import com.example.musicapplication.models.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {

    private static final String PREFS_NAME = "user_favorites";
    private static final String FAVORITES_KEY = "favorites";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public FavoriteManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Dodaj pesmu u favorite
    public void addFavorite(Song song) {
        List<FavoriteSong> favorites = getFavorites();
        favorites.add(new FavoriteSong(song.getTitle(), song.getVideoId(), song.getThumbnailUrl()));
        saveFavorites(favorites);
    }

    // Ukloni pesmu iz favorita
    public void removeFavorite(Song song) {
        List<FavoriteSong> favorites = getFavorites();
        for (FavoriteSong favorite : favorites) {
            if (favorite.getId() == (song.getId())) {
                favorites.remove(favorite);
                break;
            }
        }
        saveFavorites(favorites);
    }

    // Dohvati sve favorite
    public List<FavoriteSong> getFavorites() {
        String json = sharedPreferences.getString(FAVORITES_KEY, null);
        if (json != null) {
            Type type = new TypeToken<List<FavoriteSong>>(){}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

    // Spasi listu favorita
    private void saveFavorites(List<FavoriteSong> favorites) {
        String json = gson.toJson(favorites);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FAVORITES_KEY, json);
        editor.apply();
    }
}

