package com.example.myfit.ui.dialog.eidttext;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.ui.dialog.DialogBuilder;

public abstract class BaseSameNameDialog extends BaseDialog {
    @Override
    protected AlertDialog getAlertDialog(DialogBuilder dialogBuilder) {
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

    public abstract static class BaseAddSameNameDialog extends BaseSameNameDialog {
        @Override
        protected void task() {
            getModel().insert();
        }

        protected abstract BaseEditTextViewModel.BaseAddViewModel getModel();
    }

    public abstract static class BaseEditSameNameDialog extends BaseSameNameDialog {
        @Override
        public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setBackStackLive();
        }

        @Override
        protected void task() {
            getModel().update();
            setBackStackActionModeOff();
        }

        protected abstract BaseEditTextViewModel.BaseEditViewModel getModel();
    }
}
