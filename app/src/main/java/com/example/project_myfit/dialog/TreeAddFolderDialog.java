package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

public class TreeAddFolderDialog extends DialogFragment {
    public interface TreeAddFolderConfirmClick {
        void treeAddFolderConfirmClick(String folderName);
    }

    private TreeAddFolderConfirmClick mListener;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (TreeAddFolderConfirmClick) getTargetFragment();
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
        binding.editTextLayoutDialog.setPlaceholderText("폴더 이름");

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("폴더 생성")
                .setView(binding.getRoot())
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog1, which) -> mListener.treeAddFolderConfirmClick(String.valueOf(binding.editTextDialog.getText())))
                .create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        float size = getResources().getDimensionPixelSize(R.dimen._4sdp);
        float titleSize = getResources().getDimension(R.dimen._5sdp);
        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positive.setEnabled(false);
        positive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        Button negative = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negative.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);

        binding.editTextDialog.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positive.setEnabled(!TextUtils.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        int titleId = getResources().getIdentifier("alertTitle", "id", requireContext().getPackageName());
        TextView title = dialog.findViewById(titleId);
        if (title != null) title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleSize);
        return dialog;
    }
}
