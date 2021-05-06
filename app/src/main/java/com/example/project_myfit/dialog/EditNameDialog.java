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

public class EditNameDialog extends DialogFragment {
    private ItemDialogEditTextBinding mBinding;
    private int mItemType, mNavGraphId;
    private boolean mIsParentName;
    private long mItemId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemType = EditNameDialogArgs.fromBundle(getArguments()).getItemType();
        mIsParentName = EditNameDialogArgs.fromBundle(getArguments()).getIsParentName();
        mItemId = EditNameDialogArgs.fromBundle(getArguments()).getItemId();
        mNavGraphId = EditNameDialogArgs.fromBundle(getArguments()).getNavGraphId();
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
        setPositiveClickListener(dialogUtil, category, folder, positiveButton);
        dialogUtil.setOnImeClickListener(mBinding, positiveButton);
        return alertDialog;
    }

    private void setPositiveClickListener(DialogUtil dialogUtil, Category category, Folder folder, @NotNull Button positiveButton) {
        positiveButton.setOnClickListener(v -> {
            CommonUtil.keyBoardHide(requireContext(), v);

            String newName = String.valueOf(mBinding.et.getText()).trim();

            if (category != null) {
                if (dialogUtil.getDialogViewModel().isSameNameCategory(newName, category.getParentCategory()))
                    CommonUtil.navigate(dialogUtil.getNavController(), R.id.nameEditDialog,
                            EditNameDialogDirections.toEditSameNameDialog(CATEGORY, category.getId(), newName, mIsParentName, mNavGraphId));
                else dialogUtil.editCategoryName(category, newName, mIsParentName);
            } else {
                if (dialogUtil.getDialogViewModel().isSameNameFolder(newName, folder.getParentId()))
                    CommonUtil.navigate(dialogUtil.getNavController(), R.id.nameEditDialog,
                            EditNameDialogDirections.toEditSameNameDialog(FOLDER, folder.getId(), newName, mIsParentName, mNavGraphId));
                dialogUtil.editFolderName(folder, newName, mIsParentName);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_EDIT_NAME, String.valueOf(mBinding.et.getText()));
    }
}