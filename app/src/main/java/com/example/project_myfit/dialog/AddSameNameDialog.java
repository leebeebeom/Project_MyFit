package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;

import com.example.project_myfit.R;
import com.example.project_myfit.util.Constant;

import org.jetbrains.annotations.NotNull;

public class AddSameNameDialog extends ParentDialogFragment {
    private long mParentId;
    private String mItemName;
    private int mItemTypeIndex, mParentCategoryIndex;
    private NavController mNavController;
    private DialogViewModel mDialogViewModel;
    private String mDialogMessage;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemTypeIndex = AddSameNameDialogArgs.fromBundle(getArguments()).getItemTypeIndex();
        mParentCategoryIndex = AddSameNameDialogArgs.fromBundle(getArguments()).getParentCategoryIndex();
        mItemName = AddSameNameDialogArgs.fromBundle(getArguments()).getItemName();
        mParentId = AddSameNameDialogArgs.fromBundle(getArguments()).getParentId();
        mNavController = getNavController(this);
        mDialogViewModel = getDialogViewModel(mNavController);

        if (isItemTypeCategory())
            mDialogMessage = getString(R.string.dialog_message_same_category_name_add);
        else mDialogMessage = getString(R.string.dialog_message_same_folder_name_add);

    }
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return new DialogBuilder(requireContext())
                .setTitle(getString(R.string.dialog_confirm))
                .setMessage(mDialogMessage)
                .setBackgroundDrawable()
                .setTitleTextSize()
                .setMessageTextSize()
                .setButtonTextSize()
                .setMessageTopPadding()
                .setPositiveButtonClickListener(getPositiveButtonClickListener())
                .create();
    }

    @NotNull
    protected View.OnClickListener getPositiveButtonClickListener() {
        return v -> {
            if (isItemTypeCategory())
                mDialogViewModel.insertCategory(mItemName, mParentCategoryIndex);
            else mDialogViewModel.insertFolder(mItemName, mParentId, mParentCategoryIndex);
            setBackStackStateHandleValue();
            mNavController.popBackStack(R.id.addDialog, true);
        };
    }

    @Override
    protected void setBackStackEntryLiveValue() {
        mDialogViewModel.getBackStackEntryLive().setValue(getBackStackEntry());
    }

    @Override
    protected void setBackStackStateHandleValue() {
        getBackStackEntry().getSavedStateHandle().set(Constant.DialogBackStackStateHandleKey.ADD_SAME_NAME_CONFIRM.name(), null);
    }

    @Override
    protected NavBackStackEntry getBackStackEntry() {
        return mNavController.getBackStackEntry(R.id.addSameNameDialog);
    }

    private boolean isItemTypeCategory() {
        return mItemTypeIndex == Constant.ItemType.CATEGORY.ordinal();
    }
}
