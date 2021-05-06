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

import static com.example.project_myfit.util.MyFitConstant.CATEGORY;

public class SameNameEditDialog extends DialogFragment {

    private int mItemType, mNavGraphId;
    private long mItemId;
    private String mNewName;
    private boolean mIsParentName;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemType = SameNameEditDialogArgs.fromBundle(getArguments()).getItemType();
        mItemId = SameNameEditDialogArgs.fromBundle(getArguments()).getItemId();
        mNewName = SameNameEditDialogArgs.fromBundle(getArguments()).getNewName();
        mIsParentName = SameNameEditDialogArgs.fromBundle(getArguments()).getIsParentName();
        mNavGraphId = SameNameEditDialogArgs.fromBundle(getArguments()).getNavGraphId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtil dialogUtil = new DialogUtil(requireContext(), this, mNavGraphId).setValueBackStackLive(R.id.sameNameEditDialog);

        AlertDialog alertDialog = mItemType == CATEGORY ?
                dialogUtil.getConfirmDialog(getString(R.string.dialog_message_same_category_name_edit)) :
                dialogUtil.getConfirmDialog(getString(R.string.dialog_message_same_folder_name_edit));

        positiveClick(dialogUtil, alertDialog);
        return alertDialog;
    }

    private void positiveClick(DialogUtil dialogUtil, @NotNull AlertDialog alertDialog) {
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            if (mItemType == CATEGORY)
                dialogUtil.sameNameCategoryEdit(mItemId, mNewName, mIsParentName);
            else dialogUtil.sameNameFolderEdit(mItemId, mNewName, mIsParentName);
        });
    }
}
