package com.example.fishclassification;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.FirebaseApp;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import android.view.MenuItem;
import android.view.View;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    private static BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);;
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        if (isUserLoggedIn()) {
            showBottomNavigation();
            navigateTo(new DetectorFragment(), false);
        } else {
            hideBottomNavigation();
            navigateTo(new FirstFragment(), false);
        }
    }

    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof DetectorFragment || currentFragment instanceof AboutFragment) {
            // Exit the app or perform any other action you prefer
            finish();
        } else {
            // Otherwise, handle back navigation normally
            super.onBackPressed();
        }
    }

    private void navigateTo(Fragment fragment, boolean addToBackStack) {
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public static void showBottomNavigation() {
        if (isUserLoggedIn()) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    private static boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }


    public void hideBottomNavigation() {
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        navigateTo(new FirstFragment(), false);
        hideBottomNavigation();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.nav_detector:
                selectedFragment = new DetectorFragment();
                break;
            case R.id.nav_about:
                selectedFragment = new AboutFragment();
                break;
            case R.id.nav_history:
                selectedFragment = new DetectionHistoryFragment();
                break;

            case R.id.nav_settings:
                selectedFragment = new SettingsFragment();
                break;
        }

            if (selectedFragment != null) {
                navigateTo(selectedFragment, false);
            }
        return false;
    };
}
