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

import static com.example.project_myfit.util.MyFitConstant.DELETE_FOREVER_CONFIRM;

public class DeleteForeverDialog extends DialogFragment {
    private int mSelectedItemSize, mNavGraphId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedItemSize = DeleteForeverDialogArgs.fromBundle(getArguments()).getSelectedItemSize();
        mNavGraphId = DeleteForeverDialogArgs.fromBundle(getArguments()).getNavGraphId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtil dialogUtil = new DialogUtil(requireContext(), this, mNavGraphId)
                .setValueBackStackLive(R.id.deleteForeverDialog);

        AlertDialog alertDialog = dialogUtil.getConfirmDialog(mSelectedItemSize + getString(R.string.dialog_message_delete_forever));

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            dialogUtil.getBackStackEntry().getSavedStateHandle().set(DELETE_FOREVER_CONFIRM, null);
            dialogUtil.getNavController().popBackStack();
        });
        return alertDialog;
    }
}
