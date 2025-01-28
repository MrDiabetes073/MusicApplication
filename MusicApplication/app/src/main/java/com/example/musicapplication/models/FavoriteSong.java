package com.example.musicapplication.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_songs")
public class FavoriteSong extends Song {
    public FavoriteSong(String title, String videoId, String thumbnailUrl) {
        super(title, videoId, thumbnailUrl);
    }

    public static FavoriteSong fromSong(Song song) {
        return new FavoriteSong(song.getTitle(), song.getVideoId(), song.getThumbnailUrl());
    }
}

