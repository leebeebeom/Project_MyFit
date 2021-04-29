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

public class SameNameEditDialog extends DialogFragment {

    private String mItemType;
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
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.nav_graph_main).backStackLiveSetValue(R.id.sameNameEditDialog);

        AlertDialog alertDialog = getDialog(dialogUtils);

        positiveClick(dialogUtils, alertDialog);
        return alertDialog;
    }

    @NotNull
    private AlertDialog getDialog(DialogUtils dialogUtils) {
        if (mItemType.equals(CATEGORY))
            return dialogUtils.getConfirmDialog(getString(R.string.dialog_message_same_category_name_edit));
        else
            return dialogUtils.getConfirmDialog(getString(R.string.dialog_message_same_folder_name_edit));
    }

    private void positiveClick(DialogUtils dialogUtils, @NotNull AlertDialog alertDialog) {
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            if (mItemType.equals(CATEGORY))
                dialogUtils.sameNameCategoryEdit(mItemId, mNewName, mIsParentName);
            else
                dialogUtils.sameNameFolderEdit(mItemId, mNewName, mIsParentName, false);
        });
    }
}
