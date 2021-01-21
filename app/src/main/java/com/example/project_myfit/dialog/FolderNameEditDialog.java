package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

public class FolderNameEditDialog extends DialogFragment {

    private ItemDialogEditTextBinding mBinding;

    public interface FolderNameEditConfirmClick {
        void folderNameEditConfirmClick(String folderName);
    }

    private FolderNameEditConfirmClick mListener;

    public FolderNameEditDialog() {
    }

    public static FolderNameEditDialog newInstance(String folderName) {
        FolderNameEditDialog folderNameEditDialog = new FolderNameEditDialog();
        Bundle bundle = new Bundle();
        bundle.putString("name", folderName);
        folderNameEditDialog.setArguments(bundle);
        return folderNameEditDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (FolderNameEditConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        String folderName = null;
        if (getArguments() != null) folderName = getArguments().getString("name");
        if (savedInstanceState != null) folderName = savedInstanceState.getString("name");

        mBinding = ItemDialogEditTextBinding.inflate(LayoutInflater.from(requireContext()));
        mBinding.setHint("Folder Name");
        mBinding.setSetText(folderName);
        mBinding.editTextDialog.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mBinding.editTextDialog.requestFocus();
        mBinding.editTextDialog.setMaxLines(3);
        mBinding.editTextLayoutDialog.setHelperText("폴더 이름은 최대 3줄까지 표현 가능합니다.");

        Dialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("폴더 이름 변경")
                .setView(mBinding.getRoot())
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog1, which) -> mListener.folderNameEditConfirmClick(String.valueOf(mBinding.editTextDialog.getText())))
                .create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", String.valueOf(mBinding.editTextDialog.getText()));
    }
}
