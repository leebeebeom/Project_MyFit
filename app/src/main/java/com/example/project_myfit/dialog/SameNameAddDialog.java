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

public class SameNameAddDialog extends DialogFragment {
    private int mItemType, mNavGraphId;
    private String mParentCategory;
    private long mParentId;
    private String mNewName;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemType = SameNameAddDialogArgs.fromBundle(getArguments()).getItemType();
        mParentCategory = SameNameAddDialogArgs.fromBundle(getArguments()).getParentCategory();
        mParentId = SameNameAddDialogArgs.fromBundle(getArguments()).getParentId();
        mNewName = SameNameAddDialogArgs.fromBundle(getArguments()).getNewName();
        mNavGraphId = SameNameAddDialogArgs.fromBundle(getArguments()).getNavGraphId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtil dialogUtil = new DialogUtil(requireContext(), this, mNavGraphId).setValueBackStackLive(R.id.sameNameAddDialog);

        AlertDialog alertDialog = mItemType == CATEGORY ?
                dialogUtil.getConfirmDialog(getString(R.string.dialog_message_same_category_name_add)) :
                dialogUtil.getConfirmDialog(getString(R.string.dialog_message_same_folder_name_add));
        positiveClick(dialogUtil, alertDialog);
        return alertDialog;
    }

    private void positiveClick(DialogUtil dialogUtil, @NotNull AlertDialog alertDialog) {
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            if (mItemType == CATEGORY)
                dialogUtil.sameNameCategoryAdd(mNewName, mParentCategory);
            else dialogUtil.sameNameFolderAdd(mNewName, mParentCategory, mParentId);
        });
    }
}
