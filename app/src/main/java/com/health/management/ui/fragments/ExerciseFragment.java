package com.health.management.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.health.management.R;
import com.health.management.data.ExerciseRecordDao;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExerciseFragment extends Fragment {

    private Spinner exerciseTypeSpinner;
    private EditText durationEditText;
    private EditText distanceEditText;
    private Button saveButton;
    private RecyclerView recordsRecyclerView;
    private TextView statisticsTextView;

    private ExerciseRecordDao recordDao;
    private ExerciseRecordAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_exercise, container, false);

        // 初始化数据访问对象
        recordDao = new ExerciseRecordDao(requireContext());

        // 初始化视图
        initViews(view);

        // 设置运动类型下拉菜单
        setupExerciseTypeSpinner();

        // 设置保存按钮点击事件
        setupSaveButton();

        // 加载运动记录列表
        loadExerciseRecords();

        // 加载统计信息
        loadStatistics();

        return view;
    }

    private void initViews(View view) {
        exerciseTypeSpinner = view.findViewById(R.id.exercise_type_spinner);
        durationEditText = view.findViewById(R.id.duration_edit_text);
        distanceEditText = view.findViewById(R.id.distance_edit_text);
        saveButton = view.findViewById(R.id.save_button);
        recordsRecyclerView = view.findViewById(R.id.records_recycler_view);
        statisticsTextView = view.findViewById(R.id.statistics_text_view);

        // 设置RecyclerView
        recordsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupExerciseTypeSpinner() {
        // 设置运动类型下拉菜单选项
        String[] exerciseTypes = getResources().getStringArray(R.array.exercise_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, exerciseTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseTypeSpinner.setAdapter(adapter);
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExerciseRecord();
            }
        });
    }

    private void saveExerciseRecord() {
        // 获取输入数据
        String exerciseType = exerciseTypeSpinner.getSelectedItem().toString();
        String durationText = durationEditText.getText().toString();
        String distanceText = distanceEditText.getText().toString();

        // 验证输入
        if (durationText.isEmpty() || distanceText.isEmpty()) {
            Toast.makeText(requireContext(), "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        // 解析输入
        int duration = Integer.parseInt(durationText);
        double distance = Double.parseDouble(distanceText);

        // 获取当前日期
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        // 保存记录
        long result = recordDao.insertRecord(currentDate, exerciseType, duration, distance);

        if (result > 0) {
            Toast.makeText(requireContext(), "运动记录添加成功", Toast.LENGTH_SHORT).show();

            // 清空输入框
            durationEditText.setText("");
            distanceEditText.setText("");

            // 发送广播通知记录界面刷新数据(四大组件之广播接收器的应用）
            Intent broadcastIntent = new Intent("com.health.management.EXERCISE_RECORD_SAVED");
            requireContext().sendBroadcast(broadcastIntent);

            // 重新加载记录和统计信息
            loadExerciseRecords();
            loadStatistics();
        } else {
            Toast.makeText(requireContext(), "运动记录添加失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadExerciseRecords() {
        // 加载最近10条记录
        List<ExerciseRecordDao.ExerciseRecord> records = recordDao.getRecentRecords(10);
        adapter = new ExerciseRecordAdapter(records);
        recordsRecyclerView.setAdapter(adapter);
    }

    private void loadStatistics() {
        // 加载周统计信息
        ExerciseRecordDao.Statistics stats = recordDao.getWeeklyStatistics();

        String statsText = "本周运动统计:\n" +
                "总时长: " + stats.getTotalDuration() + " 分钟\n" +
                "总消耗: " + String.format("%.1f", stats.getTotalCalories()) + " 卡路里";

        statisticsTextView.setText(statsText);
    }

    /**
     * 运动记录适配器
     */
    private class ExerciseRecordAdapter extends RecyclerView.Adapter<ExerciseRecordViewHolder> {
        private List<ExerciseRecordDao.ExerciseRecord> records;

        public ExerciseRecordAdapter(List<ExerciseRecordDao.ExerciseRecord> records) {
            this.records = records;
        }

        @Override
        public ExerciseRecordViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_exercise_record, parent, false);
            return new ExerciseRecordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ExerciseRecordViewHolder holder, int position) {
            ExerciseRecordDao.ExerciseRecord record = records.get(position);

            holder.dateTextView.setText(record.getDate());
            holder.typeTextView.setText(record.getType());
            holder.detailsTextView.setText(record.getDuration() + "分钟 | " +
                    record.getDistance() + "公里 | " +
                    String.format("%.1f", record.getCalories()) + "卡路里");
        }

        @Override
        public int getItemCount() {
            return records.size();
        }
    }

    /**
     * 运动记录ViewHolder
     */
    private class ExerciseRecordViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView typeTextView;
        TextView detailsTextView;

        public ExerciseRecordViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            typeTextView = itemView.findViewById(R.id.type_text_view);
            detailsTextView = itemView.findViewById(R.id.details_text_view);
        }
    }
}