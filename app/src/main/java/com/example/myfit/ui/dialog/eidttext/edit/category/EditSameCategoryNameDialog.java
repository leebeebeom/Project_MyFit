package com.example.myfit.ui.dialog.eidttext.edit.category;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.eidttext.edit.BaseEditSameNameDialog;
import com.example.myfit.ui.dialog.eidttext.edit.BaseEditViewModel;

public class EditSameCategoryNameDialog extends BaseEditSameNameDialog {
    @Override
    protected BaseEditViewModel getModel() {
        NavBackStackEntry graphBackStack = getGraphBackStack();
        return new ViewModelProvider(graphBackStack, HiltViewModelFactory.create(requireContext(), graphBackStack))
                .get(EditCategoryNameViewModel.class);
    }

    @Override
    protected int getDestinationId() {
        return R.id.editCategoryNameDialog;
    }

    @Override
    protected String getMessage() {
        return getString(R.string.dialog_message_same_category_name_edit);
    }

    @Override
    protected int getResId() {
        return R.id.editSameCategoryNameDialog;
    }
}
