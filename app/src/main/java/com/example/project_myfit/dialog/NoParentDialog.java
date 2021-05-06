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

public class NoParentDialog extends DialogFragment {
    private String[] noParentNameArray;
    private int mNavGraphId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noParentNameArray = NoParentDialogArgs.fromBundle(getArguments()).getNoParentNameList();
        mNavGraphId = NoParentDialogArgs.fromBundle(getArguments()).getNavGraphId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtil dialogUtil = new DialogUtil(requireContext(), this, mNavGraphId)
                .setValueBackStackLive(R.id.noParentDialog);

        String message = getMessage();
        AlertDialog alertDialog = dialogUtil.getConfirmDialog(message);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> dialogUtil.noParentConfirm());
        return alertDialog;
    }

    @NotNull
    private String getMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < noParentNameArray.length; i++) {
            if (i != noParentNameArray.length - 1)
                stringBuilder.append(noParentNameArray[i]).append(", ");
            else stringBuilder.append(noParentNameArray[i]);
        }
        return stringBuilder.append(getString(R.string.dialog_message_no_parent)).toString();
    }
}
