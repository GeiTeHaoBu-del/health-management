package com.health.management.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.health.management.R;
import com.health.management.data.DietDao;
import com.health.management.data.ExerciseRecordDao;
import com.health.management.data.FoodDao;
import com.health.management.data.weather.WeatherData;
import com.health.management.data.weather.WeatherManager;
import com.health.management.ui.settings.SettingsActivity;
import com.health.management.utils.PinyinUtils;

import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private TextView tvStatistics;
    private TextView tvRecentRecords;
    private TextView tvDietStatistics;
    private TextView tvRecentDietRecords;

    // 天气相关UI组件
    private TextView tvWeatherInfo;
    private TextView tvExerciseSuggestion;
    private EditText etCityName;
    private Button btnSearchCity;

    private ExerciseRecordDao exerciseRecordDao;
    private FoodDao foodDao;
    private WeatherManager weatherManager;

    // 定义权限请求
    private ActivityResultLauncher<String[]> locationPermissionRequest;

    // 保存天气数据，避免每次切换页面重新获取
    private WeatherData cachedWeatherData = null;
    private boolean isLoadingWeather = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        FloatingActionButton fabSettings = view.findViewById(R.id.fab_settings);
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        initViews(view);
        exerciseRecordDao = new ExerciseRecordDao(requireContext());
        foodDao = new FoodDao(requireContext());
        weatherManager = new WeatherManager(requireContext());

        // 注册权限请求结果回调
        registerLocationPermissionRequest();

        // 设置城市查询按钮点击事件
        setupCitySearchButton();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
        loadDietStatistics();

        // 加载天气信息，但如果已有缓存数据且不是首次加载就不重新请求
        if (cachedWeatherData != null) {
            displayWeatherInfo(cachedWeatherData);
        } else if (!isLoadingWeather) {
            loadWeatherInfo();
        }
    }

    private void initViews(View view) {
        tvStatistics = view.findViewById(R.id.tv_statistics);
        tvRecentRecords = view.findViewById(R.id.tv_recent_records);
        tvDietStatistics = view.findViewById(R.id.tv_diet_statistics);
        tvRecentDietRecords = view.findViewById(R.id.tv_recent_diet_records);

        // 初始化天气相关视图
        tvWeatherInfo = view.findViewById(R.id.tv_weather_info);
        tvExerciseSuggestion = view.findViewById(R.id.tv_exercise_suggestion);
        etCityName = view.findViewById(R.id.et_city_name);
        btnSearchCity = view.findViewById(R.id.btn_search_city);
    }

    private void registerLocationPermissionRequest() {
        locationPermissionRequest = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Boolean fineLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION, false);

                if (fineLocationGranted != null && fineLocationGranted ||
                    coarseLocationGranted != null && coarseLocationGranted) {
                    // 位置权限已授予，获取天气信息
                    getCurrentLocationWeather();
                } else {
                    Toast.makeText(requireContext(), "需要位置权限才能获取当前位置天气", Toast.LENGTH_LONG).show();
                }
            }
        );
    }

    private void setupCitySearchButton() {
        btnSearchCity.setOnClickListener(v -> {
            String cityName = etCityName.getText().toString().trim();
            if (!cityName.isEmpty()) {
                // 检查输入是否包含汉字，如果包含则转换为拼音
                if (PinyinUtils.containsChinese(cityName)) {
                    String pinyinCity = PinyinUtils.toPinyin(cityName);
                    // 显示正在查询的信息，包含原始输入和转换后的拼音
                    Toast.makeText(requireContext(),
                        "正在查询: " + cityName + " (" + pinyinCity + ")",
                        Toast.LENGTH_SHORT).show();
                    getCityWeather(pinyinCity);
                } else {
                    getCityWeather(cityName);
                }
                // 清除缓存数据，因为用户主动搜索了新城市
                cachedWeatherData = null;
                isLoadingWeather = true;
            } else {
                Toast.makeText(requireContext(), "请输入城市名称", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWeatherInfo() {
        // 检查位置权限
        if (checkLocationPermission()) {
            isLoadingWeather = true;
            getCurrentLocationWeather();
        } else {
            // 请求位置权限
            requestLocationPermission();
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
               ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        locationPermissionRequest.launch(new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void getCurrentLocationWeather() {
        tvWeatherInfo.setText("正在获取当前位置天气信息...");
        tvExerciseSuggestion.setText("");

        weatherManager.getCurrentLocationWeather(new WeatherManager.WeatherCallback() {
            @Override
            public void onWeatherDataReceived(WeatherData weatherData) {
                cachedWeatherData = weatherData;  // 保存数据到缓存
                isLoadingWeather = false;
                displayWeatherInfo(weatherData);
            }

            @Override
            public void onError(String message) {
                isLoadingWeather = false;
                requireActivity().runOnUiThread(() -> {
                    tvWeatherInfo.setText("无法获取您当前的位置信息，请手动输入城市名称");
                    tvExerciseSuggestion.setText("无法提供运动建议");

                    // 突出显示城市输入框，引导用户注意
                    etCityName.setHint("请输入您所在的城市");
                    etCityName.setBackgroundResource(android.R.drawable.edit_text);
                    etCityName.requestFocus();

                    // 显示简短的Toast消息
                    Toast.makeText(requireContext(),
                        "无法自动获取位置，请手动输入城市",
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void getCityWeather(String cityName) {
        tvWeatherInfo.setText("正在获取" + cityName + "的天气信息...");
        tvExerciseSuggestion.setText("");

        weatherManager.getWeatherByCity(cityName, new WeatherManager.WeatherCallback() {
            @Override
            public void onWeatherDataReceived(WeatherData weatherData) {
                cachedWeatherData = weatherData;  // 保存数据到缓存
                isLoadingWeather = false;
                displayWeatherInfo(weatherData);
            }

            @Override
            public void onError(String message) {
                isLoadingWeather = false;
                requireActivity().runOnUiThread(() -> {
                    String cityName = etCityName.getText().toString().trim();
                    tvWeatherInfo.setText("无法获取 \"" + cityName + "\" 的天气信息，请确认城市名称是否正确");
                    tvExerciseSuggestion.setText("无法提供运动建议");

                    // 突出显示城市输入框，引导用户重新输入
                    etCityName.setHint("请输入正确的城市名称");
                    etCityName.setBackgroundResource(android.R.drawable.edit_text);
                    etCityName.requestFocus();

                    // 显示帮助提示
                    Toast.makeText(requireContext(),
                        "城市名称可能拼写错误或不受支持，请尝试其他城市",
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void displayWeatherInfo(final WeatherData weatherData) {
        requireActivity().runOnUiThread(() -> {
            // 解析温度（从开尔文转为摄氏度）
            double tempCelsius = weatherData.getTemperature() - 273.15;

            // 当前天气信息文本
            String weatherInfo = String.format(Locale.getDefault(),
                "%s: %.1f°C, %s\n湿度: %d%%, 风速: %.1f m/s",
                weatherData.getCityName(),
                tempCelsius,
                weatherData.getWeatherDescription(),
                weatherData.getHumidity(),
                weatherData.getWindSpeed());

            tvWeatherInfo.setText(weatherInfo);

            // 运动建议
            String exerciseSuggestion = weatherData.getExerciseSuggestion();
            tvExerciseSuggestion.setText(exerciseSuggestion);

            // 根据是否适合运动设置不同背景色
            if (weatherData.isSuitableForExercise()) {
                tvExerciseSuggestion.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
            } else {
                tvExerciseSuggestion.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
            }
        });
    }

    private void loadStatistics() {
        // 加载统计数据
        ExerciseRecordDao.Statistics stats = exerciseRecordDao.getWeeklyStatistics();
        String statisticsText = "本周运动统计：\n" +
            "总时长：" + stats.getTotalDuration() + "分钟\n" +
            "总消耗：" + stats.getTotalCalories() + "卡路里";
        tvStatistics.setText(statisticsText);
        
        // 加载最近记录
        List<ExerciseRecordDao.ExerciseRecord> records = exerciseRecordDao.getRecentRecords(3);
        StringBuilder recordsText = new StringBuilder("最近运动记录：\n");

        if (records.isEmpty()) {
            recordsText.append("暂无运动记录");
        } else {
            for (ExerciseRecordDao.ExerciseRecord record : records) {
                recordsText.append("- ").append(record.toString()).append("\n");
            }
        }

        tvRecentRecords.setText(recordsText.toString());
    }

    private void loadDietStatistics() {
        // 加载今日饮食统计
        double todayCalories = foodDao.getTodayTotalCalories();
        String dietStatsText = "今日饮食摄入：\n" +
            "总卡路里：" + String.format("%.1f", todayCalories) + " 卡路里";
        tvDietStatistics.setText(dietStatsText);

        // 加载最近饮食记录
        List<DietDao> dietRecords = foodDao.getRecentDietRecords(3);
        StringBuilder dietRecordsText = new StringBuilder();

        if (dietRecords.isEmpty()) {
            dietRecordsText.append("暂无饮食记录");
        } else {
            for (DietDao record : dietRecords) {
                dietRecordsText.append("- ")
                    .append(record.getDate())
                    .append(": ")
                    .append(record.getFoodName())
                    .append(" (")
                    .append(String.format("%.1f", record.getAmount()))
                    .append("份, ")
                    .append(String.format("%.1f", record.getCalories()))
                    .append("卡路里)\n");
            }
        }

        tvRecentDietRecords.setText(dietRecordsText.toString());
    }
}
