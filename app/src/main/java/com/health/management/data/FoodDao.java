package com.health.management.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.health.management.ui.DietActivity;

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

    /**
     * 保存饮食记录到数据库
     */
    public long insertDietRecord(String date, int foodId, String foodName, double amount, double calories) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("food_id", foodId);
        values.put("food_name", foodName);
        values.put("amount", amount);
        values.put("calories", calories);

        long id = db.insert("DietRecord", null, values);
        db.close();
        return id;
    }

    /**
     * 获取最近的饮食记录列表
     */
    public List<DietActivity.DietRecord> getRecentDietRecords(int limit) {
        List<DietActivity.DietRecord> records = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {"id", "date", "food_name", "amount", "calories"};
        String orderBy = "date DESC, id DESC";
        String limitStr = String.valueOf(limit);

        Cursor cursor = db.query("DietRecord", columns, null, null, null, null, orderBy, limitStr);

        while (cursor.moveToNext()) {
            DietActivity.DietRecord record = new DietActivity.DietRecord();
            record.setId(cursor.getInt(0));
            record.setDate(cursor.getString(1));
            record.setFoodName(cursor.getString(2));
            record.setAmount(cursor.getDouble(3));
            record.setCalories(cursor.getDouble(4));
            records.add(record);
        }

        cursor.close();
        db.close();
        return records;
    }

    /**
     * 获取今天的总卡路里摄入量
     */
    public double getTodayTotalCalories() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 获取今天的日期
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(new Date());

        String query = "SELECT SUM(calories) FROM DietRecord WHERE date = ?";
        String[] selectionArgs = {today};

        Cursor cursor = db.rawQuery(query, selectionArgs);
        double total = 0;

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        db.close();
        return total;
    }

    /**
     * 获取所有常见食物
     */
    public List<Food> getAllCommonFoods() {
        List<Food> foods = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"id", "name", "calories", "unit"};
        String orderBy = "name ASC"; // 按名称排序

        Cursor cursor = db.query("Food", columns, null, null, null, null, orderBy);

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
        // 如果数据库中没有食物，添加几种默认食物
        if (foods.isEmpty()) {
            addDefaultFoods();
            // 再次获取所有食物
            return getAllCommonFoods();
        }

        return foods;
    }

    /**
     * 添加几种默认食物到数据库（在食物表为空的情况下使用）
     */
    private void addDefaultFoods() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] defaultFoods = {
            "米饭,130,100g",
            "面条,120,100g",
            "鸡肉,53,100g",
            "牛肉,106,100g",
            "猪肉,62,100g",
            "鸡蛋,68,100g",
            "牛奶,42,100ml",
            "水果,89,100g",
            "海鲜,165,100g",
            "蔬菜,30,100g",
            "面包,60,100g",
            "其他,18,100g"
        };

        for (String food : defaultFoods) {
            String[] parts = food.split(",");
            ContentValues values = new ContentValues();
            values.put("name", parts[0]);
            values.put("calories", Double.parseDouble(parts[1]));
            values.put("unit", parts[2]);
            db.insert("Food", null, values);
        }

        db.close();
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
