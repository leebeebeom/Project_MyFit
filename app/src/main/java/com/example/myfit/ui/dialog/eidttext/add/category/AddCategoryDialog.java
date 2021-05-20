package com.example.myfit.ui.dialog.eidttext.add.category;

import android.view.View;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavDirections;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.eidttext.add.BaseAddDialog;
import com.example.myfit.ui.dialog.eidttext.add.BaseAddViewModel;
import com.example.myfit.util.CommonUtil;

public class AddCategoryDialog extends BaseAddDialog {
    private AddCategoryViewModel model;

    @Override
    protected BaseAddViewModel getModel() {
        if (model == null) {
            NavBackStackEntry graphBackStack = getGraphBackStack();
            model = new ViewModelProvider(graphBackStack, HiltViewModelFactory.create(requireContext(), graphBackStack)).get(AddCategoryViewModel.class);
        }
        return model;
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
    protected void navigateSameNameDialog() {
        NavDirections action = AddCategoryDialogDirections.toAddSameCategoryNameDialog();
        CommonUtil.navigate(getNavController(), R.id.addCategoryDialog, action);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.all_add_category);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            byte parentIndex = (byte) AddCategoryDialogArgs.fromBundle(getArguments()).getParentIndex();
            model.setStateHandle(getInputText(), parentIndex);
        };
    }

    @Override
    protected NavBackStackEntry getBackStack() {
        return getNavController().getBackStackEntry(R.id.addCategoryDialog);
    }
}