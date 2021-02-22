package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;

import org.jetbrains.annotations.NotNull;

public class AddCategoryDialog extends DialogFragment {
    private AddCategoryConfirmClick mListener;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (AddCategoryConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ItemDialogEditTextBinding binding = DialogUtils.getCategoryBinding(getLayoutInflater(), requireContext());
        DialogInterface.OnClickListener listener = (dialog, which) -> mListener.addCategoryConfirmClick(String.valueOf(binding.dialogEditText.getText()));
        return DialogUtils.getEditTextDialog(requireContext(), getString(R.string.add_category), binding, listener);
    }

    public interface AddCategoryConfirmClick {
        void addCategoryConfirmClick(String categoryName);
    }
}
