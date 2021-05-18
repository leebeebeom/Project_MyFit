package com.example.myfit.ui.dialog.edit;

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
import com.example.myfit.ui.main.MainGraphViewModel;

import org.jetbrains.annotations.NotNull;

import static com.example.myfit.ui.dialog.edit.BaseEditDialog.EDIT_NAME;

public abstract class BaseEditSameNameDialog extends BaseDialog {
    private BaseEditViewModel model;
    private NavBackStackEntry navBackStackEntry;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);
        navBackStackEntry = getBackstackEntry(navController);
        setBackStackLive(navController);

        NavBackStackEntry editGraphBackStackEntry = navController.getBackStackEntry(navController.getGraph().getId());
        model = getModel(editGraphBackStackEntry);
    }

    protected abstract NavBackStackEntry getBackstackEntry(NavController navController);

    private void setBackStackLive(@NotNull NavController navController) {
        NavBackStackEntry mainBackStackEntry = navController.getBackStackEntry(R.id.nav_graph_main);
        MainGraphViewModel mainGraphViewModel = new ViewModelProvider(mainBackStackEntry, HiltViewModelFactory.create(requireContext(), mainBackStackEntry))
                .get(MainGraphViewModel.class);
        mainGraphViewModel.setBackStackEntryLive(navBackStackEntry);
    }

    protected abstract BaseEditViewModel getModel(NavBackStackEntry editGraphBackStackEntry);


    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return getAlertDialog();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        return dialogBuilder.makeConfirmDialog(getMessage())
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    protected abstract String getMessage();

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            model.update();
            navBackStackEntry.getSavedStateHandle().set(EDIT_NAME, null);
            dismiss();
        };
    }
}
