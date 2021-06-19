package com.leebeebeom.closetnote.ui.dialog;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.leebeebeom.closetnote.R;

public class DeleteImageDialog extends BaseDialog {
    public static final String DELETE_IMAGE = "image delete";

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackStackLive();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        return mDialogBuilder.makeConfirmDialog(getString(R.string.dialog_message_image_clear))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            getBackStack().getSavedStateHandle().set(DELETE_IMAGE, null);
            dismiss();
        };
    }

    @Override
    protected int getResId() {
        return R.id.deleteImageDialog;
    }
}
