package com.example.myfit.ui.dialog.add.folder;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myfit.R;
import com.example.myfit.databinding.ItemDialogEditTextBinding;
import com.example.myfit.ui.dialog.DialogBindingBuilder;
import com.example.myfit.ui.dialog.DialogBuilder;
import com.example.myfit.util.CommonUtil;
import com.example.myfit.util.KeyBoardUtil;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddFolderDialog extends DialogFragment {
    @Inject
    DialogBindingBuilder dialogBindingBuilder;
    @Inject
    DialogBuilder dialogBuilder;
    private NavController navController;
    private AddFolderDialogViewModel model;
    private ItemDialogEditTextBinding binding;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        NavBackStackEntry navBackStackEntry = navController.getBackStackEntry(navController.getGraph().getId());
        model = new ViewModelProvider(navBackStackEntry, HiltViewModelFactory.create(requireContext(), navBackStackEntry)).get(AddFolderDialogViewModel.class);
        binding = dialogBindingBuilder
                .setHint(getString(R.string.dialog_hint_folder_name))
                .setPlaceHolder(getString(R.string.dialog_place_holder_folder_name))
                .create();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        model.getIsExistingLive().observe(this, isExisting -> {
            if (isExisting != null && isExisting) {
                navigateToSameFolderNameDialog();
                KeyBoardUtil.hideKeyBoard(binding.et);
            } else if (isExisting != null) {
                model.insert();
                dismiss();
            }
        });
        return getAlertDialog();
    }

    private Dialog getAlertDialog() {
        return dialogBuilder
                .makeEditTextDialog(getString(R.string.all_create_folder), binding.getRoot())
                .setPositiveClickListener(getPositiveClickListener())
                .setPositiveCallOnClickWhenImeClicked(binding.et)
                .setPositiveEnabledByIsInputTextEmpty(getInputText())
                .setPositiveEnabledByIsChangedTextEmpty(binding.et)
                .create();
    }

    private void navigateToSameFolderNameDialog() {
        NavDirections action = AddFolderDialogDirections.toAddSameFolderNameDialog();
        CommonUtil.navigate(navController, R.id.addFolderDialog, action);
    }

    @NotNull
    @Contract(pure = true)
    private View.OnClickListener getPositiveClickListener() {
        return v -> {
            long parentId = AddFolderDialogArgs.fromBundle(getArguments()).getFolderId();
            byte parentIndex = (byte) AddFolderDialogArgs.fromBundle(getArguments()).getParentIndex();
            model.queryIsExistingFolderName(getInputText(), parentId, parentIndex);
        };
    }

    @NotNull
    private String getInputText() {
        return String.valueOf(binding.et.getText());
    }
}
