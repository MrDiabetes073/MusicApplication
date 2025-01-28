package com.example.musicapplication.database;

import androidx.annotation.NonNull;

import com.example.musicapplication.models.Song;
import com.example.musicapplication.models.User;
import com.example.musicapplication.utils.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {

    private final DatabaseReference usersRef;

    public FirebaseHelper() {
        // Referenca na čvor "users" u Firebase-u
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
    }

    // Dodavanje korisnika u Firebase
    public void saveUser(User user) {
        if (user.getEmail() != null) {
            // Firebase ne dozvoljava tačke u ključevima, pa zamenjujemo "." sa ","
            String userKey = user.getEmail().replace(".", ",");
            usersRef.child(userKey).setValue(user);
        }
    }

    // Ažuriranje liste favorita za korisnika
    public void updateFavorites(User user) {
        if (user.getEmail() != null) {
            String userKey = user.getEmail().replace(".", ",");
            usersRef.child(userKey).child("favoriteSongs").setValue(user.getFavoriteSongs());
        }
    }

    // Dohvatanje korisnika iz Firebase-a
    public void fetchUser(String email, FirebaseUserCallback callback) {
        if (email != null) {
            String userKey = email.replace(".", ",");
            usersRef.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            callback.onUserFetched(user);
                        } else {
                            callback.onError("Korisnik nije pronađen.");
                        }
                    } else {
                        callback.onError("Korisnik ne postoji u bazi.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onError("Greška pri dohvatanju korisnika: " + error.getMessage());
                }
            });
        }
    }

    // Interfejs za callback
    public interface FirebaseUserCallback {
        void onUserFetched(User user);

        void onError(String errorMessage);
    }
}
