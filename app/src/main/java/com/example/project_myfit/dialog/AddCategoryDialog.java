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

import static com.example.project_myfit.MyFitConstant.PARENT_CATEGORY;

public class AddCategoryDialog extends DialogFragment {
    private AddCategoryConfirmClick mListener;

    public AddCategoryDialog() {
    }

    @NotNull
    public static AddCategoryDialog getInstance(String parentCategory) {
        //checked
        AddCategoryDialog addCategoryDialog = new AddCategoryDialog();
        Bundle bundle = new Bundle();
        bundle.putString(PARENT_CATEGORY, parentCategory);
        addCategoryDialog.setArguments(bundle);
        return addCategoryDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        //checked
        super.onAttach(context);
        mListener = (AddCategoryConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        String parentCategory = null;
        if (getArguments() != null)
            parentCategory = getArguments().getString(PARENT_CATEGORY);
        //checked
        ItemDialogEditTextBinding binding = DialogUtils.getCategoryBinding(getLayoutInflater(), requireContext(), null);

        String finalParentCategory = parentCategory;
        return DialogUtils.getEditTextDialog(requireContext(), getString(R.string.add_category), binding,
                (dialog, which) -> mListener.addCategoryConfirmClick(String.valueOf(binding.dialogEditText.getText()), finalParentCategory));
    }

    public interface AddCategoryConfirmClick {
        //checked
        void addCategoryConfirmClick(String categoryName, String parentCategory);
    }
}
