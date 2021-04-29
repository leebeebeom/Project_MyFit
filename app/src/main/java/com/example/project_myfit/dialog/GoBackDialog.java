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

import static com.example.project_myfit.util.MyFitConstant.GO_BACK_CONFIRM;

public class GoBackDialog extends DialogFragment {

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.nav_graph_main)
                .backStackLiveSetValue(R.id.goBackDialog);

        AlertDialog alertDialog = dialogUtils.getConfirmDialog(getString(R.string.dialog_message_go_back));

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v ->
                dialogUtils.getBackStackEntry().getSavedStateHandle().set(GO_BACK_CONFIRM, null));
        return alertDialog;
    }
}
