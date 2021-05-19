package com.example.myfit.ui.dialog.edit.folder;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.BaseDialogViewModel;
import com.example.myfit.ui.dialog.edit.BaseEditSameNameDialog;

public class EditSameFolderNameDialog extends BaseEditSameNameDialog {
    @Override
    protected NavBackStackEntry getBackstackEntry() {
        return getNavController().getBackStackEntry(R.id.editSameFolderNameDialog);
    }

    @Override
    protected BaseDialogViewModel getModel() {
        NavBackStackEntry graphBackStack = getGraphBackStack();
        return new ViewModelProvider(graphBackStack, HiltViewModelFactory.create(requireContext(), graphBackStack))
                .get(EditFolderNameViewModel.class);
    }

    @Override
    protected int getDestinationId() {
        return R.id.editFolderNameDialog;
    }

    @Override
    protected String getMessage() {
        return getString(R.string.dialog_message_same_folder_name_edit);
    }
}
