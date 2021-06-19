package com.leebeebeom.closetnote.ui.dialog.eidttext.add.category;

import android.view.View;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavDirections;

import com.leebeebeom.closetnote.NavGraphAddCategoryArgs;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.ui.dialog.eidttext.BaseEditTextViewModel;
import com.leebeebeom.closetnote.ui.dialog.eidttext.add.BaseAddDialog;
import com.leebeebeom.closetnote.util.CommonUtil;

public class AddCategoryDialog extends BaseAddDialog {
    private AddCategoryViewModel mModel;

    @Override
    protected BaseEditTextViewModel.BaseAddViewModel getModel() {
        if (mModel == null) {
            NavBackStackEntry graphBackStack = mNavController.getBackStackEntry(R.id.nav_graph_add_category);
            mModel = new ViewModelProvider(graphBackStack, HiltViewModelFactory.create(requireContext(), graphBackStack)).get(AddCategoryViewModel.class);
        }
        return mModel;
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
    protected void navigateSameName() {
        NavDirections action = AddCategoryDialogDirections.toAddSameCategoryName();
        CommonUtil.navigate(mNavController, R.id.addCategoryDialog, action);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.all_add_category);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            int parentIndex = NavGraphAddCategoryArgs.fromBundle(getArguments()).getParentIndex();
            mModel.queryIsExistingName(parentIndex);
        };
    }

    @Override
    protected int getResId() {
        return R.id.addCategoryDialog;
    }
}
