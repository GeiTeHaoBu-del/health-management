package com.health.management.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.health.management.R;
import com.health.management.data.ExerciseRecordDao;
import com.health.management.data.FoodDao;
import com.health.management.ui.DietActivity;
import java.util.List;

public class HomeFragment extends Fragment {
    private TextView tvStatistics;
    private TextView tvRecentRecords;
    private TextView tvDietStatistics;
    private TextView tvRecentDietRecords;

    private ExerciseRecordDao exerciseRecordDao;
    private FoodDao foodDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        initViews(view);
        exerciseRecordDao = new ExerciseRecordDao(requireContext());
        foodDao = new FoodDao(requireContext());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
        loadDietStatistics();
    }

    private void initViews(View view) {
        tvStatistics = view.findViewById(R.id.tv_statistics);
        tvRecentRecords = view.findViewById(R.id.tv_recent_records);
        tvDietStatistics = view.findViewById(R.id.tv_diet_statistics);
        tvRecentDietRecords = view.findViewById(R.id.tv_recent_diet_records);
    }

    private void loadStatistics() {
        // 加载统计数据
        ExerciseRecordDao.Statistics stats = exerciseRecordDao.getWeeklyStatistics();
        String statisticsText = "本周运动统计：\n" +
            "总时长：" + stats.getTotalDuration() + "分钟\n" +
            "总消耗：" + stats.getTotalCalories() + "卡路里";
        tvStatistics.setText(statisticsText);
        
        // 加载最近记录
        List<ExerciseRecordDao.ExerciseRecord> records = exerciseRecordDao.getRecentRecords(3);
        StringBuilder recordsText = new StringBuilder("最近运动记录：\n");

        if (records.isEmpty()) {
            recordsText.append("暂无运动记录");
        } else {
            for (ExerciseRecordDao.ExerciseRecord record : records) {
                recordsText.append("- ").append(record.toString()).append("\n");
            }
        }

        tvRecentRecords.setText(recordsText.toString());
    }

    private void loadDietStatistics() {
        // 加载今日饮食统计
        double todayCalories = foodDao.getTodayTotalCalories();
        String dietStatsText = "今日饮食摄入：\n" +
            "总卡路里：" + String.format("%.1f", todayCalories) + " 卡路里";
        tvDietStatistics.setText(dietStatsText);

        // 加载最近饮食记录
        List<DietActivity.DietRecord> dietRecords = foodDao.getRecentDietRecords(3);
        StringBuilder dietRecordsText = new StringBuilder();

        if (dietRecords.isEmpty()) {
            dietRecordsText.append("暂无饮食记录");
        } else {
            for (DietActivity.DietRecord record : dietRecords) {
                dietRecordsText.append("- ")
                    .append(record.getDate())
                    .append(": ")
                    .append(record.getFoodName())
                    .append(" (")
                    .append(String.format("%.1f", record.getAmount()))
                    .append("份, ")
                    .append(String.format("%.1f", record.getCalories()))
                    .append("卡路里)\n");
            }
        }

        tvRecentDietRecords.setText(dietRecordsText.toString());
    }
}
