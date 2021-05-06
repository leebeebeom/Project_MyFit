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

public class GoBackDialog extends DialogFragment {

    private int mNavGraphId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNavGraphId = GoBackDialogArgs.fromBundle(getArguments()).getNavGraphId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtil dialogUtil = new DialogUtil(requireContext(), this, mNavGraphId)
                .setValueBackStackLive(R.id.goBackDialog);

        AlertDialog alertDialog = dialogUtil.getConfirmDialog(getString(R.string.dialog_message_go_back));

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> dialogUtil.goBackConfirm());
        return alertDialog;
    }
}
