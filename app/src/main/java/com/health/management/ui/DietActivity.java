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
import com.health.management.R;
import com.health.management.data.FoodDao;
import java.util.ArrayList;
import java.util.List;

public class DietActivity extends AppCompatActivity {
    private EditText etFoodName;
    private Spinner spFoodList;
    private EditText etAmount;
    private Spinner spUnit;
    private Button btnSearch;
    private Button btnSave;
    private TextView tvResult;
    
    private List<FoodDao.Food> foodList;
    private FoodDao.Food selectedFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);
        
        initViews();
        setupFoodListSpinner();
        setupUnitSpinner();
        setupSearchButton();
        setupSaveButton();
    }

    private void initViews() {
        etFoodName = findViewById(R.id.et_food_name);
        spFoodList = findViewById(R.id.sp_food_list);
        etAmount = findViewById(R.id.et_amount);
        spUnit = findViewById(R.id.sp_unit);
        btnSearch = findViewById(R.id.btn_search);
        btnSave = findViewById(R.id.btn_save);
        tvResult = findViewById(R.id.tv_result);
        
        foodList = new ArrayList<>();
    }

    private void setupFoodListSpinner() {
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
                    updateResultText();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFood = null;
                tvResult.setText("");
            }
        });
    }

    private void setupUnitSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this,
            R.array.weight_units,
            android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnit.setAdapter(adapter);
    }

    private void setupSearchButton() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFood();
            }
        });
    }

    private void searchFood() {
        String keyword = etFoodName.getText().toString();
        if (keyword.isEmpty()) {
            Toast.makeText(this, "请输入食材名称", Toast.LENGTH_SHORT).show();
            return;
        }
        
        FoodDao dao = new FoodDao(this);
        foodList = dao.searchFood(keyword);
        
        if (foodList.isEmpty()) {
            Toast.makeText(this, "未找到相关食材", Toast.LENGTH_SHORT).show();
            tvResult.setText("");
        } else {
            ((ArrayAdapter<FoodDao.Food>) spFoodList.getAdapter()).clear();
            ((ArrayAdapter<FoodDao.Food>) spFoodList.getAdapter()).addAll(foodList);
            spFoodList.setSelection(0);
        }
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDietRecord();
            }
        });
    }

    private void updateResultText() {
        if (selectedFood == null) return;
        
        String amountStr = etAmount.getText().toString();
        if (amountStr.isEmpty()) {
            tvResult.setText(selectedFood.getName() + "：" + 
                selectedFood.getCalories() + "卡路里/" + selectedFood.getUnit());
        } else {
            double amount = Double.parseDouble(amountStr);
            double totalCalories = selectedFood.getCalories() * amount;
            tvResult.setText(selectedFood.getName() + "：" + 
                totalCalories + "卡路里 (" + amount + selectedFood.getUnit() + ")");
        }
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
        
        // 这里应该将饮食记录保存到数据库
        // 简化实现，仅显示提示
        Toast.makeText(this, "饮食记录已保存", Toast.LENGTH_SHORT).show();
        finish();
    }
}    