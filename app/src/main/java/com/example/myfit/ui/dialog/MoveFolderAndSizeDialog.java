package com.example.myfit.ui.dialog;

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
import com.example.myfit.ui.main.MainGraphViewModel;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MoveFolderAndSizeDialog extends BaseDialog {
    @Inject
    FolderRepository folderRepository;
    @Inject
    SizeRepository sizeRepository;

    private NavController navController;
    private long[] folderIds;
    private long[] sizeIds;
    private long targetId;
    private NavBackStackEntry navBackStackEntry;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        folderIds = MoveFolderAndSizeDialogArgs.fromBundle(getArguments()).getFolderIds();
        sizeIds = MoveFolderAndSizeDialogArgs.fromBundle(getArguments()).getSizeIds();
        targetId = MoveFolderAndSizeDialogArgs.fromBundle(getArguments()).getTargetId();
        navController = NavHostFragment.findNavController(this);

        navBackStackEntry = navController.getBackStackEntry(R.id.moveFolderAndSizeDialog);
        setBackStackEntryLive();
    }

    private void setBackStackEntryLive() {
        NavBackStackEntry mainBackStackEntry = navController.getBackStackEntry(navController.getGraph().getId());
        MainGraphViewModel mainGraphViewModel = new ViewModelProvider(mainBackStackEntry, HiltViewModelFactory.create(requireContext(), mainBackStackEntry))
                .get(MainGraphViewModel.class);
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
        String selectedItemsSize = String.valueOf(folderIds.length + sizeIds.length);
        return dialogBuilder.makeConfirmDialog(selectedItemsSize + getString(R.string.dialog_message_item_move))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            folderRepository.move(targetId, folderIds);
            sizeRepository.move(targetId, sizeIds);
            navBackStackEntry.getSavedStateHandle().set(ACTION_MODE_OFF, null);
            navController.popBackStack(R.id.treeViewDialog, true);
        };
    }

    @Override
    protected NavBackStackEntry getBackStack() {
        return getNavController().getBackStackEntry(R.id.moveFolderAndSizeDialog);
    }
}
