package com.health.management.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.health.management.R;
import com.health.management.ui.fragments.HomeFragment;
import com.health.management.ui.fragments.RecordFragment;
import com.health.management.ui.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupBottomNavigation();
        loadFragment(new HomeFragment());
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;

                    if (item.getItemId() == R.id.menu_home) {
                        fragment = new HomeFragment();
                    } else if (item.getItemId() == R.id.menu_record) {
                        fragment = new RecordFragment();
                    } else if (item.getItemId() == R.id.menu_diet) {
                        startActivity(new Intent(MainActivity.this, DietActivity.class));
                        return false;
                    } else if (item.getItemId() == R.id.menu_settings) {
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        return false;
                    }
                    
                    if (fragment != null) {
                        loadFragment(fragment);
                        return true;
                    }
                    
                    return false;
                }
            }
        );
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }
}    