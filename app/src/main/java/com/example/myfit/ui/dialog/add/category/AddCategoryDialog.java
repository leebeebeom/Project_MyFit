package com.example.myfit.ui.dialog.add.category;

import android.view.View;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.add.BaseAddDialog;
import com.example.myfit.ui.dialog.add.BaseAddViewModel;
import com.example.myfit.util.CommonUtil;

public class AddCategoryDialog extends BaseAddDialog {
    @Override
    protected BaseAddViewModel getModel(NavBackStackEntry addGraphBackStackEntry) {
        return new ViewModelProvider(addGraphBackStackEntry, HiltViewModelFactory.create(requireContext(), addGraphBackStackEntry)).get(AddCategoryDialogViewModel.class);
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
    protected void navigateSameNameDialog(NavController navController) {
        NavDirections action = AddCategoryDialogDirections.toAddSameCategoryNameDialog();
        CommonUtil.navigate(navController, R.id.addCategoryDialog, action);
    }

    @Override
    protected String getDialogTitle() {
        return getString(R.string.all_add_category);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener(BaseAddViewModel model) {
        return v -> {
            byte parentIndex = (byte) AddCategoryDialogArgs.fromBundle(getArguments()).getParentIndex();
            model.queryIsExistingName(getInputText(), parentIndex);
        };
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return null;
    }
}
