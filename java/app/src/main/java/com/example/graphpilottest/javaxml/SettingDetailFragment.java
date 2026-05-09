package com.example.graphpilottest.javaxml;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingDetailFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";

    public static SettingDetailFragment newInstance(SettingEntry setting) {
        SettingDetailFragment fragment = new SettingDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, setting.title);
        args.putString(ARG_DESCRIPTION, setting.description);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_setting_detail, container, false);
        Bundle args = requireArguments();
        String titleText = args.getString(ARG_TITLE, "설정");
        String descriptionText = args.getString(ARG_DESCRIPTION, "설정 설명");

        TextView title = view.findViewById(R.id.setting_detail_title);
        TextView description = view.findViewById(R.id.setting_detail_description);
        LinearLayout detailContent = view.findViewById(R.id.setting_detail_content);
        Button back = view.findViewById(R.id.back_to_settings_button);

        title.setText(titleText + " 상세");
        title.setContentDescription("화면 제목 " + titleText + " 상세");
        description.setText(descriptionText);
        renderDetailContent(detailContent, titleText);
        back.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showSettings();
            }
        });
        return view;
    }

    private void renderDetailContent(LinearLayout container, String titleText) {
        container.removeAllViews();
        if ("알림 설정".equals(titleText)) {
            renderNotificationSettings(container);
        } else if ("화면 설정".equals(titleText)) {
            renderDisplaySettings(container);
        } else if ("개인정보 설정".equals(titleText)) {
            renderPrivacySettings(container);
        } else if ("저장공간 설정".equals(titleText)) {
            renderStorageSettings(container);
        } else {
            renderGenericSettings(container, titleText);
        }
    }

    private void renderNotificationSettings(LinearLayout container) {
        Switch masterSwitch = new Switch(requireContext());
        masterSwitch.setText("알림 받기");
        masterSwitch.setChecked(true);
        masterSwitch.setContentDescription("알림 설정 토글");
        container.addView(masterSwitch, matchWrap());

        addSectionTitle(container, "알림음 선택");
        RadioGroup soundGroup = new RadioGroup(requireContext());
        soundGroup.setOrientation(RadioGroup.VERTICAL);
        soundGroup.setContentDescription("알림음 선택 목록");
        addRadioButton(soundGroup, "기본 알림음", true);
        addRadioButton(soundGroup, "짧은 알림음", false);
        addRadioButton(soundGroup, "진동만", false);
        container.addView(soundGroup, matchWrap());
    }

    private void renderDisplaySettings(LinearLayout container) {
        TextView brightnessLabel = addSectionTitle(container, "밝기: 65%");
        SeekBar brightness = new SeekBar(requireContext());
        brightness.setMax(100);
        brightness.setProgress(65);
        brightness.setContentDescription("화면 밝기 슬라이더");
        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightnessLabel.setText("밝기: " + progress + "%");
                brightnessLabel.setContentDescription("화면 밝기 " + progress + " 퍼센트");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        container.addView(brightness, matchWrap());

        Switch darkMode = new Switch(requireContext());
        darkMode.setText("다크 모드");
        darkMode.setContentDescription("다크 모드 토글");
        container.addView(darkMode, matchWrap());
    }

    private void renderPrivacySettings(LinearLayout container) {
        addCheckBox(container, "프로필 공개", true);
        addCheckBox(container, "사용 분석 허용", false);
        addCheckBox(container, "위치 기반 추천 허용", false);
    }

    private void renderStorageSettings(LinearLayout container) {
        addStorageRow(container, "이미지 캐시", "128MB");
        addStorageRow(container, "임시 파일", "64MB");
        addStorageRow(container, "오프라인 데이터", "310MB");

        Button clearButton = new Button(requireContext());
        clearButton.setText("캐시 정리");
        clearButton.setContentDescription("캐시 정리 실행");
        container.addView(clearButton, matchWrap());
    }

    private void renderGenericSettings(LinearLayout container, String titleText) {
        Switch settingSwitch = new Switch(requireContext());
        settingSwitch.setText(titleText + " 사용");
        settingSwitch.setContentDescription(titleText + " 토글");
        container.addView(settingSwitch, matchWrap());
    }

    private TextView addSectionTitle(LinearLayout container, String text) {
        TextView title = new TextView(requireContext());
        title.setText(text);
        title.setTextSize(18f);
        title.setContentDescription(text);
        title.setPadding(0, 12, 0, 8);
        container.addView(title, matchWrap());
        return title;
    }

    private void addRadioButton(RadioGroup group, String text, boolean checked) {
        RadioButton radioButton = new RadioButton(requireContext());
        radioButton.setText(text);
        radioButton.setChecked(checked);
        radioButton.setContentDescription("알림음 선택 " + text);
        group.addView(radioButton, matchWrap());
    }

    private void addCheckBox(LinearLayout container, String text, boolean checked) {
        CheckBox checkBox = new CheckBox(requireContext());
        checkBox.setText(text);
        checkBox.setChecked(checked);
        checkBox.setContentDescription("개인정보 옵션 " + text);
        container.addView(checkBox, matchWrap());
    }

    private void addStorageRow(LinearLayout container, String name, String size) {
        TextView row = new TextView(requireContext());
        row.setText(name + ": " + size);
        row.setTextSize(18f);
        row.setContentDescription("저장공간 항목 " + name + " " + size);
        row.setPadding(0, 8, 0, 8);
        container.addView(row, matchWrap());
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }
}
