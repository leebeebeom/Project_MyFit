package com.example.myfit.ui.dialog.eidttext;

import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.example.myfit.ui.dialog.BaseDialog;

public abstract class BaseSameNameDialog extends BaseDialog {
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
