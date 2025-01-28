package com.example.musicapplication.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String email;
    private String password;
    private List<Song> favoriteSongs; // Koristimo List<Song>

    // Konstruktor
    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.favoriteSongs = new ArrayList<>();
    }

    // Getter i Setter za email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter i Setter za password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter i Setter za favoriteSongs
    public List<Song> getFavoriteSongs() {
        return favoriteSongs;
    }

    public void setFavoriteSongs(List<Song> favoriteSongs) {
        this.favoriteSongs = favoriteSongs;
    }

    // Dodavanje pesme u favorite
    public boolean addFavoriteSong(Song song) {
        if (!favoriteSongs.contains(song)) {
            favoriteSongs.add(song);
            return true;
        }
        return false;
    }

    // Uklanjanje pesme iz favorita
    public void removeFavoriteSong(Song song) {
        favoriteSongs.remove(song);
    }

    // Provera da li je pesma favorit
    public boolean isFavorite(Song song) {
        return favoriteSongs.contains(song);
    }

    public void toggleFavorite(Song song) {
        if (isFavorite(song)) {
            removeFavoriteSong(song);
        } else {
            addFavoriteSong(song);
        }
    }
}
