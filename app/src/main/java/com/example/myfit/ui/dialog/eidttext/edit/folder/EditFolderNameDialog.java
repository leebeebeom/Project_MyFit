package com.example.myfit.ui.dialog.eidttext.edit.folder;

import android.view.View;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavDirections;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;
import com.example.myfit.ui.dialog.eidttext.edit.BaseEditDialog;
import com.example.myfit.util.CommonUtil;

public class EditFolderNameDialog extends BaseEditDialog {
    private EditFolderNameViewModel mModel;

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
    protected int getResId() {
        return R.id.editFolderNameDialog;
    }

    @Override
    protected BaseEditTextViewModel.BaseEditViewModel getModel() {
        if (mModel == null) {
            NavBackStackEntry graphBackStack = mNavController.getBackStackEntry(R.id.nav_graph_edit_folder_name);
            mModel = new ViewModelProvider(graphBackStack, HiltViewModelFactory.create(requireContext(), graphBackStack)).get(EditFolderNameViewModel.class);
        }
        return mModel;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.dialog_title_edit_folder_name);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            long id = EditFolderNameDialogArgs.fromBundle(getArguments()).getId();
            long parentId = EditFolderNameDialogArgs.fromBundle(getArguments()).getParentId();
            mModel.queryIsExistingName(getInputText(), id, parentId);
        };
    }

    @Override
    protected void navigateSameNameDialog() {
        NavDirections action = EditFolderNameDialogDirections.toEditSameFolderNameDialog();
        CommonUtil.navigate(mNavController, R.id.editFolderNameDialog, action);
    }
}
