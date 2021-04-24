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

public class RecentSearchAllClearDialog extends DialogFragment {

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.search_nav_gragh)
                .backStackLiveSetValue(R.id.recentSearchAllClearDialog);

        AlertDialog alertDialog = dialogUtils.getConfirmDialog(getString(R.string.recent_search_all_clear));

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> dialogUtils.recentSearchAllClearClick());

        return alertDialog;
    }
}
