package com.example.myfit.ui.dialog.delete;

import com.example.myfit.R;
import com.example.myfit.data.repository.CategoryRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeleteCategoriesDialog extends BaseDeleteDialog {
    @Inject
    CategoryRepository categoryRepository;

    @Override
    protected int getResId() {
        return R.id.deleteCategoriesDialog;
    }

    @Override
    protected void task() {
        categoryRepository.deleteOrRestore(getSelectedItemIds(), true);
    }

    @Override
    protected String getSelectedItemsSize() {
        return String.valueOf(getSelectedItemIds().length);
    }

    private long[] getSelectedItemIds() {
        return DeleteCategoriesDialogArgs.fromBundle(getArguments()).getSelectedItemIds();
    }
}
