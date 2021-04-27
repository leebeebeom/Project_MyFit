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
        String message = mSelectedItemSize + getString(R.string.dialog_message_selected_item_delete);

        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.nav_graph_main).backStackLiveSetValue(R.id.selectedItemDeleteDialog);        //tested

        AlertDialog alertDialog = dialogUtils.getConfirmDialog(message);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v ->
                dialogUtils.selectedItemDeleteConfirm());
        return alertDialog;
    }
}
