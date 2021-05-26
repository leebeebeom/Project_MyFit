package com.example.myfit.ui.dialog;

import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.example.myfit.R;

public class GoBackDialog extends BaseDialog {
    @Override
    protected AlertDialog getAlertDialog(DialogBuilder dialogBuilder) {
        return this.dialogBuilder.makeConfirmDialog(getString(R.string.dialog_message_go_back))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> getNavController().popBackStack(R.id.sizeFragment, true);
    }

    @Override
    protected int getResId() {
        return R.id.goBackDialog;
    }
}
