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

import static com.example.project_myfit.MyFitConstant.ALERT_TITLE;
import static com.example.project_myfit.MyFitConstant.CATEGORY_NAME;
import static com.example.project_myfit.MyFitConstant.ID;

public class CategoryNameEditDialog extends DialogFragment {
    private ItemDialogEditTextBinding mBinding;
    private CategoryNameEditConfirmClick mListener;

    public CategoryNameEditDialog() {
    }

    @NotNull
    public static CategoryNameEditDialog getInstance(String categoryName) {
        CategoryNameEditDialog categoryNameEditDialog = new CategoryNameEditDialog();
        Bundle bundle = new Bundle();
        bundle.putString(CATEGORY_NAME, categoryName);
        categoryNameEditDialog.setArguments(bundle);
        return categoryNameEditDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (CategoryNameEditConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        String folderName = "";
        if (getArguments() != null) folderName = getArguments().getString(CATEGORY_NAME);
        if (savedInstanceState != null) folderName = savedInstanceState.getString(CATEGORY_NAME);

        mBinding = ItemDialogEditTextBinding.inflate(LayoutInflater.from(requireContext()));
        mBinding.setHint(getString(R.string.category_name));
        mBinding.setSetText(folderName);
        mBinding.editTextDialog.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mBinding.editTextDialog.requestFocus();
        mBinding.editTextLayoutDialog.setPlaceholderText(getString(R.string.category_name_korean));


        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle(R.string.edit_category_name)
                .setView(mBinding.getRoot())
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, (dialog1, which) -> mListener.categoryNameEditConfirmClick(String.valueOf(mBinding.editTextDialog.getText())))
                .create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        float size = getResources().getDimensionPixelSize(R.dimen._4sdp);
        float titleSize = getResources().getDimension(R.dimen._5sdp);

        Button positive = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);

        Button negative = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negative.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);

        mBinding.editTextDialog.addTextChangedListener(new TextWatcher() {
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

        int titleId = getResources().getIdentifier(ALERT_TITLE, ID, requireContext().getPackageName());
        TextView title = dialog.findViewById(titleId);
        if (title != null) title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleSize);
        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CATEGORY_NAME, String.valueOf(mBinding.editTextDialog.getText()));
    }

    public interface CategoryNameEditConfirmClick {
        void categoryNameEditConfirmClick(String folderName);
    }
}
