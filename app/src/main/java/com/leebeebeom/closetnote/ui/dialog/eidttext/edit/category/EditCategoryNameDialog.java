package com.leebeebeom.closetnote.ui.dialog.eidttext.edit.category;

import android.view.View;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavDirections;

import com.leebeebeom.closetnote.NavGraphEditCategoryNameArgs;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.ui.dialog.eidttext.BaseEditTextViewModel;
import com.leebeebeom.closetnote.ui.dialog.eidttext.edit.BaseEditDialog;
import com.leebeebeom.closetnote.util.CommonUtil;

public class EditCategoryNameDialog extends BaseEditDialog {
    private EditCategoryNameViewModel mModel;

    @Override
    protected String getName() {
        return NavGraphEditCategoryNameArgs.fromBundle(getArguments()).getName();
    }

    @Override
    public String getHint() {
        return getString(R.string.dialog_hint_category_name);
    }

    @Override
    public String getPlaceHolder() {
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
            long id = NavGraphEditCategoryNameArgs.fromBundle(getArguments()).getId();
            int parentIndex = NavGraphEditCategoryNameArgs.fromBundle(getArguments()).getParentIndex();
            mModel.queryIsExistingName(id, parentIndex);
        };
    }

    @Override
    protected void navigateSameName() {
        NavDirections action = EditCategoryNameDialogDirections.toEditSameCategoryName();
        CommonUtil.navigate(mNavController, R.id.editCategoryNameDialog, action);
    }
}
