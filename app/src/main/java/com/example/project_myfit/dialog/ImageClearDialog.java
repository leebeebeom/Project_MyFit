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

public class ImageClearDialog extends DialogFragment {

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.nav_graph_main).backStackLiveSetValue(R.id.imageClearDialog);

        AlertDialog alertDialog = dialogUtils.getConfirmDialog(getString(R.string.dialog_message_image_clear));

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> dialogUtils.imageClearConfirm());
        return alertDialog;
    }
}
