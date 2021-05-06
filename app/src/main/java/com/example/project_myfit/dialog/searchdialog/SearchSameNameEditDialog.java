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
import com.example.project_myfit.dialog.DialogUtil;

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
        DialogUtil dialogUtil = new DialogUtil(requireContext(), this, R.id.nav_graph_search)
                .backStackLiveSetValue(R.id.searchSameNameEditDialog);

        AlertDialog alertDialog = dialogUtil.getConfirmDialog(getString(R.string.dialog_message_same_folder_name_edit));

        positiveClick(dialogUtil, alertDialog);
        return alertDialog;
    }

    private void positiveClick(DialogUtil dialogUtil, @NotNull AlertDialog alertDialog) {
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> dialogUtil.sameNameFolderEdit(mFolderId, mNewName, false, true));
    }
}
