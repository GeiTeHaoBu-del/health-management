package com.health.management.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class FoodDao {
    private HealthDBHelper dbHelper;

    public FoodDao(Context context) {
        dbHelper = new HealthDBHelper(context);
    }

    public List<Food> searchFood(String keyword) {
        List<Food> foods = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String[] columns = {"id", "name", "calories", "unit"};
        String selection = "name LIKE ?";
        String[] selectionArgs = new String[] {"%" + keyword + "%"};
        
        Cursor cursor = db.query("Food", columns, selection, selectionArgs, null, null, null);
        
        while (cursor.moveToNext()) {
            Food food = new Food();
            food.setId(cursor.getInt(0));
            food.setName(cursor.getString(1));
            food.setCalories(cursor.getDouble(2));
            food.setUnit(cursor.getString(3));
            foods.add(food);
        }
        
        cursor.close();
        db.close();
        return foods;
    }

    public static class Food {
        private int id;
        private String name;
        private double calories;
        private String unit;

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getCalories() { return calories; }
        public void setCalories(double calories) { this.calories = calories; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }

        @Override
        public String toString() {
            return name + " (" + calories + "卡路里/" + unit + ")";
        }
    }
}    