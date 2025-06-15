package com.health.management.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.health.management.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private Button btnDrinkWater;
    private TextView tvDrinkingInfo;
    private SharedPreferences sharedPreferences;
    private static final int DAILY_GOAL = 7;
    private static final int EACH_AMOUNT = 200;
    private String todayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        setupBackButton();
        setupDrinkWaterButton();
        todayDate = getTodayDate();
        loadDrinkingRecord();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnDrinkWater = findViewById(R.id.btn_drink_water);
        tvDrinkingInfo = findViewById(R.id.tv_drinking_info);
        sharedPreferences = getSharedPreferences("health_settings", MODE_PRIVATE);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupDrinkWaterButton() {
        btnDrinkWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentCount = getTodayDrinkingCount();
                if (currentCount < DAILY_GOAL) {
                    currentCount++;
                    saveTodayDrinkingCount(currentCount);
                    updateDrinkingInfo(currentCount);
                    Toast.makeText(SettingsActivity.this, "打卡成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "今日目标已完成！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private int getTodayDrinkingCount() {
        return sharedPreferences.getInt(todayDate, 0);
    }

    private void saveTodayDrinkingCount(int count) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(todayDate, count);
        editor.apply();
    }

    private void loadDrinkingRecord() {
        int currentCount = getTodayDrinkingCount();
        updateDrinkingInfo(currentCount);
    }

    private void updateDrinkingInfo(int count) {
        String info = String.format("今日已喝水 %d 次，目标 %d 次，每次 %dml", count, DAILY_GOAL, EACH_AMOUNT);
        tvDrinkingInfo.setText(info);
    }
}