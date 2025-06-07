package com.health.management.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.health.management.R;
import com.health.management.ui.fragments.HomeFragment;
import com.health.management.ui.fragments.RecordFragment;
import com.health.management.ui.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupBottomNavigation();
        loadFragment(new HomeFragment());
        setupNotificationPermission();
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
                    } else if (item.getItemId() == R.id.menu_exercise) {
                        startActivity(new Intent(MainActivity.this, ExerciseActivity.class));
                        return false;
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

    private void setupNotificationPermission() {
        requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    // 权限已授予
                } else {
                    Toast.makeText(this, "通知权限被拒绝，部分功能可能无法正常使用", Toast.LENGTH_LONG).show();
                }
            }
        );

        // Android 13及以上版本需要请求通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}

