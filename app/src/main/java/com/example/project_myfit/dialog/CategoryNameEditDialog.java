package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.CATEGORY;
import static com.example.project_myfit.MyFitConstant.CATEGORY_NAME;

public class CategoryNameEditDialog extends DialogFragment {
    private ItemDialogEditTextBinding mBinding;
    private CategoryNameEditConfirmListener mListener;

    public CategoryNameEditDialog() {
    }

    @NotNull
    public static CategoryNameEditDialog getInstance(String categoryName) {
        //checked
        CategoryNameEditDialog categoryNameEditDialog = new CategoryNameEditDialog();
        Bundle bundle = new Bundle();
        bundle.putString(CATEGORY_NAME, categoryName);
        categoryNameEditDialog.setArguments(bundle);
        return categoryNameEditDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        //checked
        super.onAttach(context);
        mListener = (CategoryNameEditConfirmListener) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        //checked
        String categoryName = null;
        if (getArguments() != null) categoryName = getArguments().getString(CATEGORY_NAME);
        if (savedInstanceState != null) categoryName = savedInstanceState.getString(CATEGORY_NAME);
        mBinding = DialogUtils.getBinding(getLayoutInflater(), requireContext(), categoryName, CATEGORY);
        return DialogUtils.getEditTextDialog(requireContext(), getString(R.string.edit_category_name), mBinding,
                (dialog, which) -> mListener.categoryNameEditConfirmClick(String.valueOf(mBinding.dialogEditText.getText()).trim()));
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        //checked
        super.onSaveInstanceState(outState);
        outState.putString(CATEGORY_NAME, String.valueOf(mBinding.dialogEditText.getText()));
    }

    public interface CategoryNameEditConfirmListener {
        void categoryNameEditConfirmClick(String categoryName);
    }
}
