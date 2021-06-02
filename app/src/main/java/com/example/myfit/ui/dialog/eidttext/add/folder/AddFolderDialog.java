package com.example.myfit.ui.dialog.eidttext.add.folder;

import android.view.View;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavDirections;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;
import com.example.myfit.ui.dialog.eidttext.add.BaseAddDialog;
import com.example.myfit.util.CommonUtil;

public class AddFolderDialog extends BaseAddDialog {
    private AddFolderViewModel mModel;

    @Override
    protected BaseEditTextViewModel.BaseAddViewModel getModel() {
        if (mModel == null) {
            NavBackStackEntry graphBackStack = mNavController.getBackStackEntry(R.id.nav_graph_add_folder);
            mModel = new ViewModelProvider(graphBackStack, HiltViewModelFactory.create(requireContext(), graphBackStack)).get(AddFolderViewModel.class);
        }
        return mModel;
    }

    @Override
    public String getHint() {
        return getString(R.string.dialog_hint_folder_name);
    }

    @Override
    public String getPlaceHolder() {
        return getString(R.string.dialog_place_holder_folder_name);
    }

    @Override
    protected void navigateSameNameDialog() {
        NavDirections action = AddFolderDialogDirections.toAddSameFolderNameDialog();
        CommonUtil.navigate(mNavController, R.id.addFolderDialog, action);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.all_create_folder);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            long parentId = AddFolderDialogArgs.fromBundle(getArguments()).getParentId();
            int parentIndex = AddFolderDialogArgs.fromBundle(getArguments()).getParentIndex();
            mModel.queryIsExistingName(parentId, parentIndex);
        };
    }

    @Override
    protected int getResId() {
        return R.id.addFolderDialog;
    }
}
