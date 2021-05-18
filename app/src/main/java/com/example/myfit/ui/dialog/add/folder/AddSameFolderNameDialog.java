package com.example.myfit.ui.dialog.add.folder;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.add.BaseAddViewModel;
import com.example.myfit.ui.dialog.add.BaseSameNameDialog;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddSameFolderNameDialog extends BaseSameNameDialog {
    @Override
    protected BaseAddViewModel getModel(NavBackStackEntry navBackStackEntry) {
        return new ViewModelProvider(navBackStackEntry, HiltViewModelFactory.create(requireContext(), navBackStackEntry)).get(AddFolderDialogViewModel.class);
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
