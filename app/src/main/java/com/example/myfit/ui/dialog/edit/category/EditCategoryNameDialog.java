package com.example.myfit.ui.dialog.edit.category;

import android.view.View;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.edit.BaseEditDialog;
import com.example.myfit.ui.dialog.edit.BaseEditViewModel;
import com.example.myfit.util.CommonUtil;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditCategoryNameDialog extends BaseEditDialog {
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
    protected NavBackStackEntry getBackStackEntry(@NotNull NavController navController) {
        return navController.getBackStackEntry(R.id.editCategoryNameDialog);
    }

    @Override
    protected BaseEditViewModel getModel(NavBackStackEntry editGraphBackStackEntry) {
        return new ViewModelProvider(editGraphBackStackEntry, HiltViewModelFactory.create(requireContext(), editGraphBackStackEntry))
                .get(EditCategoryNameViewModel.class);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.all_add_category);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener(BaseEditViewModel model, String inputText) {
        return v -> {
            long id = EditCategoryNameDialogArgs.fromBundle(getArguments()).getId();
            byte parentIndex = (byte) EditCategoryNameDialogArgs.fromBundle(getArguments()).getParentIndex();
            model.queryIsExistingName(id, getInputText(), parentIndex);
        };
    }

    @Override
    protected void navigateSameNameDialog(NavController navController) {
        NavDirections action = EditCategoryNameDialogDirections.toEditSameCategoryNameDialog();
        CommonUtil.navigate(navController, R.id.editCategoryNameDialog, action);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return null;
    }
}
