package com.leebeebeom.closetnote.ui.dialog.delete;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.leebeebeom.closetnote.R;

public class DeleteFolderAndSizesDialog extends BaseDeleteDialog {
    private long[] mSelectedFolderIds, mSelectedSizeIds;

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
        if (mSelectedFolderIds.length != 0)
            getMainGraphViewModel().foldersDeleteOrRestore(mSelectedFolderIds);
        if (mSelectedSizeIds.length != 0)
            getMainGraphViewModel().sizesDeleteOrRestore(mSelectedSizeIds);
    }

    @Override
    protected int getSelectedItemsSize() {
        return mSelectedFolderIds.length + mSelectedSizeIds.length;
    }
}
