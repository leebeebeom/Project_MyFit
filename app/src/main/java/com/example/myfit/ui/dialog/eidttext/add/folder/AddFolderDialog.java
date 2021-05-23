package com.example.myfit.ui.dialog.eidttext.add.folder;

import android.view.View;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavDirections;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.eidttext.add.BaseAddDialog;
import com.example.myfit.ui.dialog.eidttext.add.BaseAddViewModel;
import com.example.myfit.util.CommonUtil;

public class AddFolderDialog extends BaseAddDialog {
    private AddFolderViewModel model;

    @Override
    protected BaseAddViewModel getModel() {
        if (model == null) {
            NavBackStackEntry graphBackStack = getGraphBackStack();
            model = new ViewModelProvider(graphBackStack, HiltViewModelFactory.create(requireContext(), graphBackStack)).get(AddFolderViewModel.class);
        }
        return model;
    }

    @Override
    protected String getHint() {
        return getString(R.string.dialog_hint_folder_name);
    }

    @Override
    protected String getPlaceHolder() {
        return getString(R.string.dialog_place_holder_folder_name);
    }

    @Override
    protected void navigateSameNameDialog() {
        NavDirections action = AddFolderDialogDirections.toAddSameFolderNameDialog();
        CommonUtil.navigate(getNavController(), R.id.addFolderDialog, action);
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
            model.setStateHandle(getInputText(), parentId, parentIndex);
        };
    }

    @Override
    protected int getResId() {
        return R.id.addFolderDialog;
    }
}
