package com.health.management.data;

public class DietDao {
        private int id;
        private String date;
        private String foodName;
        private double amount;
        private double calories;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getFoodName() { return foodName; }
        public void setFoodName(String foodName) { this.foodName = foodName; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public double getCalories() { return calories; }
        public void setCalories(double calories) { this.calories = calories; }

}
