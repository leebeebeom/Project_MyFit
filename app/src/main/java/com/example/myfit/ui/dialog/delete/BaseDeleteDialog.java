package com.example.myfit.ui.dialog.delete;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myfit.data.repository.BaseRepository;
import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.ui.main.MainGraphViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class BaseDeleteDialog extends BaseDialog {
    public static final String DELETE_SELECT_ITEMS = "delete select items";
    private long[] selectedItemIds;
    private NavBackStackEntry navBackStackEntry;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedItemIds = getSelectedItemIds();
        NavController navController = NavHostFragment.findNavController(this);
        navBackStackEntry = getBackStackEntry(navController);
        setBackStackEntryLive(navController);
    }

    @NotNull
    protected abstract NavBackStackEntry getBackStackEntry(NavController navController);

    private void setBackStackEntryLive(NavController navController){
        NavBackStackEntry mainBackStackEntry = navController.getBackStackEntry(navController.getGraph().getId());
        MainGraphViewModel mainGraphViewModel = new ViewModelProvider(mainBackStackEntry, HiltViewModelFactory.create(requireContext(), mainBackStackEntry)).get(MainGraphViewModel.class);
        mainGraphViewModel.setBackStackEntryLive(navBackStackEntry);
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return getAlertDialog();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        String selectedItemSize = String.valueOf(selectedItemIds.length);
        return dialogBuilder.makeConfirmDialog(selectedItemSize + getMessage())
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    protected abstract String getMessage();

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            getRepository().deleteOrRestore(selectedItemIds, true);
            navBackStackEntry.getSavedStateHandle().set(DELETE_SELECT_ITEMS, null);
            dismiss();
        };
    }

    protected abstract long[] getSelectedItemIds();

    protected abstract BaseRepository getRepository();
}
