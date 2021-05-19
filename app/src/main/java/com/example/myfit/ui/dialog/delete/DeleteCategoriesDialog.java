package com.example.myfit.ui.dialog.delete;

import android.view.View;

import androidx.navigation.NavBackStackEntry;

import com.example.myfit.R;
import com.example.myfit.data.repository.CategoryRepository;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeleteCategoriesDialog extends BaseDeleteDialog {
    @Inject
    CategoryRepository categoryRepository;
    private long[] selectedCategoryIds;
    private NavBackStackEntry backStackEntry;

    @NotNull
    @Override
    protected NavBackStackEntry getBackStackEntry() {
        if (backStackEntry == null)
            backStackEntry = getNavController().getBackStackEntry(R.id.deleteCategoriesDialog);
        return backStackEntry;
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            categoryRepository.deleteOrRestore(getSelectedItemIds(), true);
            actionModeOff(backStackEntry);
            dismiss();
        };
    }

    @Override
    protected String getSelectedItemsSize() {
        return String.valueOf(getSelectedItemIds().length);
    }

    private long[] getSelectedItemIds() {
        if (selectedCategoryIds == null)
            selectedCategoryIds = DeleteCategoriesDialogArgs.fromBundle(getArguments()).getSelectedItemIds();
        return selectedCategoryIds;
    }
}
