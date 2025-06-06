package com.health.management.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HealthDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "health_manager.db";
    private static final int DATABASE_VERSION = 1;

    // ExerciseRecord表创建语句
    public static final String CREATE_EXERCISE_RECORD = 
        "CREATE TABLE ExerciseRecord (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "date TEXT NOT NULL, " +
        "type TEXT NOT NULL, " +
        "duration INTEGER NOT NULL, " +
        "distance REAL NOT NULL, " +
        "calories REAL NOT NULL)";

    // Food表创建语句
    public static final String CREATE_FOOD = 
        "CREATE TABLE Food (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "name TEXT NOT NULL, " +
        "calories REAL NOT NULL, " +
        "unit TEXT NOT NULL)";

    // DietRecord表创建语句
    public static final String CREATE_DIET_RECORD = 
        "CREATE TABLE DietRecord (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "food_name TEXT NOT NULL, " +
        "calories REAL NOT NULL, " +
        "amount REAL NOT NULL, " +
        "unit TEXT NOT NULL, " +
        "record_time TEXT NOT NULL)";

    public HealthDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EXERCISE_RECORD);
        db.execSQL(CREATE_FOOD);
        db.execSQL(CREATE_DIET_RECORD);
        initFoodData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ExerciseRecord");
        db.execSQL("DROP TABLE IF EXISTS Food");
        db.execSQL("DROP TABLE IF EXISTS DietRecord");
        onCreate(db);
    }

    private void initFoodData(SQLiteDatabase db) {
        // 初始化常见食材数据
        String[] foods = {
            "米饭,130,100g",
            "鸡蛋,70,个",
            "苹果,53,100g",
            "牛肉,106,100g",
            "西兰花,36,100g"
        };

        for (String food : foods) {
            String[] parts = food.split(",");
            String insertSql = "INSERT INTO Food (name, calories, unit) VALUES ('" +
                parts[0] + "', " + parts[1] + ", '" + parts[2] + "')";
            db.execSQL(insertSql);
        }
    }
}    