package com.example.myfit.ui.dialog.edit.category;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.edit.BaseEditSameNameDialog;
import com.example.myfit.ui.dialog.edit.BaseEditViewModel;

import org.jetbrains.annotations.NotNull;

public class EditSameCategoryNameDialog extends BaseEditSameNameDialog {

    @Override
    protected NavBackStackEntry getBackstackEntry(@NotNull NavController navController) {
        return navController.getBackStackEntry(R.id.editSameCategoryNameDialog);
    }

    @Override
    protected BaseEditViewModel getModel(NavBackStackEntry editGraphBackStackEntry) {
        return new ViewModelProvider(editGraphBackStackEntry, HiltViewModelFactory.create(requireContext(), editGraphBackStackEntry))
                .get(EditCategoryNameViewModel.class);
    }

    @Override
    protected String getMessage() {
        return getString(R.string.dialog_message_same_category_name_edit);
    }
}
