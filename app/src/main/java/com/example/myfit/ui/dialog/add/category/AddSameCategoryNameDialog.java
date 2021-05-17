package com.example.myfit.ui.dialog.add.category;

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
import com.example.myfit.ui.dialog.BaseDialog;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddSameCategoryNameDialog extends BaseDialog {
    private NavController navController;
    private AddCategoryDialogViewModel model;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        NavBackStackEntry navBackStackEntry = navController.getBackStackEntry(navController.getGraph().getId());
        model = new ViewModelProvider(navBackStackEntry, HiltViewModelFactory.create(requireContext(), navBackStackEntry)).get(AddCategoryDialogViewModel.class);
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return getAlertDialog();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        return dialogBuilder.makeConfirmDialog(getString(R.string.dialog_message_same_category_name_add))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @NotNull
    @Contract(pure = true)
    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            model.insert();
            navController.popBackStack(R.id.addCategoryDialog, true);
        };
    }
}
