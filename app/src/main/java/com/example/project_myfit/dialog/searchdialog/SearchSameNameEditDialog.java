package com.example.project_myfit.dialog.searchdialog;

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

public class SearchSameNameEditDialog extends DialogFragment {

    private long mFolderId;
    private String mNewName;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFolderId = SearchSameNameEditDialogArgs.fromBundle(getArguments()).getFolderId();
        mNewName = SearchSameNameEditDialogArgs.fromBundle(getArguments()).getNewName();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.nav_graph_search)
                .backStackLiveSetValue(R.id.searchSameNameEditDialog);

        AlertDialog alertDialog = dialogUtils.getConfirmDialog(getString(R.string.dialog_message_same_folder_name_edit));

        positiveClick(dialogUtils, alertDialog);
        return alertDialog;
    }

    private void positiveClick(DialogUtils dialogUtils, @NotNull AlertDialog alertDialog) {
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> dialogUtils.sameNameFolderEdit(mFolderId, mNewName, false, true));
    }
}
