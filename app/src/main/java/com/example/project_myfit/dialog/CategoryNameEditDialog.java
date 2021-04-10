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
import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.databinding.ItemDialogEditTextBinding;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.CATEGORY;
import static com.example.project_myfit.MyFitConstant.CATEGORY_NAME;

public class CategoryNameEditDialog extends DialogFragment {
    private ItemDialogEditTextBinding mBinding;

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Repository repository = new Repository(requireContext());
        long categoryId = CategoryNameEditDialogArgs.fromBundle(getArguments()).getCategoryId();
        Category category = repository.getCategory(categoryId);
        String categoryName = savedInstanceState == null ? category.getCategoryName() : savedInstanceState.getString(CATEGORY_NAME);

        NavController navController = NavHostFragment.findNavController(this);
        NavigationViewModel navigationViewModel = new ViewModelProvider(navController.getViewModelStoreOwner(R.id.main_nav_graph))
                .get(NavigationViewModel.class);

        mBinding = DialogUtils.getBinding(getLayoutInflater(), requireContext(), categoryName, CATEGORY);
        return DialogUtils.getEditTextDialog(requireContext(), getString(R.string.edit_category_name), mBinding,
                (dialog, which) -> {
                    navigationViewModel.backStackEntryLiveSetValue(navController.getBackStackEntry(R.id.categoryNameEditDialog));
                    category.setCategoryName(String.valueOf(mBinding.dialogEditText.getText()).trim());
                    repository.categoryUpdate(category);
                });
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CATEGORY_NAME, String.valueOf(mBinding.dialogEditText.getText()));
    }
}
