package com.leebeebeom.closetnote.ui.dialog.eidttext.edit.folder;

import android.view.View;

import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavDirections;

import com.leebeebeom.closetnote.NavGraphEditFolderNameArgs;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.ui.dialog.eidttext.BaseEditTextViewModel;
import com.leebeebeom.closetnote.ui.dialog.eidttext.edit.BaseEditDialog;
import com.leebeebeom.closetnote.util.CommonUtil;

public class EditFolderNameDialog extends BaseEditDialog {
    private EditFolderNameViewModel mModel;

    @Override
    protected String getInitialText() {
        return NavGraphEditFolderNameArgs.fromBundle(getArguments()).getName();
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
            long id = NavGraphEditFolderNameArgs.fromBundle(getArguments()).getId();
            long parentId = NavGraphEditFolderNameArgs.fromBundle(getArguments()).getParentId();
            mModel.queryIsExistingName(id, parentId);
        };
    }

    @Override
    protected void navigateSameName() {
        NavDirections action = EditFolderNameDialogDirections.toEditSameFolderName();
        CommonUtil.navigate(mNavController, R.id.editFolderNameDialog, action);
    }
}
