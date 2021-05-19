package com.example.myfit.ui.dialog.delete;

import android.view.View;

import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;

import com.example.myfit.R;
import com.example.myfit.data.repository.CategoryRepository;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeleteCategoriesDialog extends BaseDeleteDialog {
    @Inject
    CategoryRepository categoryRepository;

    @NotNull
    @Override
    protected NavBackStackEntry getBackStackEntry(@NotNull NavController navController) {
        return navController.getBackStackEntry(R.id.deleteCategoriesDialog);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener(NavBackStackEntry navBackStackEntry) {
        return v -> {
            categoryRepository.deleteOrRestore(getSelectedItemIds(), true);
            navBackStackEntry.getSavedStateHandle().set(ACTION_MODE_OFF, null);
            dismiss();
        };
    }

    @Override
    protected String getSelectedItemsSize() {
        return String.valueOf(getSelectedItemIds().length);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return null;
    }

    private long[] getSelectedItemIds() {
        return DeleteCategoriesDialogArgs.fromBundle(getArguments()).getSelectedItemIds();
    }
}
