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

public class DeleteAllRecentSearchDialog extends ParentDialogFragment {

    private NavController mNavController;
    private DialogViewModel mDialogViewModel;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNavController = DialogUtil.getNavController(this);
        mDialogViewModel = DialogUtil.getDialogViewModel(mNavController);
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return new DialogBuilder(requireContext())
                .setTitle(getString(R.string.dialog_confirm))
                .setMessage(getString(R.string.dialog_message_recent_search_delete_all))
                .setBackgroundDrawable()
                .setTitleTextSize()
                .setMessageTextSize()
                .setMessageTopPadding()
                .setButtonTextSize()
                .setPositiveButtonClickListener(getPositiveButtonClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveButtonClickListener() {
        return v -> {
            mDialogViewModel.recentSearchDeleteAll();
            setBackStackStateHandleValue();
            mNavController.popBackStack();
        };
    }

    @Override
    protected NavBackStackEntry getBackStackEntry() {
        return mNavController.getBackStackEntry(R.id.deleteAllRecentSearchDialog);
    }

    @Override
    protected void setBackStackEntryLiveValue() {
        mDialogViewModel.getBackStackEntryLive().setValue(getBackStackEntry());
    }

    @Override
    protected void setBackStackStateHandleValue() {
        getBackStackEntry().getSavedStateHandle().set(Constant.DialogBackStackStateHandleKey.DELETE_ALL_RECENT_SEARCH_CONFIRM.name(), null);
    }
}
