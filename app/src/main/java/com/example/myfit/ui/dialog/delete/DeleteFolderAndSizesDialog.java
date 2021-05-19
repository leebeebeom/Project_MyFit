package com.example.myfit.ui.dialog.delete;

import android.view.View;

import androidx.navigation.NavBackStackEntry;

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
    private long[] selectedFolderIds;
    private long[] selectedSizeIds;

    @Override
    protected @NotNull NavBackStackEntry getBackStackEntry() {
        return getNavController().getBackStackEntry(R.id.deleteFolderAndSizesDialog);
    }

    @Override
    protected View.OnClickListener getPositiveClickListener(NavBackStackEntry navBackStackEntry) {
        return v -> {
            folderRepository.deleteOrRestore(getSelectedFolderIds(), true);
            sizeRepository.deleteOrRestore(getSelectedSizeIds(), true);
            actionModeOff(navBackStackEntry);
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
        if (selectedFolderIds == null)
            selectedFolderIds = DeleteFolderAndSizesDialogArgs.fromBundle(getArguments()).getSelectedFolderIds();
        return selectedFolderIds;
    }

    protected long[] getSelectedSizeIds() {
        if (selectedFolderIds == null)
            selectedSizeIds = DeleteFolderAndSizesDialogArgs.fromBundle(getArguments()).getSelectedSizeIds();
        return selectedSizeIds;
    }
}
