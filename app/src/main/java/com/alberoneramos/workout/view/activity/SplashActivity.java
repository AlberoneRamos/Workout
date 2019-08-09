package com.alberoneramos.workout.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.alberoneramos.workout.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class SplashActivity extends AppCompatActivity {
    static boolean calledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }
        setContentView(R.layout.splash);
        Handler handler = new Handler();
        handler.postDelayed(this::showHomescreen, 2000);
    }

    private void showHomescreen() {
        Intent intent;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            intent = new Intent(SplashActivity.this, HomescreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            intent = new Intent(SplashActivity.this, LogInActivity.class);
            Log.d("isLoggedIn", "onAuthStateChanged:signed_out");
        }
        startActivity(intent);
        finish();
    }
}
