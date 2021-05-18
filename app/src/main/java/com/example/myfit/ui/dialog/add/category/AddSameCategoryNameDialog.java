package com.example.myfit.ui.dialog.add.category;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.hilt.navigation.HiltViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;

import com.example.myfit.R;
import com.example.myfit.ui.dialog.add.BaseAddSameNameDialog;
import com.example.myfit.ui.dialog.add.BaseAddViewModel;

import org.jetbrains.annotations.NotNull;

public class AddSameCategoryNameDialog extends BaseAddSameNameDialog {
    @Override
    protected BaseAddViewModel getModel(NavBackStackEntry navBackStackEntry) {
        return new ViewModelProvider(navBackStackEntry, HiltViewModelFactory.create(requireContext(), navBackStackEntry)).get(AddCategoryDialogViewModel.class);
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return getAlertDialog();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.dialog_message_same_category_name_add);
    }

    @Override
    protected int getDestinationId() {
        return R.id.addCategoryDialog;
    }
}
