package com.health.management.ui.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.health.management.R;
import com.health.management.receiver.ReminderReceiver;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    private TimePicker tpWaterReminder;
    private Button btnSaveWaterReminder;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        initViews();
        loadSavedSettings();
        setupSaveButton();
    }

    private void initViews() {
        tpWaterReminder = findViewById(R.id.tp_water_reminder);
        btnSaveWaterReminder = findViewById(R.id.btn_save_water_reminder);

        sharedPreferences = getSharedPreferences("health_settings", MODE_PRIVATE);
    }

    private void loadSavedSettings() {
        int hour = sharedPreferences.getInt("water_reminder_hour", 10);
        int minute = sharedPreferences.getInt("water_reminder_minute", 0);
        
        tpWaterReminder.setHour(hour);
        tpWaterReminder.setMinute(minute);
    }

    private void setupSaveButton() {
        btnSaveWaterReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWaterReminderSettings();
            }
        });
    }

    private void saveWaterReminderSettings() {
        int hour = tpWaterReminder.getHour();
        int minute = tpWaterReminder.getMinute();
        
        // 保存设置
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("water_reminder_hour", hour);
        editor.putInt("water_reminder_minute", minute);
        editor.apply();
        
        // 设置提醒
        setWaterReminder(hour, minute);
        
        Toast.makeText(this, "喝水提醒设置已保存", Toast.LENGTH_SHORT).show();
    }

    private void setWaterReminder(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("type", "water");
        intent.putExtra("message", "该喝水啦！");
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            this, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // 设置提醒时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        
        // 如果设置的时间已过，则设置为明天
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        
        // 设置重复提醒（每天）
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(),
            pendingIntent
        );
    }
}
