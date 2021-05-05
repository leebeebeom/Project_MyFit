package com.example.project_myfit.dialog.recyclebindialog;

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

import static com.example.project_myfit.util.MyFitConstant.NO_PARENT_CONFIRM;

public class NoParentDialog extends DialogFragment {
    private String[] noParentNameArray;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noParentNameArray = NoParentDialogArgs.fromBundle(getArguments()).getNoParentNameList();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.nav_graph_recycle_bin)
                .backStackLiveSetValue(R.id.noParentDialog);

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < noParentNameArray.length; i++) {
            if (i != noParentNameArray.length -1)
                stringBuilder.append(noParentNameArray[i]).append(", ");
            else stringBuilder.append(noParentNameArray[i]);
        }

        stringBuilder.append(getString(R.string.dialog_message_no_parent));

        AlertDialog alertDialog = dialogUtils.getConfirmDialog(stringBuilder.toString());

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> dialogUtils.getBackStackEntry().getSavedStateHandle().set(NO_PARENT_CONFIRM, null));
        return alertDialog;
    }
}
