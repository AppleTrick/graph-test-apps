package com.example.graphpilottest.javaxml;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView selectedText = view.findViewById(R.id.selected_ringtone_text);
        TextView overlayStatus = view.findViewById(R.id.overlay_status_text);
        RadioGroup radioGroup = view.findViewById(R.id.ringtone_radio_group);
        Button dialogButton = view.findViewById(R.id.open_modal_dialog_button);
        Button bottomSheetButton = view.findViewById(R.id.open_bottom_sheet_button);
        Button popupButton = view.findViewById(R.id.open_popup_menu_button);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = group.findViewById(checkedId);
            if (selected != null) {
                selectedText.setText("현재 선택: " + selected.getText());
                selectedText.setContentDescription("현재 선택 벨소리 " + selected.getText());
            }
        });

        dialogButton.setOnClickListener(button -> {
            overlayStatus.setText("Overlay 상태: Modal Dialog 열림");
            overlayStatus.setContentDescription("Overlay 상태 Modal Dialog 열림");
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Modal Dialog")
                    .setMessage("중앙에 뜨는 확인/취소형 modal surface 입니다.")
                    .setPositiveButton("확인", (dialog, which) -> {
                        overlayStatus.setText("Overlay 상태: Dialog 확인 선택");
                        overlayStatus.setContentDescription("Overlay 상태 Dialog 확인 선택");
                    })
                    .setNegativeButton("취소", (dialog, which) -> {
                        overlayStatus.setText("Overlay 상태: Dialog 취소 선택");
                        overlayStatus.setContentDescription("Overlay 상태 Dialog 취소 선택");
                    })
                    .show();
        });

        bottomSheetButton.setOnClickListener(button -> {
            overlayStatus.setText("Overlay 상태: Modal BottomSheet 열림");
            overlayStatus.setContentDescription("Overlay 상태 Modal BottomSheet 열림");
            showBottomSheet(overlayStatus);
        });

        popupButton.setOnClickListener(button -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), popupButton);
            popupMenu.getMenu().add("정렬: 최신순");
            popupMenu.getMenu().add("정렬: 가격순");
            popupMenu.getMenu().add("보기: 카드형");
            popupMenu.setOnMenuItemClickListener(item -> {
                overlayStatus.setText("Overlay 상태: Popup 선택 " + item.getTitle());
                overlayStatus.setContentDescription("Overlay 상태 Popup 선택 " + item.getTitle());
                return true;
            });
            popupMenu.show();
        });

        return view;
    }

    private void showBottomSheet(TextView overlayStatus) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        LinearLayout sheetLayout = new LinearLayout(requireContext());
        sheetLayout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        sheetLayout.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(requireContext());
        title.setText("Modal BottomSheet");
        title.setTextSize(22);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        sheetLayout.addView(title);

        TextView stateText = new TextView(requireContext());
        stateText.setText("현재 단계: 요약 단계");
        stateText.setContentDescription("BottomSheet 현재 단계 요약 단계");
        sheetLayout.addView(stateText);

        Button changeButton = new Button(requireContext());
        changeButton.setText("시트 내부 상태 변경");
        changeButton.setContentDescription("BottomSheet 내부 상태 변경");
        changeButton.setOnClickListener(view -> {
            stateText.setText("현재 단계: 상세 옵션 선택됨");
            stateText.setContentDescription("BottomSheet 현재 단계 상세 옵션 선택됨");
            overlayStatus.setText("Overlay 상태: BottomSheet 내부 상태 변경");
            overlayStatus.setContentDescription("Overlay 상태 BottomSheet 내부 상태 변경");
        });
        sheetLayout.addView(changeButton);

        Button applyButton = new Button(requireContext());
        applyButton.setText("적용");
        applyButton.setContentDescription("BottomSheet 적용");
        applyButton.setOnClickListener(view -> {
            overlayStatus.setText("Overlay 상태: BottomSheet 적용 완료");
            overlayStatus.setContentDescription("Overlay 상태 BottomSheet 적용 완료");
            bottomSheetDialog.dismiss();
        });
        sheetLayout.addView(applyButton);

        bottomSheetDialog.setContentView(sheetLayout);
        bottomSheetDialog.show();
    }
}
