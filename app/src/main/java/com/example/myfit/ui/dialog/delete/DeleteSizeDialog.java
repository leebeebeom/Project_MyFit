package com.example.myfit.ui.dialog.delete;

import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.BaseDialog;

public class DeleteSizeDialog extends BaseDialog {
    @Override
    protected AlertDialog getAlertDialog() {
        return mDialogBuilder.makeConfirmDialog(getString(R.string.dialog_message_delete))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            long id = DeleteSizeDialogArgs.fromBundle(getArguments()).getId();
            getMainGraphViewModel().sizeDelete(id);
            mNavController.popBackStack(R.id.sizeFragment, true);
        };
    }

    @Override
    protected int getResId() {
        return R.id.deleteSizeDialog;
    }
}
