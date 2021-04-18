package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.CATEGORY;

public class AddDialog extends DialogFragment {

    private String mItemType;
    private String mParentCategory;
    private long mParentId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemType = AddDialogArgs.fromBundle(getArguments()).getItemType();
        mParentCategory = AddDialogArgs.fromBundle(getArguments()).getParentCategory();
        mParentId = AddDialogArgs.fromBundle(getArguments()).getParentId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.main_nav_graph).backStackLiveSetValue(R.id.addDialog);

        ItemDialogEditTextBinding binding = dialogUtils.getBinding(null, mItemType);
        AlertDialog alertDialog;
        if (mItemType.equals(CATEGORY))
            alertDialog = dialogUtils.getEditTextDialog(binding, getString(R.string.add_category), null);
        else
            alertDialog = dialogUtils.getEditTextDialog(binding, getString(R.string.add_folder), null);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(v -> {
            InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

            String newName = String.valueOf(binding.dialogEditText.getText()).trim();

            if (mItemType.equals(CATEGORY))
                dialogUtils.addCategoryConfirmClick(newName, mParentCategory, false);
            else dialogUtils.addFolderConfirmClick(newName, mParentId, mParentCategory, false);
        });
        return alertDialog;
    }
}
