package com.example.musicapplication.api;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.List;

public class YouTubeApiClient {
    private static final String API_KEY = "AIzaSyDrN0wkrxxnxzHEkK63MAhFrSFutv_H-3w";
    private static final String APPLICATION_NAME = "MusicApplication";

    private static YouTube youtube;

    // Ensures initialization happens only once
    public static void initialize() {
        if (youtube == null) {  // Check to avoid reinitialization
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), null)
                    .setApplicationName(APPLICATION_NAME)
                    .setYouTubeRequestInitializer(new YouTubeRequestInitializer(API_KEY))
                    .build();
        }
    }

    public static List<SearchResult> searchMusic(String query, String type, String videoCategoryId) throws IOException {
        // Ensure YouTube is initialized before making API calls
        if (youtube == null) {
            throw new IllegalStateException("YouTubeApiClient is not initialized. Call initialize() first.");
        }

        YouTube.Search.List search = youtube.search().list("id,snippet");
        search.setQ(query);
        search.setType(type);
        search.setMaxResults(25L);
        search.setVideoCategoryId(videoCategoryId);
        search.setFields("items(id/videoId,snippet/title,snippet/thumbnails/default/url)");
        search.setKey(API_KEY);

        SearchListResponse response = search.execute();
        return response.getItems();
    }
}