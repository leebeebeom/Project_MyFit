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
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.example.project_myfit.dialog.DialogUtils;
import com.example.project_myfit.util.KeyboardUtil;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.FOLDER;
import static com.example.project_myfit.util.MyFitConstant.NAME_EDIT_NAME;

public class SearchNameEditDialog extends DialogFragment {
    private ItemDialogEditTextBinding mBinding;
    private long mFolderId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFolderId = SearchNameEditDialogArgs.fromBundle(getArguments()).getFolderId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.nav_graph_search)
                .backStackLiveSetValue(R.id.searchNameEditDialog);

        Folder folder = dialogUtils.getDialogViewModel().getFolder(mFolderId);

        String oldName = folder.getFolderName();
        final String finalOldName = oldName;

        //restore
        oldName = savedInstanceState != null ? savedInstanceState.getString(NAME_EDIT_NAME) : oldName;

        mBinding = dialogUtils.getBinding(oldName, FOLDER);

        AlertDialog alertDialog = dialogUtils.getEditTextDialog(mBinding, getString(R.string.dialog_title_edit_folder_name), finalOldName);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveClick(dialogUtils, folder, positiveButton);
        dialogUtils.imeClick(mBinding, positiveButton);
        return alertDialog;
    }

    private void positiveClick(DialogUtils dialogUtils, Folder folder, @NotNull Button positiveButton) {
        positiveButton.setOnClickListener(v -> {
            KeyboardUtil.hide(requireContext(), v);

            String newName = String.valueOf(mBinding.etDialog.getText()).trim();

            dialogUtils.folderNameEdit(folder, newName, false, true);
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_EDIT_NAME, String.valueOf(mBinding.etDialog.getText()));
    }
}
