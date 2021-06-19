package com.leebeebeom.closetnote.ui.dialog;

import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.leebeebeom.closetnote.R;

public class GoBackDialog extends BaseDialog {
    @Override
    protected AlertDialog getAlertDialog() {
        return mDialogBuilder.makeConfirmDialog(getString(R.string.dialog_message_go_back))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> mNavController.popBackStack(R.id.sizeFragment, true);
    }

    @Override
    protected int getResId() {
        return R.id.goBackDialog;
    }
}
