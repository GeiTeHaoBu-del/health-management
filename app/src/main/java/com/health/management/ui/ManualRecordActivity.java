package com.health.management.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.health.management.R;
import com.health.management.data.ExerciseRecordDao;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ManualRecordActivity extends AppCompatActivity {
    private EditText etDate;
    private Spinner spExerciseType;
    private EditText etDuration;
    private EditText etDistance;
    private Button btnSave;
    
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_record);
        
        initViews();
        setupDatePicker();
        setupExerciseTypeSpinner();
        setupSaveButton();
    }

    private void initViews() {
        etDate = findViewById(R.id.et_date);
        spExerciseType = findViewById(R.id.sp_exercise_type);
        etDuration = findViewById(R.id.et_duration);
        etDistance = findViewById(R.id.et_distance);
        btnSave = findViewById(R.id.btn_save);
        
        selectedDate = Calendar.getInstance();
        updateDateDisplay();
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog dialog = new DatePickerDialog(
            this,
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateDisplay();
                }
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        
        // 设置最大日期为今天
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        
        // 设置最小日期为7天前
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, -7);
        dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        
        dialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        etDate.setText(sdf.format(selectedDate.getTime()));
    }

    private void setupExerciseTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this,
            R.array.exercise_types,
            android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spExerciseType.setAdapter(adapter);
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecord();
            }
        });
    }

    private void saveRecord() {
        String date = etDate.getText().toString();
        String type = spExerciseType.getSelectedItem().toString();
        
        String durationStr = etDuration.getText().toString();
        String distanceStr = etDistance.getText().toString();
        
        if (durationStr.isEmpty() || distanceStr.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int duration = Integer.parseInt(durationStr);
        double distance = Double.parseDouble(distanceStr);
        
        ExerciseRecordDao dao = new ExerciseRecordDao(this);
        long result = dao.insertRecord(date, type, duration, distance);
        
        if (result != -1) {
            Toast.makeText(this, "记录保存成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "记录保存失败", Toast.LENGTH_SHORT).show();
        }
    }
}    