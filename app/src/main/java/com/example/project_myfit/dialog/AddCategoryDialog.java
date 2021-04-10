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

public class AddCategoryDialog extends DialogFragment {
    private AddCategoryConfirmListener mListener;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (AddCategoryConfirmListener) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        String parentCategory = AddCategoryDialogArgs.fromBundle(getArguments()).getParentCategory();

        ItemDialogEditTextBinding binding = DialogUtils.getBinding(getLayoutInflater(), requireContext(), null, CATEGORY);

        return DialogUtils.getEditTextDialog(requireContext(), getString(R.string.add_category), binding,
                (dialog, which) -> mListener.addCategoryConfirmClick(String.valueOf(binding.dialogEditText.getText()).trim(), parentCategory));
    }

    public interface AddCategoryConfirmListener {
        void addCategoryConfirmClick(String categoryName, String parentCategory);
    }
}
