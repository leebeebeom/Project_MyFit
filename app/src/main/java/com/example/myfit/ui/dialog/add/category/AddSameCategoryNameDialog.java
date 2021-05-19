package com.example.myfit.ui.dialog.add.category;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.BaseDialogViewModel;
import com.example.myfit.ui.dialog.add.BaseAddSameNameDialog;

public class AddSameCategoryNameDialog extends BaseAddSameNameDialog {
    @Override
    protected BaseDialogViewModel getModel(NavBackStackEntry addGraphBackStackEntry) {
        return new ViewModelProvider(addGraphBackStackEntry, HiltViewModelFactory.create(requireContext(), addGraphBackStackEntry)).get(AddCategoryDialogViewModel.class);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.dialog_message_same_category_name_add);
    }

    @Override
    protected int getDestinationId() {
        return R.id.addCategoryDialog;
    }
}
