package com.example.project_myfit.dialog.search_dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.example.project_myfit.dialog.DialogUtils;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.CATEGORY;
import static com.example.project_myfit.util.MyFitConstant.FOLDER;
import static com.example.project_myfit.util.MyFitConstant.NAME_EDIT_NAME;

public class SearchNameEditDialog extends DialogFragment {
    private ItemDialogEditTextBinding mBinding;
    private String mItemType;
    private boolean mIsParentName;
    private long mItemId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemType = SearchNameEditDialogArgs.fromBundle(getArguments()).getItemType();
        mIsParentName = SearchNameEditDialogArgs.fromBundle(getArguments()).getIsParentName();
        mItemId = SearchNameEditDialogArgs.fromBundle(getArguments()).getItemId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.search_nav_gragh)
                .backStackLiveSetValue(R.id.searchNameEditDialog);

        Category category = mItemType.equals(CATEGORY) ? dialogUtils.getDialogViewModel().getCategory(mItemId) : null;
        Folder folder = mItemType.equals(FOLDER) ? dialogUtils.getDialogViewModel().getFolder(mItemId) : null;

        String oldName = category != null ? category.getCategoryName() : folder != null ? folder.getFolderName() : null;
        final String finalOldName = oldName;
        //입력된 이름 리스토어
        oldName = savedInstanceState != null ? savedInstanceState.getString(NAME_EDIT_NAME) : oldName;

        mBinding = dialogUtils.getBinding(oldName, mItemType);

        AlertDialog alertDialog;
        if (mItemType.equals(CATEGORY))
            alertDialog = dialogUtils.getEditTextDialog(mBinding, getString(R.string.edit_category_name), finalOldName);
        else
            alertDialog = dialogUtils.getEditTextDialog(mBinding, getString(R.string.edit_folder_name), finalOldName);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(v -> {
            InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

            String newName = String.valueOf(mBinding.dialogEditText.getText()).trim();
            if (category != null)
                dialogUtils.categoryNameEditConfirmClick(category, newName, mIsParentName, true);
            else if (folder != null)
                dialogUtils.folderNameEditConfirmCLick(folder, newName, mIsParentName, true);
        });
        return alertDialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_EDIT_NAME, String.valueOf(mBinding.dialogEditText.getText()));
    }
}