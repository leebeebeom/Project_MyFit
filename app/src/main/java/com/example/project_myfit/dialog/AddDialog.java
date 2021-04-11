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

import static com.example.project_myfit.MyFitConstant.CATEGORY;

public class AddDialog extends DialogFragment {

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        String itemType = AddDialogArgs.fromBundle(getArguments()).getItemType();
        String parentCategory = AddDialogArgs.fromBundle(getArguments()).getParentCategory();
        long folderId = AddDialogArgs.fromBundle(getArguments()).getFolderId();

        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this);

        ItemDialogEditTextBinding binding = dialogUtils.getBinding(null, itemType);
        AlertDialog alertDialog = dialogUtils.getEditTextDialog(binding);

        if (itemType.equals(CATEGORY))
            alertDialog.setTitle(R.string.add_category);
        else alertDialog.setTitle(R.string.add_folder);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(v -> {
            InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

            String name = String.valueOf(binding.dialogEditText.getText()).trim();

            if (itemType.equals(CATEGORY))
                dialogUtils.addCategoryConfirmClick(name, parentCategory);
            else dialogUtils.addFolderConfirmClick(name, folderId, parentCategory);
        });

        return alertDialog;
    }
}
