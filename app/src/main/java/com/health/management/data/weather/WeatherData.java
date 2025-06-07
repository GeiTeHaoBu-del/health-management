package com.health.management.data.weather;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherData {
    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("name")
    private String cityName;

    // 内部类代表API返回的主要天气数据
    public static class Main {
        @SerializedName("temp")
        private double temperature;

        @SerializedName("humidity")
        private int humidity;

        @SerializedName("feels_like")
        private double feelsLike;

        public double getTemperature() {
            return temperature;
        }

        public int getHumidity() {
            return humidity;
        }

        public double getFeelsLike() {
            return feelsLike;
        }
    }

    // 内部类代表API返回的天气状况数据
    public static class Weather {
        @SerializedName("main")
        private String main;

        @SerializedName("description")
        private String description;

        @SerializedName("icon")
        private String icon;

        public String getMain() {
            return main;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

    // 内部类代表API返回的风力数据
    public static class Wind {
        @SerializedName("speed")
        private double speed;

        public double getSpeed() {
            return speed;
        }
    }

    // 获取温度
    public double getTemperature() {
        return main != null ? main.getTemperature() : 0;
    }

    // 获取湿度
    public int getHumidity() {
        return main != null ? main.getHumidity() : 0;
    }

    // 获取体感温度
    public double getFeelsLike() {
        return main != null ? main.getFeelsLike() : 0;
    }

    // 获取天气情况
    public String getWeatherMain() {
        return weather != null && !weather.isEmpty() ? weather.get(0).getMain() : "";
    }

    // 获取天气描述
    public String getWeatherDescription() {
        return weather != null && !weather.isEmpty() ? weather.get(0).getDescription() : "";
    }

    // 获取天气图标代码
    public String getWeatherIcon() {
        return weather != null && !weather.isEmpty() ? weather.get(0).getIcon() : "";
    }

    // 获取风速
    public double getWindSpeed() {
        return wind != null ? wind.getSpeed() : 0;
    }

    // 获取城市名
    public String getCityName() {
        return cityName;
    }

    // 判断当天是否适合运动
    public boolean isSuitableForExercise() {
        // 如果天气为雨、雪、雷暴等极端天气，不适合运动
        String weatherMain = getWeatherMain().toLowerCase();
        if (weatherMain.contains("rain") || weatherMain.contains("snow") ||
            weatherMain.contains("storm") || weatherMain.contains("extreme")) {
            return false;
        }

        // 如果温度过高（超过35摄氏度）或过低（低于0摄氏度），不适合运动
        double temp = getTemperature() - 273.15; // 开尔文转摄氏度
        if (temp > 35 || temp < 0) {
            return false;
        }

        // 如果风速过大（超过10 m/s，约36 km/h），不适合运动
        if (getWindSpeed() > 10) {
            return false;
        }

        return true;
    }

    // 获取运动建议
    public String getExerciseSuggestion() {
        if (!isSuitableForExercise()) {
            String weatherMain = getWeatherMain().toLowerCase();
            double temp = getTemperature() - 273.15; // 开尔文转摄氏度

            if (weatherMain.contains("rain")) {
                return "今天有雨，不建议户外运动";
            } else if (weatherMain.contains("snow")) {
                return "今天有雪，不建议户外运动";
            } else if (weatherMain.contains("storm")) {
                return "今天有暴风雨，不建议户外运动";
            } else if (temp > 35) {
                return "今天温度过高，建议室内运动或避开高温时段";
            } else if (temp < 0) {
                return "今天温度过低，建议室内运动";
            } else if (getWindSpeed() > 10) {
                return "今天风力较大，不建议户外运动";
            }
            return "今天天气不适合户外运动";
        } else {
            double temp = getTemperature() - 273.15; // 开尔文转摄氏度
            String weatherMain = getWeatherMain();

            if (temp > 28) {
                return "今天适合运动，但温度较高，请注意补充水分";
            } else if (temp < 10) {
                return "今天适合运动，但温度较低，请注意保暖";
            } else if ("Clear".equalsIgnoreCase(weatherMain)) {
                return "今天天气晴朗，非常适合户外运动";
            } else {
                return "今天天气适合运动，祝您运动愉快";
            }
        }
    }
}
