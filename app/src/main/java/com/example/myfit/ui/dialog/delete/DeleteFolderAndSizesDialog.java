package com.example.myfit.ui.dialog.delete;

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

    @Override
    protected int getResId() {
        return R.id.deleteFolderAndSizesDialog;
    }

    @Override
    protected void task() {
        folderRepository.deleteOrRestore(getSelectedFolderIds(), true);
        sizeRepository.deleteOrRestore(getSelectedSizeIds(), true);
    }

    @Override
    protected String getSelectedItemsSize() {
        return String.valueOf(getSelectedFolderIds().length + getSelectedSizeIds().length);
    }

    private long[] getSelectedFolderIds() {
        return DeleteFolderAndSizesDialogArgs.fromBundle(getArguments()).getSelectedFolderIds();
    }

    protected long[] getSelectedSizeIds() {
        return DeleteFolderAndSizesDialogArgs.fromBundle(getArguments()).getSelectedSizeIds();
    }
}
