package com.health.management.data.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherManager {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "b69bd1bbed7fcb45523f289404d70fe1";

    private Context context;
    private WeatherApiService apiService;
    private FusedLocationProviderClient fusedLocationClient;
    private WeatherCallback weatherCallback;

    public interface WeatherCallback {
        void onWeatherDataReceived(WeatherData weatherData);
        void onError(String message);
    }

    public WeatherManager(Context context) {
        this.context = context;

        // 初始化Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(WeatherApiService.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    // 使用设备当前位置获取天气
    public void getCurrentLocationWeather(WeatherCallback callback) {
        this.weatherCallback = callback;

        // 检查位置权限
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (callback != null) {
                callback.onError("需要位置权限才能获取当前位置的天气信息");
            }
            return;
        }

        // 获取最新位置
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return this;
            }
        }).addOnSuccessListener(location -> {
            if (location != null) {
                // 使用位置获取天气
                getWeatherByLocation(location.getLatitude(), location.getLongitude());
            } else if (callback != null) {
                callback.onError("无法获取当前位置");
            }
        }).addOnFailureListener(e -> {
            if (callback != null) {
                callback.onError("获取位置失败: " + e.getMessage());
            }
        });
    }

    // 根据城市名称获取天气
    public void getWeatherByCity(String cityName, WeatherCallback callback) {
        this.weatherCallback = callback;

        Call<WeatherData> call = apiService.getCurrentWeatherByCity(cityName, API_KEY);

        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (callback != null) {
                        callback.onWeatherDataReceived(response.body());
                    }
                } else {
                    if (callback != null) {
                        callback.onError("获取天气数据失败: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                if (callback != null) {
                    callback.onError("API请求失败: " + t.getMessage());
                }
            }
        });
    }

    // 根据经纬度获取天气
    private void getWeatherByLocation(double latitude, double longitude) {
        Call<WeatherData> call = apiService.getCurrentWeatherByLocation(latitude, longitude, API_KEY);

        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (weatherCallback != null) {
                        weatherCallback.onWeatherDataReceived(response.body());
                    }
                } else {
                    if (weatherCallback != null) {
                        weatherCallback.onError("获取天气数据失败: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                if (weatherCallback != null) {
                    weatherCallback.onError("API请求失败: " + t.getMessage());
                }
            }
        });
    }
}
