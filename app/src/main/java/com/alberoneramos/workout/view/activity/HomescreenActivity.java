package com.alberoneramos.workout.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.alberoneramos.workout.R;
import com.alberoneramos.workout.controller.NavigationManager;
import com.alberoneramos.workout.view.fragment.CalendarFragment;
import com.alberoneramos.workout.view.fragment.QRScannerFragment;
import com.alberoneramos.workout.view.fragment.WorkoutListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomescreenActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                NavigationManager.openFragment(getSupportFragmentManager(), new WorkoutListFragment(), "Home", R.id.fragment_container);
                return true;
            case R.id.navigation_calendar:
                NavigationManager.openFragment(getSupportFragmentManager(),new CalendarFragment(),"Home",R.id.fragment_container);
                return true;
            case R.id.navigation_qr_scanner:
                NavigationManager.openFragment(getSupportFragmentManager(), new QRScannerFragment(), "Home", R.id.fragment_container);
                return true;
            case R.id.navigation_maps:
                NavigationManager.openActivity(this, MapsActivity.class);
                finish();
                return true;
        }
        return false;
    };


    public void addWorkout(View view) {
        Intent intent = new Intent(HomescreenActivity.this, AddWorkoutActivity.class);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);
    }

}
