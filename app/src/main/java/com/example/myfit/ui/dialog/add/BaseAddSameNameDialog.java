package com.example.myfit.ui.dialog.add;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.ui.dialog.BaseDialogViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class BaseAddSameNameDialog extends BaseDialog {
    private NavController navController;
    private BaseDialogViewModel model;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        NavBackStackEntry addGraphBackStackEntry = navController.getBackStackEntry(navController.getGraph().getId());
        model = getModel(addGraphBackStackEntry);
    }

    protected abstract BaseDialogViewModel getModel(NavBackStackEntry addGraphBackStackEntry);

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return getAlertDialog();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        return dialogBuilder.makeConfirmDialog(getTitle())
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    protected abstract String getTitle();

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            model.insert();
            navController.popBackStack(getDestinationId(), true);
        };
    }

    protected abstract int getDestinationId();
}
