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

public class DeleteConFirmDialog extends DialogFragment {
    public interface DeleteConfirmClick {
        void deleteConfirmClick();
    }

    private DeleteConfirmClick mListener;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (DeleteConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage("휴지통으로 이동하시겠습니까?")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog, which) -> mListener.deleteConfirmClick());
        return builder.create();
    }
}
