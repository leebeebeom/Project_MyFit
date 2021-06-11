package com.example.myfit.ui.dialog.delete;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.myfit.R;

public class DeleteCategoriesDialog extends BaseDeleteDialog {
    private long[] mSelectedItemIds;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedItemIds = DeleteCategoriesDialogArgs.fromBundle(getArguments()).getSelectedItemIds();
    }

    @Override
    protected int getResId() {
        return R.id.deleteCategoriesDialog;
    }

    @Override
    protected void task() {
        getMainGraphViewModel().categoriesDeleteOrRestore(mSelectedItemIds);
    }

    @Override
    protected int getSelectedItemsSize() {
        return mSelectedItemIds.length;
    }
}
