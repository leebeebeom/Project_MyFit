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
import com.example.project_myfit.util.KeyboardUtil;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.CATEGORY;
import static com.example.project_myfit.util.MyFitConstant.FOLDER;
import static com.example.project_myfit.util.MyFitConstant.NAME_EDIT_NAME;

public class NameEditDialog extends DialogFragment {
    private ItemDialogEditTextBinding mBinding;
    private String mItemType;
    private boolean mIsParentName;
    private long mItemId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemType = NameEditDialogArgs.fromBundle(getArguments()).getItemType();
        mIsParentName = NameEditDialogArgs.fromBundle(getArguments()).getIsParentName();
        mItemId = NameEditDialogArgs.fromBundle(getArguments()).getItemId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.nav_graph_main).backStackLiveSetValue(R.id.nameEditDialog);

        Category category = mItemType.equals(CATEGORY) ? dialogUtils.getDialogViewModel().getCategory(mItemId) : null;
        Folder folder = mItemType.equals(FOLDER) ? dialogUtils.getDialogViewModel().getFolder(mItemId) : null;

        String oldName = category != null ? category.getCategoryName() : folder != null ? folder.getFolderName() : null;
        final String finalOldName = oldName;
        //입력된 이름 리스토어
        oldName = savedInstanceState != null ? savedInstanceState.getString(NAME_EDIT_NAME) : oldName;

        mBinding = dialogUtils.getBinding(oldName, mItemType);

        AlertDialog alertDialog = getDialog(dialogUtils, finalOldName);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveClick(dialogUtils, category, folder, positiveButton);
        dialogUtils.imeClick(mBinding, positiveButton);
        return alertDialog;
    }

    @NotNull
    private AlertDialog getDialog(DialogUtils dialogUtils, String finalOldName) {
        if (mItemType.equals(CATEGORY))
            return dialogUtils.getEditTextDialog(mBinding, getString(R.string.dialog_title_edit_category_name), finalOldName);
        else
            return dialogUtils.getEditTextDialog(mBinding, getString(R.string.dialog_title_edit_folder_name), finalOldName);
    }

    private void positiveClick(DialogUtils dialogUtils, Category category, Folder folder, @NotNull Button positiveButton) {
        positiveButton.setOnClickListener(v -> {
            KeyboardUtil.hide(requireContext(), v);

            String newName = String.valueOf(mBinding.etDialog.getText()).trim();
            if (category != null)
                dialogUtils.categoryNameEdit(category, newName, mIsParentName);
            else if (folder != null)
                dialogUtils.folderNameEdit(folder, newName, mIsParentName, false);
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_EDIT_NAME, String.valueOf(mBinding.etDialog.getText()));
    }
}
