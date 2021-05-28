package com.example.myfit.ui.dialog.delete;

import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.example.myfit.R;
import com.example.myfit.data.repository.SizeRepository;
import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.ui.dialog.DialogBuilder;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeleteSizeDialog extends BaseDialog {
    @Inject
    SizeRepository mSizeRepository;

    @Override
    protected AlertDialog getAlertDialog(DialogBuilder dialogBuilder) {
        return dialogBuilder.makeConfirmDialog(getString(R.string.dialog_message_delete))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            long id = DeleteSizeDialogArgs.fromBundle(getArguments()).getId();
            mSizeRepository.delete(id);
            getNavController().popBackStack(R.id.sizeFragment, true);
        };
    }

    @Override
    protected int getResId() {
        return R.id.deleteSizeDialog;
    }
}
