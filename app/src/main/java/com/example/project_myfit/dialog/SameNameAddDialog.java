package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.CATEGORY;

public class SameNameAddDialog extends DialogFragment {

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        String itemType = SameNameAddDialogArgs.fromBundle(getArguments()).getItemType();
        String parentCategory = SameNameAddDialogArgs.fromBundle(getArguments()).getParentCategory();
        long folderId = SameNameAddDialogArgs.fromBundle(getArguments()).getFolderId();
        String newName = SameNameAddDialogArgs.fromBundle(getArguments()).getNewName();

        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this);

        AlertDialog alertDialog = dialogUtils.getConfirmDialog();

        if (itemType.equals(CATEGORY))
            alertDialog.setTitle(R.string.same_category_name_add);
        else alertDialog.setTitle(R.string.same_folder_name_add);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            if (itemType.equals(CATEGORY))
                dialogUtils.sameNameCategoryAddConfirmClick(newName, parentCategory);
            else dialogUtils.sameNameFolderAddConfirmClick(newName, parentCategory, folderId);
        });

        return alertDialog;
    }
}
