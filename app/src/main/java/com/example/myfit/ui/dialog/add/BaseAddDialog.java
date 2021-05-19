package com.example.myfit.ui.dialog.add;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myfit.databinding.ItemDialogEditTextBinding;
import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.ui.dialog.DialogBindingBuilder;
import com.example.myfit.util.KeyBoardUtil;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseAddDialog extends BaseDialog {
    public static final String INPUT_TEXT = "input text";
    @Inject
    DialogBindingBuilder dialogBindingBuilder;
    private NavController navController;
    private BaseAddViewModel model;
    private ItemDialogEditTextBinding binding;
    private String inputText;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputText = savedInstanceState == null ? "" : savedInstanceState.getString(INPUT_TEXT);
        navController = NavHostFragment.findNavController(this);
        NavBackStackEntry addGraphBackStackEntry = navController.getBackStackEntry(navController.getGraph().getId());
        model = getModel(addGraphBackStackEntry);
        binding = getBinding();
    }

    protected abstract BaseAddViewModel getModel(NavBackStackEntry addGraphBackStackEntry);

    private ItemDialogEditTextBinding getBinding() {
        return dialogBindingBuilder
                .setHint(getHint())
                .setPlaceHolder(getPlaceHolder())
                .setText(inputText)
                .create();
    }

    protected abstract String getHint();

    protected abstract String getPlaceHolder();

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        model.getIsExistingMutable().observe(this, isExisting -> {
            if (isExisting != null) {
                if (isExisting) {
                    navigateSameNameDialog(navController);
                    KeyBoardUtil.hideKeyBoard(binding.et);
                } else {
                    model.insert();
                    dismiss();
                }
                model.getIsExistingMutable().setValue(null);
            }
        });

        return getAlertDialog();
    }

    protected abstract void navigateSameNameDialog(NavController navController);

    @Override
    protected AlertDialog getAlertDialog() {
        return dialogBuilder
                .makeEditTextDialog(getDialogTitle(), binding.getRoot())
                .setPositiveClickListener(getPositiveClickListener(model))
                .setPositiveEnabledByInputText(inputText)
                .setPositiveEnabledByChangedText(binding.et)
                .setPositiveCallOnClickWhenImeClicked(binding.et)
                .create();
    }

    protected abstract String getDialogTitle();

    protected abstract View.OnClickListener getPositiveClickListener(BaseAddViewModel model);

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INPUT_TEXT, String.valueOf(binding.et.getText()));
    }

    @NotNull
    protected String getInputText() {
        return String.valueOf(binding.et.getText());
    }
}
