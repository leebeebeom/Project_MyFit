package com.example.project_myfit.dialog.search_dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.dialog.DialogUtils;

import org.jetbrains.annotations.NotNull;

public class SearchSelectedItemDeleteDialog extends DialogFragment {

    private int mSelectedItemSize;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mSelectedItemSize = SearchSelectedItemDeleteDialogArgs.fromBundle(getArguments()).getSelectedItemSize();
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        String message = mSelectedItemSize + getString(R.string.selected_item_delete_check);

        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.search_nav_gragh).backStackLiveSetValue(R.id.searchSelectedItemDeleteDialog);

        AlertDialog alertDialog = dialogUtils.getConfirmDialog(message);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> dialogUtils.selectedItemDeleteConfirmClick());
        return alertDialog;
    }
}
