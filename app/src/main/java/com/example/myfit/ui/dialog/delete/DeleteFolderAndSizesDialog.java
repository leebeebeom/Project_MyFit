package com.example.myfit.ui.dialog.delete;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.myfit.R;
import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.data.repository.SizeRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeleteFolderAndSizesDialog extends BaseDeleteDialog {
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
    protected int getResId() {
        return R.id.deleteFolderAndSizesDialog;
    }

    @Override
    protected void task() {
        folderRepository.deleteOrRestore(selectedFolderIds, true);
        sizeRepository.deleteOrRestore(selectedSizeIds, true);
    }

    @Override
    protected String getSelectedItemsSize() {
        return String.valueOf(selectedFolderIds.length + selectedSizeIds.length);
    }
}
