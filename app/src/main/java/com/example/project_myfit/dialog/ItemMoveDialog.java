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

public class ItemMoveDialog extends DialogFragment {

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        int selectedItemSize = ItemMoveDialogArgs.fromBundle(getArguments()).getSelectedItemSize();
        long folderId = ItemMoveDialogArgs.fromBundle(getArguments()).getFolderId();

        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this).setBackStack(R.id.itemMoveDialog);


        AlertDialog alertDialog = dialogUtils.getConfirmDialog();
        String message = selectedItemSize + getString(R.string.item_move_check);
        alertDialog.setMessage(message);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> dialogUtils.itemMoveConfirmClick(folderId));

        return alertDialog;
    }
}
