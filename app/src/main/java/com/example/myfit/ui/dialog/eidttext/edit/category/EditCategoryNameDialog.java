package com.example.myfit.ui.dialog.eidttext.edit.category;

import android.view.View;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavDirections;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;
import com.example.myfit.ui.dialog.eidttext.edit.BaseEditDialog;
import com.example.myfit.util.CommonUtil;

public class EditCategoryNameDialog extends BaseEditDialog {
    private EditCategoryNameViewModel mModel;

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
    protected int getResId() {
        return R.id.editCategoryNameDialog;
    }

    @Override
    protected BaseEditTextViewModel.BaseEditViewModel getModel() {
        if (mModel == null) {
            NavBackStackEntry graphBackStack = mNavController.getBackStackEntry(R.id.nav_graph_edit_category_name);
            mModel = new ViewModelProvider(graphBackStack, HiltViewModelFactory.create(requireContext(), graphBackStack))
                    .get(EditCategoryNameViewModel.class);
        }
        return mModel;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.dialog_title_edit_category_name);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            long id = EditCategoryNameDialogArgs.fromBundle(getArguments()).getId();
            int parentIndex = EditCategoryNameDialogArgs.fromBundle(getArguments()).getParentIndex();
            mModel.queryIsExistingName(getInputText(), id, parentIndex);
        };
    }

    @Override
    protected void navigateSameNameDialog() {
        NavDirections action = EditCategoryNameDialogDirections.toEditSameCategoryNameDialog();
        CommonUtil.navigate(mNavController, R.id.editCategoryNameDialog, action);
    }
}
