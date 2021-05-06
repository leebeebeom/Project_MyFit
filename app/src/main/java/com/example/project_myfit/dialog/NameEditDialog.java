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
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.example.project_myfit.util.CommonUtil;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.CATEGORY;
import static com.example.project_myfit.util.MyFitConstant.FOLDER;
import static com.example.project_myfit.util.MyFitConstant.NAME_EDIT_NAME;

public class NameEditDialog extends DialogFragment {
    private ItemDialogEditTextBinding mBinding;
    private int mItemType, mNavGraphId;
    private boolean mIsParentName;
    private long mItemId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemType = NameEditDialogArgs.fromBundle(getArguments()).getItemType();
        mIsParentName = NameEditDialogArgs.fromBundle(getArguments()).getIsParentName();
        mItemId = NameEditDialogArgs.fromBundle(getArguments()).getItemId();
        mNavGraphId = NameEditDialogArgs.fromBundle(getArguments()).getNavGraphId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtil dialogUtil = new DialogUtil(requireContext(), this, mNavGraphId).setValueBackStackLive(R.id.nameEditDialog);

        Category category = mItemType == CATEGORY ?
                dialogUtil.getDialogViewModel().getCategory(mItemId) : null;
        Folder folder = mItemType == FOLDER ?
                dialogUtil.getDialogViewModel().getFolder(mItemId) : null;

        String oldName = category != null ? category.getCategoryName() : folder != null ? folder.getFolderName() : null;
        final String finalOldName = oldName;
        //입력된 이름 리스토어
        oldName = savedInstanceState != null ? savedInstanceState.getString(NAME_EDIT_NAME) : oldName;

        mBinding = mItemType == CATEGORY ? dialogUtil.getCategoryBinding() : dialogUtil.getFolderBinding();
        mBinding.et.setText(oldName);

        AlertDialog alertDialog = mItemType == CATEGORY ?
                dialogUtil.getEditDialog(mBinding, getString(R.string.dialog_title_edit_category_name), finalOldName) :
                dialogUtil.getEditDialog(mBinding, getString(R.string.dialog_title_edit_folder_name), finalOldName);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveClick(dialogUtil, category, folder, positiveButton);
        dialogUtil.setOnImeClickListener(mBinding, positiveButton);
        return alertDialog;
    }

    private void positiveClick(DialogUtil dialogUtil, Category category, Folder folder, @NotNull Button positiveButton) {
        positiveButton.setOnClickListener(v -> {
            CommonUtil.keyBoardHide(requireContext(), v);

            String newName = String.valueOf(mBinding.et.getText()).trim();

            if (category != null)
                dialogUtil.categoryNameEdit(category, newName, mIsParentName, mNavGraphId);
            else dialogUtil.folderNameEdit(folder, newName, mIsParentName, mNavGraphId);
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_EDIT_NAME, String.valueOf(mBinding.et.getText()));
    }
}
