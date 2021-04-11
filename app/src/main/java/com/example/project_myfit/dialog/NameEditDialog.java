package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import static com.example.project_myfit.MyFitConstant.CATEGORY;
import static com.example.project_myfit.MyFitConstant.FOLDER;
import static com.example.project_myfit.MyFitConstant.NAME_EDIT_NAME;

public class NameEditDialog extends DialogFragment {
    private ItemDialogEditTextBinding mBinding;

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        String itemType = NameEditDialogArgs.fromBundle(getArguments()).getItemType();
        long itemId = NameEditDialogArgs.fromBundle(getArguments()).getItemId();
        boolean isParentName = NameEditDialogArgs.fromBundle(getArguments()).getIsParentName();

        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this).setBackStack(R.id.NameEditDialog);

        Category category = itemType.equals(CATEGORY) ? dialogUtils.getCategory(itemId) : null;
        Folder folder = itemType.equals(FOLDER) ? dialogUtils.getFolder(itemId) : null;

        String name = category != null ? category.getCategoryName() : folder != null ? folder.getFolderName() : null;
        name = savedInstanceState != null ? savedInstanceState.getString(NAME_EDIT_NAME) : name;

        mBinding = dialogUtils.getBinding(name, itemType);
        AlertDialog alertDialog;
        if (itemType.equals(CATEGORY))
            alertDialog = dialogUtils.getEditTextDialog(mBinding, getString(R.string.edit_category_name));
        else
            alertDialog = dialogUtils.getEditTextDialog(mBinding, getString(R.string.edit_folder_name));

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);

        addTextChangedListener(category, folder, positiveButton);

        positiveButton.setOnClickListener(v -> {
            InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

            String newName = String.valueOf(mBinding.dialogEditText.getText()).trim();
            if (category != null)
                dialogUtils.categoryNameEditConfirmClick(category, newName, isParentName);
            else if (folder != null)
                dialogUtils.folderNameEditConfirmCLick(folder, newName, isParentName);
        });
        return alertDialog;
    }

    private void addTextChangedListener(Category category, Folder folder, Button positiveButton) {
        mBinding.dialogEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (category != null)
                    positiveButton.setEnabled(!category.getCategoryName().equals(String.valueOf(s).trim()));
                else if (folder != null)
                    positiveButton.setEnabled(!folder.getFolderName().equals(String.valueOf(s).trim()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_EDIT_NAME, String.valueOf(mBinding.dialogEditText.getText()));
    }
}
