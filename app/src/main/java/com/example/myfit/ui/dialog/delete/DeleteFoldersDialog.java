package com.example.myfit.ui.dialog.delete;

import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;

import com.example.myfit.R;
import com.example.myfit.data.repository.BaseRepository;
import com.example.myfit.data.repository.FolderRepository;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeleteFoldersDialog extends BaseDeleteDialog {
    @Inject
    FolderRepository folderRepository;

    @NotNull
    @Override
    protected NavBackStackEntry getBackStackEntry(@NotNull NavController navController) {
        return navController.getBackStackEntry(R.id.deleteFoldersDialog);
    }

    @Override
    protected String getMessage() {
        return getString(R.string.dialog_message_selected_item_delete);
    }

    @Override
    protected long[] getSelectedItemIds() {
        return DeleteFoldersDialogArgs.fromBundle(getArguments()).getSelectedItemIds();
    }

    @Override
    protected BaseRepository getRepository() {
        return folderRepository;
    }
}
