package com.example.myfit.ui.dialog.edit.folder;

import android.view.View;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.edit.BaseEditDialog;
import com.example.myfit.ui.dialog.edit.BaseEditViewModel;
import com.example.myfit.util.CommonUtil;

import org.jetbrains.annotations.NotNull;

public class EditFolderNameDialog extends BaseEditDialog {
    @Override
    protected String getName() {
        return EditFolderNameDialogArgs.fromBundle(getArguments()).getName();
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
    protected NavBackStackEntry getBackStackEntry(@NotNull NavController navController) {
        return navController.getBackStackEntry(R.id.editFolderNameDialog);
    }

    @Override
    protected BaseEditViewModel getModel(NavBackStackEntry editGraphBackStackEntry) {
        return new ViewModelProvider(editGraphBackStackEntry, HiltViewModelFactory.create(requireContext(), editGraphBackStackEntry))
                .get(EditFolderNameViewModel.class);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.all_create_folder);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener(BaseEditViewModel model, String inputText) {
        return v -> {
            long id = EditFolderNameDialogArgs.fromBundle(getArguments()).getId();
            long parentId = EditFolderNameDialogArgs.fromBundle(getArguments()).getParentId();
            model.queryIsExistingName(id, getInputText(), parentId);
        };
    }

    @Override
    protected void navigateSameNameDialog(NavController navController) {
        NavDirections action = EditFolderNameDialogDirections.toEditSameFolderNameDialog();
        CommonUtil.navigate(navController, R.id.editFolderNameDialog, action);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return null;
    }
}
