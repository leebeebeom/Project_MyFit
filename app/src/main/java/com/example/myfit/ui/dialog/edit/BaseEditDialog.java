package com.example.myfit.ui.dialog.edit;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavBackStackEntry;

import com.example.myfit.databinding.ItemDialogEditTextBinding;
import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.ui.dialog.BaseDialogViewModel;
import com.example.myfit.ui.dialog.DialogBindingBuilder;
import com.example.myfit.util.KeyBoardUtil;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseEditDialog extends BaseDialog {
    public static final String EDIT_NAME = "name";
    @Inject
    DialogBindingBuilder dialogBindingBuilder;
    private ItemDialogEditTextBinding binding;
    private String oldName;
    private NavBackStackEntry backStackEntry;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oldName = getName();
        String inputText = savedInstanceState == null ? oldName : savedInstanceState.getString(EDIT_NAME);
        binding = getBinding(inputText);

        backStackEntry = getBackStackEntry();
        setBackStackLive(backStackEntry);
    }

    protected abstract String getName();

    private ItemDialogEditTextBinding getBinding(String inputText) {
        return dialogBindingBuilder
                .setHint(getHint())
                .setPlaceHolder(getPlaceHolder())
                .setText(inputText).create();
    }

    protected abstract String getHint();

    protected abstract String getPlaceHolder();

    protected abstract NavBackStackEntry getBackStackEntry();

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        BaseDialogViewModel model = getModel();

        model.getIsExistingMutable().observe(this, isExisting -> {
            if (isExisting != null) {
                if (isExisting) {
                    navigateSameNameDialog();
                    KeyBoardUtil.hideKeyBoard(binding.et);
                } else {
                    model.update();
                    actionModeOff(backStackEntry);
                    dismiss();
                }
                model.getIsExistingMutable().setValue(null);
            }
        });

        return getAlertDialog();
    }

    protected abstract BaseDialogViewModel getModel();

    protected abstract void navigateSameNameDialog();

    @Override
    protected AlertDialog getAlertDialog() {
        return dialogBuilder.makeEditTextDialog(getTitle(), binding.getRoot())
                .setPositiveClickListener(getPositiveClickListener())
                .setPositiveEnabledByInputText(binding.et.getText(), oldName)
                .setPositiveEnabledByChangedText(binding.et, oldName)
                .setPositiveCallOnClickWhenImeClicked(binding.et)
                .create();
    }

    protected abstract String getTitle();

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EDIT_NAME, getInputText());
    }

    @NotNull
    protected String getInputText() {
        return String.valueOf(binding.et.getText());
    }
}
