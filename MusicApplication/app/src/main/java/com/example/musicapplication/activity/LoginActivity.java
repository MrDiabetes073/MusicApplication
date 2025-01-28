package com.example.musicapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplication.R;
import com.example.musicapplication.models.User;
import com.example.musicapplication.utils.SessionManager;
import com.example.musicapplication.database.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, signupButton;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicijalizacija elemenata
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        progressBar = findViewById(R.id.progressBar);

        // Inicijalizacija FirebaseAuth i FirebaseHelper
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseHelper = new FirebaseHelper();

        // Login dugme
        loginButton.setOnClickListener(v -> loginUser());

        // Signup dugme vodi na registraciju
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        // Dohvatanje emaila i lozinke
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Provere da li su polja prazna
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        // Prikaz progress bara
        progressBar.setVisibility(View.VISIBLE);

        // Firebase login
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    // Sakrij progress bar
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        Log.d("LoginDebug", "Login successful for email: " + email);

                        // Pre nego što pokreneš HomeActivity, postavi korisnika u SessionManager
                        User user = new User(email, ""); // Kreiranje korisnika sa emailom
                        SessionManager.setCurrentUser(user); // Postavljanje korisnika u SessionManager

                        // Pozivaj fetchUser metodu da učitaš korisnika iz Firebase-a
                        firebaseHelper.fetchUser(email, new FirebaseHelper.FirebaseUserCallback() {
                            @Override
                            public void onUserFetched(User fetchedUser) {
                                // Ako su podaci preuzeti uspešno, ažuriraj korisnika u SessionManager
                                SessionManager.setCurrentUser(fetchedUser);
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String errorMessage) {
                                // Prikaži grešku ako se desi problem pri preuzimanju podataka
                                Toast.makeText(LoginActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Idi na HomeActivity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Logovanje greške
                        Log.e("LoginError", "Login failed: ", task.getException());
                        Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Ako se desi dodatna greška
                    progressBar.setVisibility(View.GONE);
                    Log.e("LoginError", "Error occurred: ", e);
                    Toast.makeText(LoginActivity.this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
