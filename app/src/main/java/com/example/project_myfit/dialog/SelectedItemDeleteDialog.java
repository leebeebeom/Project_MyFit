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

    private int mSelectedItemSize;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mSelectedItemSize = SelectedItemDeleteDialogArgs.fromBundle(getArguments()).getSelectedItemSize();
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        //tested
        String message = mSelectedItemSize + getString(R.string.selected_item_delete_check);

        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this).setBackStack(R.id.selectedItemDeleteDialog);

        AlertDialog alertDialog = dialogUtils.getConfirmDialog(message);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> dialogUtils.selectedItemDeleteConfirmClick());

        return alertDialog;
    }
}
