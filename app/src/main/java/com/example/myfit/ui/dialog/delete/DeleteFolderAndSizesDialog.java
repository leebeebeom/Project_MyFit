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
    private NavBackStackEntry backStackEntry;

    @Override
    protected @NotNull NavBackStackEntry getBackStackEntry() {
        if (backStackEntry == null)
            backStackEntry = getNavController().getBackStackEntry(R.id.deleteFolderAndSizesDialog);
        return backStackEntry;
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            folderRepository.deleteOrRestore(getSelectedFolderIds(), true);
            sizeRepository.deleteOrRestore(getSelectedSizeIds(), true);
            actionModeOff(backStackEntry);
            dismiss();
        };
    }

    @Override
    protected String getSelectedItemsSize() {
        return String.valueOf(getSelectedFolderIds().length + getSelectedSizeIds().length);
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
