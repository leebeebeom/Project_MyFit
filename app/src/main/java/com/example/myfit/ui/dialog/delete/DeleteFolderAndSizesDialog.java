package com.example.myfit.ui.dialog.delete;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myfit.R;
import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.data.repository.SizeRepository;
import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.ui.main.MainGraphViewModel;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.example.myfit.ui.dialog.delete.BaseDeleteDialog.DELETE_SELECT_ITEMS;

@AndroidEntryPoint
public class DeleteFolderAndSizesDialog extends BaseDialog {
    @Inject
    FolderRepository folderRepository;
    @Inject
    SizeRepository sizeRepository;
    private long[] selectedFolderIds;
    private long[] selectedSizeIds;
    private NavBackStackEntry navBackStackEntry;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedFolderIds = DeleteFolderAndSizesDialogArgs.fromBundle(getArguments()).getSelectedFolderIds();
        selectedSizeIds = DeleteFolderAndSizesDialogArgs.fromBundle(getArguments()).getSelectedSizeIds();
        NavController navController = NavHostFragment.findNavController(this);
        navBackStackEntry = navController.getBackStackEntry(R.id.deleteFolderAndSizesDialog);
        setBackStackEntryLive(navController);
    }

    private void setBackStackEntryLive(@NotNull NavController navController) {
        NavBackStackEntry mainBackStackEntry = navController.getBackStackEntry(navController.getGraph().getId());
        MainGraphViewModel mainGraphViewModel = new ViewModelProvider(mainBackStackEntry, HiltViewModelFactory.create(requireContext(), mainBackStackEntry)).get(MainGraphViewModel.class);
        mainGraphViewModel.setBackStackEntryLive(navBackStackEntry);
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return getAlertDialog();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        String selectedItemsSize = String.valueOf(selectedFolderIds.length + selectedSizeIds.length);
        return dialogBuilder.makeConfirmDialog(selectedItemsSize + getString(R.string.dialog_message_selected_item_delete))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            folderRepository.deleteOrRestore(selectedFolderIds, true);
            sizeRepository.deleteOrRestore(selectedSizeIds, true);
            navBackStackEntry.getSavedStateHandle().set(DELETE_SELECT_ITEMS, null);
            dismiss();
        };
    }
}
