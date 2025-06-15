package com.health.management.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.health.management.R;
import com.health.management.ui.fragments.DietFragment;
import com.health.management.ui.fragments.ExerciseFragment;
import com.health.management.ui.fragments.HomeFragment;
import com.health.management.ui.fragments.RecordFragment;
import com.health.management.ui.settings.SettingsActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView; // 底部导航栏视图
    private ActivityResultLauncher<String> requestPermissionLauncher; // 用于请求用户权限的工具

    // 保存Fragment实例，避免重复创建
    private HomeFragment homeFragment; // 主页
    private RecordFragment recordFragment; // 记录查看
    private ExerciseFragment exerciseFragment; // 添加锻炼记录
    private DietFragment dietFragment; // 添加饮食记录
    private Fragment activeFragment; // 当前正在显示的页面

    // 设置Activity启动器，用于接收返回的喝水数据
    private ActivityResultLauncher<Intent> settingsActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            int remainingCount = data.getIntExtra("remaining_count", 0);
                            Log.d("MainActivity", "Received remaining count: " + remainingCount);

                            // 获取HomeFragment实例并更新数据
                            if (homeFragment != null && homeFragment.isAdded()) {
                                homeFragment.updateRemainingWater(remainingCount);
                            }
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 页面初始化方法
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 使用此布局

        initViews(); // 初始化页面元素的方法
        setupBottomNavigation(); // 初始化底部导航栏的方法

        // 初始化四个Fragment
        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();
            recordFragment = new RecordFragment();
            exerciseFragment = new ExerciseFragment();
            dietFragment = new DietFragment();

            // 加载所有的fragment，但是把主页之外的隐藏起来，这样每次看别的页面就可以快速切换
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, recordFragment, "record")
                    .hide(recordFragment)
                    .add(R.id.fragment_container, exerciseFragment, "exercise")
                    .hide(exerciseFragment)
                    .add(R.id.fragment_container, dietFragment, "diet")
                    .hide(dietFragment)
                    .add(R.id.fragment_container, homeFragment, "home")
                    .commit(); // 先只显示主页

            // 当前fragment设置为主页
            activeFragment = homeFragment;
        } else {
            // 从保存的状态中恢复fragment实例
            homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("home");
            recordFragment = (RecordFragment) getSupportFragmentManager().findFragmentByTag("record");
            exerciseFragment = (ExerciseFragment) getSupportFragmentManager().findFragmentByTag("exercise");
            dietFragment = (DietFragment) getSupportFragmentManager().findFragmentByTag("diet");
            activeFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
        }

        // 设置通知权限
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
                            // 切换到运动Fragment
                            if (exerciseFragment == null) {
                                exerciseFragment = new ExerciseFragment();
                            }
                            loadFragment(exerciseFragment);
                            return true;
                        } else if (item.getItemId() == R.id.menu_diet) {
                            // 切换到饮食Fragment
                            if (dietFragment == null) {
                                dietFragment = new DietFragment();
                            }
                            loadFragment(dietFragment);
                            return true;
                        }
                        return false;
                    }
                }
        );
    }

    private void loadFragment(Fragment fragment) {
        // 已经是目标页面了就不加载
        if (fragment == activeFragment) {
            return;
        }

        // 隐藏当前显示的Fragment，显示需要切换的Fragment
        getSupportFragmentManager()
                .beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .setPrimaryNavigationFragment(fragment)
                .commit();

        // 更新当前活动的Fragment引用
        activeFragment = fragment;
    }

    // 判断通知权限
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

    // 打开设置Activity的方法（需要在合适的地方调用，如点击设置按钮）
    public void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        settingsActivityLauncher.launch(intent);
    }
}