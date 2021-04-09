package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.CATEGORY_NAME;
import static com.example.project_myfit.MyFitConstant.PARENT_CATEGORY;

public class SameCategoryNameDialog extends DialogFragment {
    private SameCategoryNameConfirmListener mListener;

    @NotNull
    public static SameCategoryNameDialog getInstance(String categoryName, String parentCategory) {
        SameCategoryNameDialog sameCategoryNameDialog = new SameCategoryNameDialog();
        Bundle bundle = new Bundle();
        bundle.putString(CATEGORY_NAME, categoryName);
        bundle.putString(PARENT_CATEGORY, parentCategory);
        sameCategoryNameDialog.setArguments(bundle);
        return sameCategoryNameDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (SameCategoryNameConfirmListener) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        String categoryName = getArguments() != null ? getArguments().getString(CATEGORY_NAME) : null;
        String parentCategory = getArguments() != null ? getArguments().getString(PARENT_CATEGORY) : null;

        return DialogUtils.getConfirmDialog(requireContext(), getString(R.string.same_category_name),
                (dialog, which) -> mListener.SameCategoryNameConfirmClick(categoryName, parentCategory));
    }

    public interface SameCategoryNameConfirmListener {
        void SameCategoryNameConfirmClick(String categoryName, String parentCategory);
    }
}
