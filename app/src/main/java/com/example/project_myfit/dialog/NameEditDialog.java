package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
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

        AlertDialog alertDialog;
        if (mItemType.equals(CATEGORY))
            alertDialog = dialogUtils.getEditTextDialog(mBinding, getString(R.string.dialog_title_edit_category_name), finalOldName);
        else
            alertDialog = dialogUtils.getEditTextDialog(mBinding, getString(R.string.dialog_title_edit_folder_name), finalOldName);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(v -> {
            InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

            String newName = String.valueOf(mBinding.etDialog.getText()).trim();
            if (category != null)
                dialogUtils.categoryNameEdit(category, newName, mIsParentName, false);
            else if (folder != null)
                dialogUtils.folderNameEdit(folder, newName, mIsParentName, false);
        });

        mBinding.etDialog.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && positiveButton.isEnabled())
                positiveButton.callOnClick();
            return false;
        });

        return alertDialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_EDIT_NAME, String.valueOf(mBinding.etDialog.getText()));
    }
}
