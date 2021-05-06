package com.example.project_myfit.dialog.searchdialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.dialog.DialogUtil;
import com.example.project_myfit.dialog.SameNameAddDialogArgs;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.CATEGORY;

public class SearchSameNameAddDialog extends DialogFragment {
    private String mItemType;
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
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtil dialogUtil = new DialogUtil(requireContext(), this, R.id.nav_graph_search).backStackLiveSetValue(R.id.searchSameNameAddDialog);

        AlertDialog alertDialog = getDialog(dialogUtil);

        positiveClick(dialogUtil, alertDialog);
        return alertDialog;
    }

    @NotNull
    private AlertDialog getDialog(DialogUtil dialogUtil) {
        if (mItemType.equals(CATEGORY))
            return dialogUtil.getConfirmDialog(getString(R.string.dialog_message_same_category_name_add));
        else
            return dialogUtil.getConfirmDialog(getString(R.string.dialog_message_same_folder_name_add));
    }

    private void positiveClick(DialogUtil dialogUtil, @NotNull AlertDialog alertDialog) {
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            if (mItemType.equals(CATEGORY))
                dialogUtil.sameNameCategoryAdd(mNewName, mParentCategory, true);
            else dialogUtil.sameNameFolderAdd(mNewName, mParentCategory, mParentId, true);
        });
    }
}
