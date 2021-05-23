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
    private long[] folderIds;
    private long[] sizeIds;
    private long targetId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        folderIds = MoveFolderAndSizeDialogArgs.fromBundle(getArguments()).getFolderIds();
        sizeIds = MoveFolderAndSizeDialogArgs.fromBundle(getArguments()).getSizeIds();
        targetId = MoveFolderAndSizeDialogArgs.fromBundle(getArguments()).getTargetId();
        setBackStackLive();
    }
    @Override
    protected AlertDialog getAlertDialog() {
        String selectedItemsSize = String.valueOf(folderIds.length + sizeIds.length);
        return dialogBuilder.makeConfirmDialog(selectedItemsSize + getString(R.string.dialog_message_item_move))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            if (folderIds.length != 0) folderRepository.move(targetId, folderIds);
            if (sizeIds.length != 0) sizeRepository.move(targetId, sizeIds);
            getBackStack().getSavedStateHandle().set(ACTION_MODE_OFF, null);
            getNavController().popBackStack(R.id.treeViewDialog, true);
        };
    }

    @Override
    protected int getResId() {
        return R.id.moveFolderAndSizeDialog;
    }
}
