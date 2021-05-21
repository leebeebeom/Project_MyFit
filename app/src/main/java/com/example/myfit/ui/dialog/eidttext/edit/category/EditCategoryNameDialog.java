package com.example.myfit.ui.dialog.eidttext.edit.category;

import android.view.View;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavDirections;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.eidttext.edit.BaseEditDialog;
import com.example.myfit.ui.dialog.eidttext.edit.BaseEditViewModel;
import com.example.myfit.util.CommonUtil;

public class EditCategoryNameDialog extends BaseEditDialog {
    private EditCategoryNameViewModel model;

    @Override
    protected String getName() {
        return EditCategoryNameDialogArgs.fromBundle(getArguments()).getName();
    }

    @Override
    protected String getHint() {
        return getString(R.string.dialog_hint_category_name);
    }

    @Override
    protected String getPlaceHolder() {
        return getString(R.string.dialog_place_holder_category_name);
    }

    @Override
    protected NavBackStackEntry getBackStack() {
        return getNavController().getBackStackEntry(R.id.editCategoryNameDialog);
    }

    @Override
    protected BaseEditViewModel getModel() {
        if (model == null) {
            NavBackStackEntry graphBackStack = getGraphBackStack();
            model = new ViewModelProvider(graphBackStack, HiltViewModelFactory.create(requireContext(), graphBackStack))
                    .get(EditCategoryNameViewModel.class);
        }
        return model;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.all_add_category);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            long id = EditCategoryNameDialogArgs.fromBundle(getArguments()).getId();
            int parentIndex = EditCategoryNameDialogArgs.fromBundle(getArguments()).getParentIndex();
            model.setStateHandle(id, getInputText(), parentIndex);
        };
    }

    @Override
    protected void navigateSameNameDialog() {
        NavDirections action = EditCategoryNameDialogDirections.toEditSameCategoryNameDialog();
        CommonUtil.navigate(getNavController(), R.id.editCategoryNameDialog, action);
    }
}
