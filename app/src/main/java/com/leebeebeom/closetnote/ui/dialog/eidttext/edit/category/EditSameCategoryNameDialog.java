package com.leebeebeom.closetnote.ui.dialog.eidttext.edit.category;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;

import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.ui.dialog.eidttext.BaseEditTextViewModel;
import com.leebeebeom.closetnote.ui.dialog.eidttext.BaseSameNameDialog;

public class EditSameCategoryNameDialog extends BaseSameNameDialog.BaseEditSameNameDialog {
    @Override
    protected BaseEditTextViewModel.BaseEditViewModel getModel() {
        NavBackStackEntry graphBackStack = mNavController.getBackStackEntry(R.id.nav_graph_edit_category_name);
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
