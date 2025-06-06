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

public class RecordFragment extends Fragment {
    private TextView tvRecords;
    
    private ExerciseRecordDao exerciseRecordDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        
        initViews(view);
        exerciseRecordDao = new ExerciseRecordDao(requireContext());
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecords();
    }

    private void initViews(View view) {
        tvRecords = view.findViewById(R.id.tv_records);
    }

    private void loadRecords() {
        // 加载所有记录
        List<ExerciseRecordDao.ExerciseRecord> records = exerciseRecordDao.getRecentRecords(10);
        StringBuilder recordsText = new StringBuilder("运动记录：\n");
        for (ExerciseRecordDao.ExerciseRecord record : records) {
            recordsText.append("- ").append(record.toString()).append("\n");
        }
        tvRecords.setText(recordsText.toString());
    }
}    