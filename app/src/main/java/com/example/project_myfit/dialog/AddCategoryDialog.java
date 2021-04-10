package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.project_myfit.NavigationViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.CATEGORY;

public class AddCategoryDialog extends DialogFragment {

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        NavController navController = NavHostFragment.findNavController(this);
        NavigationViewModel navigationViewModel = new ViewModelProvider(navController.getViewModelStoreOwner(R.id.main_nav_graph)).get(NavigationViewModel.class);

        String parentCategory = AddCategoryDialogArgs.fromBundle(getArguments()).getParentCategory();

        ItemDialogEditTextBinding binding = DialogUtils.getBinding(getLayoutInflater(), requireContext(), null, CATEGORY);

        return DialogUtils.getEditTextDialog(requireContext(), getString(R.string.add_category), binding,
                (dialog, which) -> navigationViewModel.addCategoryConfirmClick(String.valueOf(binding.dialogEditText.getText()).trim(), parentCategory));
    }
}
