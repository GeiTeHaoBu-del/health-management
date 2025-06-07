package com.health.management.data.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("weather")
    Call<WeatherData> getCurrentWeatherByLocation(
        @Query("lat") double latitude,
        @Query("lon") double longitude,
        @Query("appid") String apiKey
    );

    @GET("weather")
    Call<WeatherData> getCurrentWeatherByCity(
        @Query("q") String cityName,
        @Query("appid") String apiKey
    );
}
