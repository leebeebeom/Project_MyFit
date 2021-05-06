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
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.CATEGORY;
import static com.example.project_myfit.util.MyFitConstant.FOLDER;

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

        Category category = mItemType == CATEGORY ?
                dialogUtil.getDialogViewModel().getCategory(mItemId) : null;
        Folder folder = mItemType == FOLDER ?
                dialogUtil.getDialogViewModel().getFolder(mItemId) : null;

        AlertDialog alertDialog = mItemType == CATEGORY ?
                dialogUtil.getConfirmDialog(getString(R.string.dialog_message_same_category_name_edit)) :
                dialogUtil.getConfirmDialog(getString(R.string.dialog_message_same_folder_name_edit));

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        setPositiveClickListener(dialogUtil, category, folder, positiveButton);
        return alertDialog;
    }

    private void setPositiveClickListener(DialogUtil dialogUtil, Category category, Folder folder, @NotNull Button positiveButton) {
        positiveButton.setOnClickListener(v -> {
            if (mItemType == CATEGORY)
                dialogUtil.editCategoryName(category, mNewName, mIsParentName);
            else dialogUtil.editFolderName(folder, mNewName, mIsParentName);
        });
    }
}
