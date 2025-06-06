package com.health.management.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.health.management.R;
import com.health.management.data.ExerciseRecordDao;
import java.util.List;

public class HomeFragment extends Fragment {
    private TextView tvStatistics;
    private TextView tvRecentRecords;
    
    private ExerciseRecordDao exerciseRecordDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        initViews(view);
        exerciseRecordDao = new ExerciseRecordDao(requireContext());
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
    }

    private void initViews(View view) {
        tvStatistics = view.findViewById(R.id.tv_statistics);
        tvRecentRecords = view.findViewById(R.id.tv_recent_records);
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
        for (ExerciseRecordDao.ExerciseRecord record : records) {
            recordsText.append("- ").append(record.toString()).append("\n");
        }
        tvRecentRecords.setText(recordsText.toString());
    }
}    