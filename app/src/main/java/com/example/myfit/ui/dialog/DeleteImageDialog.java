package com.example.myfit.ui.dialog;

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

import com.example.myfit.R;
import com.example.myfit.ui.main.MainGraphViewModel;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeleteImageDialog extends BaseDialog {
    public static final String DELETE_IMAGE = "image delete";
    private NavBackStackEntry navBackStackEntry;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);
        navBackStackEntry = navController.getBackStackEntry(R.id.deleteImageDialog);
        setBackStackEntryLive(navController);
    }

    private void setBackStackEntryLive(@NotNull NavController navController) {
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
        return dialogBuilder.makeConfirmDialog(getString(R.string.dialog_message_image_clear))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            navBackStackEntry.getSavedStateHandle().set(DELETE_IMAGE, null);
            dismiss();
        };
    }
}
