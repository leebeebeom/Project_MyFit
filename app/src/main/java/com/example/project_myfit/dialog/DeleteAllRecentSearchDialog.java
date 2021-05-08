package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.project_myfit.R;
import com.example.project_myfit.util.Constant;

import org.jetbrains.annotations.NotNull;

public class DeleteAllRecentSearchDialog extends ParentDialogFragment {

    private NavController mNavController;
    private DialogViewModel mDialogViewModel;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNavController = NavHostFragment.findNavController(this);
        mDialogViewModel = new ViewModelProvider(mNavController.getViewModelStoreOwner(mNavController.getGraph().getId()))
                .get(DialogViewModel.class);
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
        getBackStackEntry().getSavedStateHandle().set(Constant.BackStackStateHandleKey.DELETE_ALL_RECENT_SEARCH_CONFIRM.name(), null);
    }
}
