package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

public class SelectedItemDeleteDialog extends DialogFragment {
    public interface SelectedItemDeleteConfirmClick {
        void selectedItemDeleteConfirmClick();
    }

    private SelectedItemDeleteConfirmClick mListener;

    public SelectedItemDeleteDialog() {
    }

    public static SelectedItemDeleteDialog newInstance(int selectedItemAmount) {
        SelectedItemDeleteDialog selectedItemDeleteDialog = new SelectedItemDeleteDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("amount", selectedItemAmount);
        selectedItemDeleteDialog.setArguments(bundle);
        return selectedItemDeleteDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (SelectedItemDeleteConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        int amount = 0;
        if (getArguments() != null) amount = getArguments().getInt("amount");

        return new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage(amount + "개의 아이템을 휴지통으로 이동하시겠습니까?")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog, which) -> mListener.selectedItemDeleteConfirmClick())
                .create();
    }
}
