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

public class AddFolderDialog extends DialogFragment {
    public interface AddFolderConfirmClick {
        void addFolderConfirmClick(String folderName);
    }

    private AddFolderConfirmClick mListener;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (AddFolderConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ItemDialogEditTextBinding binding = ItemDialogEditTextBinding.inflate(LayoutInflater.from(requireContext()));
        binding.setHint("Folder Name");
        binding.editTextDialog.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        binding.editTextDialog.requestFocus();
        binding.editTextDialog.setMaxLines(3);
        binding.editTextLayoutDialog.setHelperText("폴더 이름은 최대 3줄까지 표현 가능합니다.");

        Dialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("폴더 생성")
                .setView(binding.getRoot())
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog1, which) -> mListener.addFolderConfirmClick(String.valueOf(binding.editTextDialog.getText())))
                .create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }
}
