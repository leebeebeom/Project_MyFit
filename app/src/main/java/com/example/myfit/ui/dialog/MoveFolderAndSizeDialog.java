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
    private long[] selectedFolderIds, selectedSizeIds;
    private long targetId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedFolderIds = MoveFolderAndSizeDialogArgs.fromBundle(getArguments()).getSelectedFolderIds();
        selectedSizeIds = MoveFolderAndSizeDialogArgs.fromBundle(getArguments()).getSelectedSizeIds();
        targetId = MoveFolderAndSizeDialogArgs.fromBundle(getArguments()).getTargetId();
        setBackStackLive();
    }

    @Override
    protected AlertDialog getAlertDialog(DialogBuilder dialogBuilder) {
        String selectedItemsSize = String.valueOf(selectedFolderIds.length + selectedSizeIds.length);
        return this.dialogBuilder.makeConfirmDialog(selectedItemsSize + getString(R.string.dialog_message_item_move))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            if (selectedFolderIds.length != 0)
                folderRepository.move(targetId, selectedFolderIds);
            if (selectedSizeIds.length != 0)
                sizeRepository.move(targetId, selectedSizeIds);
            getBackStack().getSavedStateHandle().set(ACTION_MODE_OFF, null);
            getNavController().popBackStack(R.id.treeViewDialog, true);
        };
    }

    @Override
    protected int getResId() {
        return R.id.moveFolderAndSizeDialog;
    }
}
