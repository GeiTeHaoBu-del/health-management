package com.health.management.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    private BroadcastReceiver exerciseRecordReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.health.management.EXERCISE_RECORD_SAVED".equals(intent.getAction())) {
                loadRecords();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        
        initViews(view);
        exerciseRecordDao = new ExerciseRecordDao(requireContext());

        // 注册广播接收器
        IntentFilter filter = new IntentFilter("com.health.management.EXERCISE_RECORD_SAVED");
        requireContext().registerReceiver(exerciseRecordReceiver, filter);
        
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 注销广播接收器
        requireContext().unregisterReceiver(exerciseRecordReceiver);
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