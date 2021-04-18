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

    private int mSelectedItemSize;
    private long mParentId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedItemSize = ItemMoveDialogArgs.fromBundle(getArguments()).getSelectedItemSize();
        mParentId = ItemMoveDialogArgs.fromBundle(getArguments()).getParentId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        String message = mSelectedItemSize + getString(R.string.item_move_check);

        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.main_nav_graph).backStackLiveSetValue(R.id.itemMoveDialog);

        AlertDialog alertDialog = dialogUtils.getConfirmDialog(message);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> dialogUtils.itemMoveConfirmClick(mParentId));

        return alertDialog;
    }
}
