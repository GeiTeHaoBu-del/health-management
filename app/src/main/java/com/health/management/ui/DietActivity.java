package com.health.management.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.health.management.R;
import com.health.management.data.FoodDao;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DietActivity extends AppCompatActivity {
    private Spinner spFoodList;
    private EditText etAmount;
    private Button btnSave;
    private RecyclerView rvDietRecords;
    private TextView tvDailyCalories;

    private List<FoodDao.Food> foodList;
    private FoodDao.Food selectedFood;
    private FoodDao foodDao;
    private DietRecordAdapter recordAdapter;
    private List<DietRecord> dietRecords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);
        
        foodDao = new FoodDao(this);

        initViews();
        setupFoodListSpinner();
        setupSaveButton();
        loadDietRecords();
        updateDailyCalories();
    }

    private void initViews() {
        spFoodList = findViewById(R.id.sp_food_list);
        etAmount = findViewById(R.id.et_amount);
        btnSave = findViewById(R.id.btn_save);
        rvDietRecords = findViewById(R.id.rv_diet_records);
        tvDailyCalories = findViewById(R.id.tv_daily_calories);

        // 设置RecyclerView
        rvDietRecords.setLayoutManager(new LinearLayoutManager(this));
        recordAdapter = new DietRecordAdapter(dietRecords);
        rvDietRecords.setAdapter(recordAdapter);

        foodList = new ArrayList<>();
    }

    private void setupFoodListSpinner() {
        // 获取所有常见食物
        foodList = foodDao.getAllCommonFoods();

        ArrayAdapter<FoodDao.Food> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            foodList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFoodList.setAdapter(adapter);
        
        spFoodList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < foodList.size()) {
                    selectedFood = foodList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFood = null;
            }
        });
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDietRecord();
            }
        });
    }

    private void saveDietRecord() {
        if (selectedFood == null) {
            Toast.makeText(this, "请先选择食材", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String amountStr = etAmount.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "请输入食用量", Toast.LENGTH_SHORT).show();
            return;
        }
        
        double amount = Double.parseDouble(amountStr);
        double totalCalories = selectedFood.getCalories() * amount;
        
        // 获取当前日期
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(new Date());

        // 保存记录到数据库
        long result = foodDao.insertDietRecord(currentDate, selectedFood.getId(),
                selectedFood.getName(), amount, totalCalories);

        if (result > 0) {
            Toast.makeText(this, "饮食记录保存成功", Toast.LENGTH_SHORT).show();

            // 刷新界面
            etAmount.setText("");
            // 清空选择
            spFoodList.setSelection(0);

            // 重新加载记录
            loadDietRecords();
            updateDailyCalories();
        } else {
            Toast.makeText(this, "饮食记录保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDietRecords() {
        // 加载最近的饮食记录
        List<DietRecord> records = foodDao.getRecentDietRecords(10);
        dietRecords.clear();
        dietRecords.addAll(records);
        recordAdapter.notifyDataSetChanged();
    }

    private void updateDailyCalories() {
        // 计算今日摄入的总卡路里
        double dailyCalories = foodDao.getTodayTotalCalories();
        tvDailyCalories.setText(String.format("今日摄入：%.1f 卡路里", dailyCalories));
    }

    /**
     * 饮食记录数据类
     */
    public static class DietRecord {
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

    /**
     * 饮食记录适配器
     */
    private class DietRecordAdapter extends RecyclerView.Adapter<DietRecordViewHolder> {
        private List<DietRecord> records;

        public DietRecordAdapter(List<DietRecord> records) {
            this.records = records;
        }

        @Override
        public DietRecordViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_diet_record, parent, false);
            return new DietRecordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DietRecordViewHolder holder, int position) {
            DietRecord record = records.get(position);

            holder.tvDate.setText(record.getDate());
            holder.tvFoodName.setText(record.getFoodName());
            holder.tvDetails.setText(String.format("%.1f 份 | %.1f 卡路里",
                record.getAmount(), record.getCalories()));
        }

        @Override
        public int getItemCount() {
            return records.size();
        }
    }

    /**
     * 饮食记录ViewHolder
     */
    private class DietRecordViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvFoodName;
        TextView tvDetails;

        public DietRecordViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvDetails = itemView.findViewById(R.id.tv_details);
        }
    }
}
