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

import static com.example.project_myfit.util.MyFitConstant.MOVE_ITEM_CONFIRM;

public class MoveDialog extends DialogFragment {

    private int mSelectedItemSize;
    private long mParentId;
    private int mNavGraphId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedItemSize = MoveDialogArgs.fromBundle(getArguments()).getSelectedItemSize();
        mParentId = MoveDialogArgs.fromBundle(getArguments()).getParentId();
        mNavGraphId = MoveDialogArgs.fromBundle(getArguments()).getNavGraphId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtil dialogUtil = new DialogUtil(requireContext(), this, mNavGraphId).setValueBackStackLive(R.id.moveDialog);

        AlertDialog alertDialog = dialogUtil.getConfirmDialog(mSelectedItemSize + getString(R.string.dialog_message_item_move));

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            dialogUtil.getBackStackEntry().getSavedStateHandle().set(MOVE_ITEM_CONFIRM, mParentId);
            dialogUtil.getNavController().popBackStack(R.id.treeViewDialog, true);
        });
        return alertDialog;
    }
}
