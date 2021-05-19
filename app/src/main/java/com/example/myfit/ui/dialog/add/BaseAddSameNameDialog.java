package com.example.myfit.ui.dialog.add;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.ui.dialog.BaseDialogViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class BaseAddSameNameDialog extends BaseDialog {
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
            BaseDialogViewModel model = getModel();
            model.insert();
            getNavController().popBackStack(getDestinationId(), true);
        };
    }

    protected abstract BaseDialogViewModel getModel();

    protected abstract int getDestinationId();
}
