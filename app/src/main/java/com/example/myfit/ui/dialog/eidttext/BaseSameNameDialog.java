package com.example.myfit.ui.dialog.eidttext;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.ui.dialog.BaseDialog;

import org.jetbrains.annotations.NotNull;

public abstract class BaseSameNameDialog extends BaseDialog {
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
            task();
            getNavController().popBackStack(getDestinationId(), true);
        };
    }

    protected abstract void task();

    protected abstract int getDestinationId();
}
