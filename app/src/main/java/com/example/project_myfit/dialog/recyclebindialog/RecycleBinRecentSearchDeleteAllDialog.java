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

public class RecycleBinRecentSearchDeleteAllDialog extends DialogFragment {

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.nav_graph_recycle_bin)
                .backStackLiveSetValue(R.id.recycleBinRecentSearchDeleteAllDialog);

        AlertDialog alertDialog = dialogUtils.getConfirmDialog(getString(R.string.dialog_message_recent_search_delete_all));

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> dialogUtils.recentSearchDeleteAll());
        return alertDialog;
    }
}
