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
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;
import com.example.project_myfit.dialog.DialogUtils;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.CATEGORY;

public class SearchAddDialog extends DialogFragment {

    private String mItemType;
    private String mParentCategory;
    private long mParentId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemType = SearchAddDialogArgs.fromBundle(getArguments()).getItemType();
        mParentCategory = SearchAddDialogArgs.fromBundle(getArguments()).getParentCategory();
        mParentId = SearchAddDialogArgs.fromBundle(getArguments()).getParentId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.search_nav_gragh).backStackLiveSetValue(R.id.searchAddDialog);

        ItemDialogEditTextBinding binding = dialogUtils.getBinding(null, mItemType);
        AlertDialog alertDialog;
        if (mItemType.equals(CATEGORY))
            alertDialog = dialogUtils.getEditTextDialog(binding, getString(R.string.add_category), null);
        else
            alertDialog = dialogUtils.getEditTextDialog(binding, getString(R.string.add_folder), null);

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

        positiveButton.setOnClickListener(v -> {
            InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

            String newName = String.valueOf(binding.dialogEditText.getText()).trim();

            if (mItemType.equals(CATEGORY))
                dialogUtils.addCategoryConfirmClick(newName, mParentCategory, true);
            else dialogUtils.addFolderConfirmClick(newName, mParentId, mParentCategory, true);
        });
        return alertDialog;
    }
}
