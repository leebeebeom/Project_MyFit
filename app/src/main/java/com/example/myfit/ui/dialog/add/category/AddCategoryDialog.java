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
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myfit.R;
import com.example.myfit.databinding.ItemDialogEditTextBinding;
import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.ui.dialog.DialogBindingBuilder;
import com.example.myfit.ui.dialog.DialogBuilder;
import com.example.myfit.util.CommonUtil;
import com.example.myfit.util.KeyBoardUtil;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddCategoryDialog extends BaseDialog {
    public static final String INPUT_TEXT = "input text";
    @Inject
    DialogBindingBuilder dialogBindingBuilder;
    @Inject
    DialogBuilder dialogBuilder;
    private NavController navController;
    private AddCategoryDialogViewModel model;
    private ItemDialogEditTextBinding binding;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String inputText = savedInstanceState == null ? "" : savedInstanceState.getString(INPUT_TEXT);
        navController = NavHostFragment.findNavController(this);
        NavBackStackEntry navBackStackEntry = navController.getBackStackEntry(navController.getGraph().getId());
        model = new ViewModelProvider(navBackStackEntry, HiltViewModelFactory.create(requireContext(), navBackStackEntry)).get(AddCategoryDialogViewModel.class);
        binding = dialogBindingBuilder
                .setHint(getString(R.string.dialog_hint_category_name))
                .setPlaceHolder(getString(R.string.dialog_place_holder_category_name))
                .setText(inputText)
                .create();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        model.getIsExistingLive().observe(this, isExisting -> {
            if (isExisting != null) {
                if (isExisting) {
                    navigateToAddSameCategoryNameDialog();
                    KeyBoardUtil.hideKeyBoard(binding.et);
                } else {
                    model.insert();
                    dismiss();
                }
                model.getIsExistingLive().setValue(null);
            }
        });

        return getAlertDialog();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        return dialogBuilder
                .makeEditTextDialog(getString(R.string.all_add_category), binding.getRoot())
                .setPositiveClickListener(getPositiveClickListener())
                .setPositiveEnabledByIsInputTextEmpty(getInputText())
                .setPositiveEnabledByIsChangedTextEmpty(binding.et)
                .setPositiveCallOnClickWhenImeClicked(binding.et)
                .create();
    }

    private void navigateToAddSameCategoryNameDialog() {
        NavDirections action = AddCategoryDialogDirections.toAddSameCategoryNameDialog();
        CommonUtil.navigate(navController, R.id.addCategoryDialog, action);
    }

    @NotNull
    @Contract(pure = true)
    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            byte parentIndex = (byte) AddCategoryDialogArgs.fromBundle(getArguments()).getParentIndex();
            model.queryIsExistingName(getInputText(), parentIndex);
        };
    }

    @NotNull
    private String getInputText() {
        return String.valueOf(binding.et.getText());
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INPUT_TEXT, String.valueOf(binding.et.getText()));
    }
}
