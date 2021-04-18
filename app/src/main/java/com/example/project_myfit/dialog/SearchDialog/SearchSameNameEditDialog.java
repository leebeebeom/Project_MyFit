package com.example.project_myfit.dialog.SearchDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.dialog.DialogUtils;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.CATEGORY;

public class SearchSameNameEditDialog extends DialogFragment {

    private String mItemType;
    private long mItemId;
    private String mNewName;
    private boolean mIsParentName;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemType = SearchSameNameEditDialogArgs.fromBundle(getArguments()).getItemType();
        mItemId = SearchSameNameEditDialogArgs.fromBundle(getArguments()).getItemId();
        mNewName = SearchSameNameEditDialogArgs.fromBundle(getArguments()).getNewName();
        mIsParentName = SearchSameNameEditDialogArgs.fromBundle(getArguments()).getIsParentName();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtils dialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.search_nav_gragh)
                .backStackLiveSetValue(R.id.searchSameNameEditDialog);

        AlertDialog alertDialog;
        if (mItemType.equals(CATEGORY))
            alertDialog = dialogUtils.getConfirmDialog(getString(R.string.same_category_name_edit));
        else alertDialog = dialogUtils.getConfirmDialog(getString(R.string.same_folder_name_edit));

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            if (mItemType.equals(CATEGORY))
                dialogUtils.sameNameCategoryEditConfirmClick(mItemId, mNewName, mIsParentName, true);
            else dialogUtils.sameNameFolderEditConfirmClick(mItemId, mNewName, mIsParentName, true);
        });

        return alertDialog;
    }
}
