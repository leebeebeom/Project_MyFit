package com.example.myfit.ui.dialog.delete;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavBackStackEntry;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.BaseDialog;

import org.jetbrains.annotations.NotNull;

public abstract class BaseDeleteDialog extends BaseDialog {
    private NavBackStackEntry navBackStackEntry;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navBackStackEntry = getBackStackEntry();
        setBackStackLive(navBackStackEntry);
    }

    @NotNull
    protected abstract NavBackStackEntry getBackStackEntry();

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return getAlertDialog();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        return dialogBuilder.makeConfirmDialog(getSelectedItemsSize() + getString(R.string.dialog_message_selected_item_delete))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return getPositiveClickListener(navBackStackEntry);
    }

    protected abstract View.OnClickListener getPositiveClickListener(NavBackStackEntry navBackStackEntry);

    protected abstract String getSelectedItemsSize();
}
