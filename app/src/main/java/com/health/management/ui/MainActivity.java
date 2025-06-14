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

    // 保存Fragment实例，避免重复创建
    private HomeFragment homeFragment;
    private RecordFragment recordFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupBottomNavigation();

        // 初始化Fragment
        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();
            recordFragment = new RecordFragment();

            // 加载初始Fragment（主页）
            getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, recordFragment, "record")
                .hide(recordFragment)
                .add(R.id.fragment_container, homeFragment, "home")
                .commit();

            activeFragment = homeFragment;
        }

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
                    if (item.getItemId() == R.id.menu_home) {
                        // 切换到主页Fragment
                        if (homeFragment == null) {
                            homeFragment = new HomeFragment();
                        }
                        loadFragment(homeFragment);
                        return true;
                    } else if (item.getItemId() == R.id.menu_record) {
                        // 切换到记录Fragment
                        if (recordFragment == null) {
                            recordFragment = new RecordFragment();
                        }
                        loadFragment(recordFragment);
                        return true;
                    } else if (item.getItemId() == R.id.menu_exercise) {
                        startActivity(new Intent(MainActivity.this, ExerciseActivity.class));
                        return true;
                    } else if (item.getItemId() == R.id.menu_diet) {
                        startActivity(new Intent(MainActivity.this, DietActivity.class));
                        return true;
                    } else if (item.getItemId() == R.id.menu_settings) {
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        return true;
                    }
                    return false;
                }
            }
        );
    }

    private void loadFragment(Fragment fragment) {
        // 避免切换到当前已经显示的Fragment
        if (fragment == activeFragment) {
            return;
        }

        // 隐藏当前显示的Fragment，显示需要切换的Fragment
        getSupportFragmentManager()
            .beginTransaction()
            .hide(activeFragment)
            .show(fragment)
            .commit();

        // 更新当前活动的Fragment引用
        activeFragment = fragment;
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
