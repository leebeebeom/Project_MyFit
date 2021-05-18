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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddCategoryDialog extends BaseAddDialog {
    @Override
    protected BaseAddViewModel getModel(NavBackStackEntry navBackStackEntry) {
        return new ViewModelProvider(navBackStackEntry, HiltViewModelFactory.create(requireContext(), navBackStackEntry)).get(AddCategoryDialogViewModel.class);
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

    @NotNull
    @Contract(pure = true)
    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            byte parentIndex = (byte) AddCategoryDialogArgs.fromBundle(getArguments()).getParentIndex();
            model.queryIsExistingName(getInputText(), parentIndex);
        };
    }

    @NotNull
    private String getInputText() {
        return String.valueOf(binding.et.getText());
    }
}
