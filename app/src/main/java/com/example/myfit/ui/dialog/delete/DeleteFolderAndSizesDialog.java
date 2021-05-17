package com.example.myfit.ui.dialog.delete;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.R;
import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.data.repository.SizeRepository;
import com.example.myfit.ui.dialog.BaseDialog;

import javax.inject.Inject;

public class DeleteFolderAndSizesDialog extends BaseDialog {
    @Inject
    FolderRepository folderRepository;
    @Inject
    SizeRepository sizeRepository;
    private long[] selectedFolderIds;
    private long[] selectedSizeIds;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedFolderIds = DeleteFolderAndSizesDialogArgs.fromBundle(getArguments()).getSelectedFolderIds();
        selectedSizeIds = DeleteFolderAndSizesDialogArgs.fromBundle(getArguments()).getSelectedSizeIds();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        String selectedItemsSize = String.valueOf(selectedFolderIds.length + selectedSizeIds.length);
        return dialogBuilder.makeConfirmDialog(selectedItemsSize + getString(R.string.dialog_message_selected_item_delete))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            folderRepository.deleteOrRestore(selectedFolderIds, true);
            sizeRepository.deleteOrRestore(selectedSizeIds, true);
            dismiss();
        };
    }
}
