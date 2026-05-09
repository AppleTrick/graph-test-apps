package com.example.graphpilottest.javaxml;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.List;

public class SettingsFragment extends Fragment {
    private final List<SettingEntry> settings = Arrays.asList(
            new SettingEntry("알림 설정", "앱 알림과 소리 옵션을 확인합니다."),
            new SettingEntry("화면 설정", "밝기와 화면 표시 방식을 확인합니다."),
            new SettingEntry("개인정보 설정", "개인정보 표시 옵션을 확인합니다."),
            new SettingEntry("저장공간 설정", "저장공간 사용량 옵션을 확인합니다.")
    );

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        LinearLayout list = view.findViewById(R.id.settings_list);
        list.removeAllViews();

        for (SettingEntry setting : settings) {
            TextView row = new TextView(requireContext());
            row.setText(setting.title + "\n" + setting.description);
            row.setTextSize(18f);
            row.setPadding(20, 20, 20, 20);
            row.setClickable(true);
            row.setFocusable(true);
            row.setContentDescription("설정 항목 " + setting.title);
            row.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showSettingDetail(setting);
                }
            });
            list.addView(row, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
        }

        return view;
    }
}
