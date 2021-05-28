package com.example.myfit.ui.dialog.delete;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.myfit.R;
import com.example.myfit.data.repository.CategoryRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeleteCategoriesDialog extends BaseDeleteDialog {
    @Inject
    CategoryRepository mCategoryRepository;
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
        mCategoryRepository.deleteOrRestore(mSelectedItemIds, true);
    }

    @Override
    protected String getSelectedItemsSize() {
        return String.valueOf(mSelectedItemIds.length);
    }
}
