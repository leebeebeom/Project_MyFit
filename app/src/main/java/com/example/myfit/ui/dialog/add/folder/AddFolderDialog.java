package com.example.myfit.ui.dialog.add.folder;

import android.view.View;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavDirections;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.add.BaseAddDialog;
import com.example.myfit.util.CommonUtil;

public class AddFolderDialog extends BaseAddDialog {
    @Override
    protected BaseAddViewModel getModel() {
        NavBackStackEntry graphBackStack = getGraphBackStack();
        return new ViewModelProvider(graphBackStack, HiltViewModelFactory.create(requireContext(), graphBackStack)).get(AddFolderDialogViewModel.class);
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
    protected String getDialogTitle() {
        return getString(R.string.all_create_folder);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener(BaseAddViewModel model) {
        return v -> {
            long parentId = AddFolderDialogArgs.fromBundle(getArguments()).getFolderId();
            byte parentIndex = (byte) AddFolderDialogArgs.fromBundle(getArguments()).getParentIndex();
            model.queryIsExistingName(getInputText(), parentId, parentIndex);
        };
    }
}
