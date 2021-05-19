package com.example.myfit.ui.dialog.delete;

import android.view.View;

import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;

import com.example.myfit.R;
import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.data.repository.SizeRepository;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeleteFolderAndSizesDialog extends BaseDeleteDialog {
    @Inject
    FolderRepository folderRepository;
    @Inject
    SizeRepository sizeRepository;

    @Override
    protected @NotNull NavBackStackEntry getBackStackEntry(NavController navController) {
        return navController.getBackStackEntry(R.id.deleteFolderAndSizesDialog);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener(NavBackStackEntry navBackStackEntry) {
        return v -> {
            folderRepository.deleteOrRestore(getSelectedFolderIds(), true);
            sizeRepository.deleteOrRestore(getSelectedSizeIds(), true);
            navBackStackEntry.getSavedStateHandle().set(ACTION_MODE_OFF, null);
            dismiss();
        };
    }

    @Override
    protected String getSelectedItemsSize() {
        return String.valueOf(getSelectedFolderIds().length + getSelectedSizeIds().length);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return null;
    }

    private long[] getSelectedFolderIds() {
        return DeleteFolderAndSizesDialogArgs.fromBundle(getArguments()).getSelectedFolderIds();
    }

    protected long[] getSelectedSizeIds() {
        return DeleteFolderAndSizesDialogArgs.fromBundle(getArguments()).getSelectedSizeIds();
    }

}
