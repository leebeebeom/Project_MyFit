package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

import static com.example.project_myfit.MyFitConstant.SORT;

public class SortDialog extends DialogFragment {

    private AtomicInteger mCheckedItem;

    public interface SortConfirmClick {
        void sortConfirmClick(int which);
    }

    private SortConfirmClick mListener;

    public SortDialog() {
    }

    public static SortDialog getInstance(int sort) {
        SortDialog sortDialog = new SortDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(SORT, sort);
        sortDialog.setArguments(bundle);
        return sortDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (SortConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mCheckedItem = new AtomicInteger();
        if (getArguments() != null) mCheckedItem.set(getArguments().getInt(SORT));
        if (savedInstanceState != null) mCheckedItem.set(savedInstanceState.getInt(SORT));

        String[] items = new String[]{"사용자 정의", "최근 생성 순", "최근 생성 역순"};
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("정렬 순서")
                .setSingleChoiceItems(items, mCheckedItem.get(), (dialog1, which) -> {
                    if (which == 0) mCheckedItem.set(0);
                    else if (which == 1) mCheckedItem.set(1);
                    else mCheckedItem.set(2);
                })
                .setNegativeButton(" 취소", null)
                .setPositiveButton("확인", (dialog1, which) -> mListener.sortConfirmClick(mCheckedItem.get()))
                .show();

        float titleSize = getResources().getDimension(R.dimen._5sdp);
        int titleId = getResources().getIdentifier("alertTitle", "id", requireContext().getPackageName());
        TextView title = dialog.findViewById(titleId);
        if (title != null) title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleSize);

        float size = getResources().getDimensionPixelSize(R.dimen._4sdp);
        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        Button negative = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negative.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);

        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORT, mCheckedItem.get());
    }
}