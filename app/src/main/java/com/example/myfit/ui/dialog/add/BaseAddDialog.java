package com.example.myfit.ui.dialog.add;

import android.app.Dialog;
import android.os.Bundle;

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
    protected DialogBindingBuilder dialogBindingBuilder;
    private NavController navController;
    protected BaseAddViewModel model;
    private ItemDialogEditTextBinding binding;
    private String inputText;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputText = savedInstanceState == null ? "" : savedInstanceState.getString(INPUT_TEXT);
        navController = NavHostFragment.findNavController(this);
        NavBackStackEntry navBackStackEntry = navController.getBackStackEntry(navController.getGraph().getId());
        model = getModel(navBackStackEntry);
        binding = getBinding();
    }

    protected abstract BaseAddViewModel getModel(NavBackStackEntry navBackStackEntry);

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
        model.getIsExistingLive().observe(this, isExisting -> {
            if (isExisting != null) {
                if (isExisting) {
                    navigateSameNameDialog(navController);
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

    protected abstract void navigateSameNameDialog(NavController navController);

    @Override
    protected AlertDialog getAlertDialog() {
        return dialogBuilder
                .makeEditTextDialog(getDialogTitle(), binding.getRoot())
                .setPositiveClickListener(getPositiveClickListener())
                .setPositiveEnabledByIsInputText(inputText)
                .setPositiveEnabledByIsChangedText(binding.et)
                .setPositiveCallOnClickWhenImeClicked(binding.et)
                .create();
    }

    protected abstract String getDialogTitle();

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
