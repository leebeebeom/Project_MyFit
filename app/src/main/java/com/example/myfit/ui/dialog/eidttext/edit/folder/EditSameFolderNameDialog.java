package com.example.myfit.ui.dialog.eidttext.edit.folder;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;
import com.example.myfit.ui.dialog.eidttext.BaseSameNameDialog;

public class EditSameFolderNameDialog extends BaseSameNameDialog.BaseEditSameNameDialog {
    @Override
    protected BaseEditTextViewModel.BaseEditViewModel getModel() {
        NavBackStackEntry graphBackStack = mNavController.getBackStackEntry(R.id.nav_graph_edit_folder_name);
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

    @Override
    protected int getResId() {
        return R.id.editSameFolderNameDialog;
    }
}
