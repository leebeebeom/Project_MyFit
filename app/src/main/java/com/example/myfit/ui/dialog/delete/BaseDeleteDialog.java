package com.example.myfit.ui.dialog.delete;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.BaseDialog;

public abstract class BaseDeleteDialog extends BaseDialog {

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackStackLive();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        return mDialogBuilder.makeConfirmDialog(getSelectedItemsSize() + getString(R.string.dialog_message_selected_item_delete))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    protected abstract int getSelectedItemsSize();

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            task();
            setBackStackActionModeOff();
            dismiss();
        };
    }

    protected abstract void task();
}
