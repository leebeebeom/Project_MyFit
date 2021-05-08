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
import com.example.project_myfit.util.DialogUtil;

import org.jetbrains.annotations.NotNull;

public class DeleteForeverDialog extends ParentDialogFragment {
    private int mSelectedItemSize;
    private NavController mNavController;
    private DialogViewModel mDialogViewModel;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedItemSize = DeleteForeverDialogArgs.fromBundle(getArguments()).getSelectedItemSize();
        mNavController = DialogUtil.getNavController(this);
        mDialogViewModel = DialogUtil.getDialogViewModel(mNavController);
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return new DialogBuilder(requireContext())
                .setTitle(getString(R.string.dialog_confirm))
                .setMessage(mSelectedItemSize + getString(R.string.dialog_message_delete_forever))
                .setBackgroundDrawable()
                .setTitleTextSize()
                .setMessageTextSize()
                .setMessageTopPadding()
                .setButtonTextSize()
                .setPositiveButtonClickListener(getPositiveButtonClickListener())
                .create();
    }

    @Override
    protected NavBackStackEntry getBackStackEntry() {
        return mNavController.getBackStackEntry(R.id.deleteForeverDialog);
    }

    @Override
    protected void setBackStackEntryLiveValue() {
        mDialogViewModel.getBackStackEntryLive().setValue(getBackStackEntry());
    }

    @Override
    protected void setBackStackStateHandleValue() {
        getBackStackEntry().getSavedStateHandle().set(Constant.BackStackStateHandleKey.DELETE_FOREVER_CONFIRM.name(), null);
    }

    @Override
    protected View.OnClickListener getPositiveButtonClickListener() {
        return v -> {
            setBackStackEntryLiveValue();
            mNavController.popBackStack();
        };
    }
}
