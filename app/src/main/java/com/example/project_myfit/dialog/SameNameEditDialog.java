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

public class SameNameEditDialog extends DialogFragment {

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        String itemType = SameNameEditDialogArgs.fromBundle(getArguments()).getItemType();
        long itemId = SameNameEditDialogArgs.fromBundle(getArguments()).getItemId();
        String newName = SameNameEditDialogArgs.fromBundle(getArguments()).getNewName();
        boolean isParentName = SameNameEditDialogArgs.fromBundle(getArguments()).getIsParentName();

        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this).setBackStack(R.id.sameNameEditDialog);

        AlertDialog alertDialog = dialogUtils.getConfirmDialog();

        if (itemType.equals(CATEGORY))
            alertDialog.setTitle(R.string.same_category_name_edit);
        else alertDialog.setTitle(R.string.same_folder_name_edit);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            if (itemType.equals(CATEGORY))
                dialogUtils.sameNameCategoryEditConfirmClick(itemId, newName, isParentName);
            else dialogUtils.sameNameFolderEditConfirmClick(itemId, newName, isParentName);
        });

        return alertDialog;
    }
}
