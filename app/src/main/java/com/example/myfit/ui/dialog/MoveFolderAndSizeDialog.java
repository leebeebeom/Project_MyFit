package com.example.myfit.ui.dialog;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.R;
import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.data.repository.SizeRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MoveFolderAndSizeDialog extends BaseDialog {
    @Inject
    FolderRepository folderRepository;
    @Inject
    SizeRepository sizeRepository;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackStackLive();
    }

    @Override
    protected AlertDialog getAlertDialog(DialogBuilder dialogBuilder) {
        String selectedItemsSize = String.valueOf(getSelectedFolderIds().length + getSelectedSizeIds().length);
        return this.dialogBuilder.makeConfirmDialog(selectedItemsSize + getString(R.string.dialog_message_item_move))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            if (getSelectedFolderIds().length != 0)
                folderRepository.move(getTargetId(), getSelectedFolderIds());
            if (getSelectedSizeIds().length != 0)
                sizeRepository.move(getTargetId(), getSelectedSizeIds());
            getBackStack().getSavedStateHandle().set(ACTION_MODE_OFF, null);
            getNavController().popBackStack(R.id.treeViewDialog, true);
        };
    }

    @Override
    protected int getResId() {
        return R.id.moveFolderAndSizeDialog;
    }

    private long[] getSelectedFolderIds() {
        return MoveFolderAndSizeDialogArgs.fromBundle(getArguments()).getFolderIds();
    }

    private long[] getSelectedSizeIds() {
        return MoveFolderAndSizeDialogArgs.fromBundle(getArguments()).getSizeIds();
    }

    private long getTargetId() {
        return MoveFolderAndSizeDialogArgs.fromBundle(getArguments()).getTargetId();
    }
}
