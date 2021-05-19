package com.example.myfit.ui.dialog.add.folder;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.BaseDialogViewModel;
import com.example.myfit.ui.dialog.add.BaseAddSameNameDialog;

public class AddSameFolderNameDialog extends BaseAddSameNameDialog {
    @Override
    protected BaseDialogViewModel getModel() {
        NavBackStackEntry graphBackStack = getGraphBackStack();
        return new ViewModelProvider(graphBackStack, HiltViewModelFactory.create(requireContext(), graphBackStack)).get(AddFolderDialogViewModel.class);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.dialog_message_same_folder_name_add);
    }

    @Override
    protected int getDestinationId() {
        return R.id.addFolderDialog;
    }
}
