package com.example.myfit.ui.dialog.eidttext.add.category;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.eidttext.add.BaseAddSameNameDialog;
import com.example.myfit.ui.dialog.eidttext.add.BaseAddViewModel;

public class AddSameCategoryNameDialog extends BaseAddSameNameDialog {
    @Override
    protected BaseAddViewModel getModel() {
        NavBackStackEntry graphBackStack = getGraphBackStack();
        return new ViewModelProvider(graphBackStack, HiltViewModelFactory.create(requireContext(), graphBackStack)).get(AddCategoryViewModel.class);
    }

    @Override
    protected String getMessage() {
        return getString(R.string.dialog_message_same_category_name_add);
    }

    @Override
    protected int getDestinationId() {
        return R.id.addCategoryDialog;
    }

    @Override
    protected NavBackStackEntry getBackStack() {
        return getNavController().getBackStackEntry(R.id.addSameCategoryNameDialog);
    }
}
