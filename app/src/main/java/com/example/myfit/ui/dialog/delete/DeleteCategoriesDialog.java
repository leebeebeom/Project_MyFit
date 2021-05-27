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
    CategoryRepository categoryRepository;
    private long[] selectedItemIds;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedItemIds = DeleteCategoriesDialogArgs.fromBundle(getArguments()).getSelectedItemIds();
    }

    @Override
    protected int getResId() {
        return R.id.deleteCategoriesDialog;
    }

    @Override
    protected void task() {
        categoryRepository.deleteOrRestore(selectedItemIds, true);
    }

    @Override
    protected String getSelectedItemsSize() {
        return String.valueOf(selectedItemIds.length);
    }
}
