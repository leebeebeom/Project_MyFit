package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.project_myfit.NavigationViewModel;
import com.example.project_myfit.R;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.SELECTED_ITEM_DELETE_CONFIRM_CLICK;

public class SelectedItemDeleteDialog extends DialogFragment {

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        NavController navController = NavHostFragment.findNavController(this);
        NavBackStackEntry navBackStackEntry = navController.getBackStackEntry(R.id.selectedItemDeleteDialog);
        NavigationViewModel navigationViewModel = new ViewModelProvider(navController.getViewModelStoreOwner(R.id.main_nav_graph)).get(NavigationViewModel.class);
        navigationViewModel.backStackEntryLiveSetValue(navBackStackEntry);

        int selectedItemSize = SelectedItemDeleteDialogArgs.fromBundle(getArguments()).getSelectedItemSize();

        String message = selectedItemSize + getString(R.string.selected_item_delete_check);
        return DialogUtils.getConfirmDialog(requireContext(), message,
                (dialog, which) -> navBackStackEntry.getSavedStateHandle().set(SELECTED_ITEM_DELETE_CONFIRM_CLICK, null));
    }
}
