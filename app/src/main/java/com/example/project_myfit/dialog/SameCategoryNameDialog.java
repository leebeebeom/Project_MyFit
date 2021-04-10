package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;

import org.jetbrains.annotations.NotNull;

public class SameCategoryNameDialog extends DialogFragment {
    private SameCategoryNameConfirmListener mListener;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (SameCategoryNameConfirmListener) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        String categoryName = SameCategoryNameDialogArgs.fromBundle(getArguments()).getCategoryName();
        String parentCategory = SameCategoryNameDialogArgs.fromBundle(getArguments()).getParentCategory();

        return DialogUtils.getConfirmDialog(requireContext(), getString(R.string.same_category_name),
                (dialog, which) -> mListener.SameCategoryNameConfirmClick(categoryName, parentCategory));
    }

    public interface SameCategoryNameConfirmListener {
        void SameCategoryNameConfirmClick(String categoryName, String parentCategory);
    }
}
