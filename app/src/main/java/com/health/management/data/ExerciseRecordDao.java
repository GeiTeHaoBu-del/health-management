package com.health.management.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class ExerciseRecordDao {
    private HealthDBHelper dbHelper;

    public ExerciseRecordDao(Context context) {
        dbHelper = new HealthDBHelper(context);
    }

    public long insertRecord(String date, String type, int duration, double distance) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("type", type);
        values.put("duration", duration);
        values.put("distance", distance);
        
        // 计算卡路里
        double calories = calculateCalories(type, distance);
        values.put("calories", calories);

        long id = db.insert("ExerciseRecord", null, values);
        db.close();
        return id;
    }

    private double calculateCalories(String type, double distance) {
        if ("跑步".equals(type)) {
            return distance * 60;
        } else if ("步行".equals(type)) {
            return distance * 30;
        }
        return 0;
    }

    public List<ExerciseRecord> getRecentRecords(int limit) {
        List<ExerciseRecord> records = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String[] columns = {"id", "date", "type", "duration", "distance", "calories"};
        String orderBy = "date DESC";
        String limitStr = String.valueOf(limit);
        
        Cursor cursor = db.query("ExerciseRecord", columns, null, null, null, null, orderBy, limitStr);
        
        while (cursor.moveToNext()) {
            ExerciseRecord record = new ExerciseRecord();
            record.setId(cursor.getInt(0));
            record.setDate(cursor.getString(1));
            record.setType(cursor.getString(2));
            record.setDuration(cursor.getInt(3));
            record.setDistance(cursor.getDouble(4));
            record.setCalories(cursor.getDouble(5));
            records.add(record);
        }
        
        cursor.close();
        db.close();
        return records;
    }

    public Statistics getWeeklyStatistics() {
        Statistics stats = new Statistics();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        // 查询总时长
        String durationQuery = "SELECT SUM(duration) FROM ExerciseRecord " +
            "WHERE date >= date('now', '-7 days')";
        Cursor durationCursor = db.rawQuery(durationQuery, null);
        if (durationCursor.moveToFirst()) {
            stats.setTotalDuration(durationCursor.getInt(0));
        }
        durationCursor.close();
        
        // 查询总卡路里
        String caloriesQuery = "SELECT SUM(calories) FROM ExerciseRecord " +
            "WHERE date >= date('now', '-7 days')";
        Cursor caloriesCursor = db.rawQuery(caloriesQuery, null);
        if (caloriesCursor.moveToFirst()) {
            stats.setTotalCalories(caloriesCursor.getDouble(0));
        }
        caloriesCursor.close();
        
        db.close();
        return stats;
    }

    public static class ExerciseRecord {
        private int id;
        private String date;
        private String type;
        private int duration;
        private double distance;
        private double calories;

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = duration; }
        public double getDistance() { return distance; }
        public void setDistance(double distance) { this.distance = distance; }
        public double getCalories() { return calories; }
        public void setCalories(double calories) { this.calories = calories; }

        @Override
        public String toString() {
            return date + " - " + type + " (" + duration + "分钟, " + 
                distance + "公里, " + calories + "卡路里)";
        }
    }

    public static class Statistics {
        private int totalDuration;
        private double totalCalories;

        // Getters and setters
        public int getTotalDuration() { return totalDuration; }
        public void setTotalDuration(int totalDuration) { this.totalDuration = totalDuration; }
        public double getTotalCalories() { return totalCalories; }
        public void setTotalCalories(double totalCalories) { this.totalCalories = totalCalories; }
    }
}
