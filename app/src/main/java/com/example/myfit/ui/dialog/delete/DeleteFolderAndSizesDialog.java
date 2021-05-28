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
    FolderRepository mFolderRepository;
    @Inject
    SizeRepository mSizeRepository;
    private long[] mSelectedFolderIds;
    private long[] mSelectedSizeIds;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedFolderIds = DeleteFolderAndSizesDialogArgs.fromBundle(getArguments()).getSelectedFolderIds();
        mSelectedSizeIds = DeleteFolderAndSizesDialogArgs.fromBundle(getArguments()).getSelectedSizeIds();
    }

    @Override
    protected int getResId() {
        return R.id.deleteFolderAndSizesDialog;
    }

    @Override
    protected void task() {
        mFolderRepository.deleteOrRestore(mSelectedFolderIds, true);
        mSizeRepository.deleteOrRestore(mSelectedSizeIds, true);
    }

    @Override
    protected String getSelectedItemsSize() {
        return String.valueOf(mSelectedFolderIds.length + mSelectedSizeIds.length);
    }
}
