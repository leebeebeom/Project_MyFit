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

public class SelectedItemDeleteDialog extends DialogFragment {

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        int selectedItemSize = SelectedItemDeleteDialogArgs.fromBundle(getArguments()).getSelectedItemSize();
        String message = selectedItemSize + getString(R.string.selected_item_delete_check);

        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this).setBackStack(R.id.selectedItemDeleteDialog);

        AlertDialog alertDialog = dialogUtils.getConfirmDialog();
        alertDialog.setTitle(message);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> dialogUtils.selectedItemDeleteConfirmClick());

        return alertDialog;
    }
}
