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

import org.jetbrains.annotations.NotNull;

public abstract class BaseSameNameDialog extends BaseDialog {
    private NavController navController;
    private BaseAddViewModel model;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        NavBackStackEntry navBackStackEntry = navController.getBackStackEntry(navController.getGraph().getId());
        model = getModel(navBackStackEntry);
    }

    protected abstract BaseAddViewModel getModel(NavBackStackEntry navBackStackEntry);

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
