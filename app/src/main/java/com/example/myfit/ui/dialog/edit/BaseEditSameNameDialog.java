package com.example.myfit.ui.dialog.edit;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavBackStackEntry;

import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.ui.dialog.BaseDialogViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class BaseEditSameNameDialog extends BaseDialog {
    private NavBackStackEntry navBackStackEntry;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navBackStackEntry = getBackstackEntry();
        setBackStackLive(navBackStackEntry);
    }

    protected abstract NavBackStackEntry getBackstackEntry();

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return getAlertDialog();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        return dialogBuilder.makeConfirmDialog(getMessage())
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    protected abstract String getMessage();

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            getModel().update();
            actionModeOff(navBackStackEntry);
            getNavController().popBackStack(getDestinationId(), true);
            dismiss();
        };
    }

    protected abstract BaseDialogViewModel getModel();

    protected abstract int getDestinationId();
}
