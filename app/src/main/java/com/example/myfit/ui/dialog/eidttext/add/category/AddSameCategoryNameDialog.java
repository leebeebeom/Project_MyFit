package com.example.myfit.ui.dialog.eidttext.add.category;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;
import com.example.myfit.ui.dialog.eidttext.BaseSameNameDialog;

public class AddSameCategoryNameDialog extends BaseSameNameDialog.BaseAddSameNameDialog {
    @Override
    protected BaseEditTextViewModel.BaseAddViewModel getModel() {
        NavBackStackEntry graphBackStack = mNavController.getBackStackEntry(R.id.nav_graph_add_category);
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
    protected int getResId() {
        return R.id.addSameCategoryNameDialog;
    }
}
