package com.example.myfit.ui.dialog.eidttext.edit;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.databinding.ItemDialogEditTextBinding;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextDialog;

public abstract class BaseEditDialog extends BaseEditTextDialog {
    private String oldName;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oldName = getName();
        setBackStackLive();
    }

    protected abstract String getName();

    @Override
    protected String getInitialText(Bundle savedInstanceState) {
        return savedInstanceState == null ? oldName : savedInstanceState.getString(INPUT_TEXT);
    }

    @Override
    protected void task() {
        getModel().update();
        actionModeOff();
    }

    protected abstract BaseEditViewModel getModel();

    @Override
    protected AlertDialog getAlertDialog() {
        ItemDialogEditTextBinding binding = getBinding();
        return dialogBuilder
                .makeEditTextDialog(getTitle(), binding.getRoot())
                .setPositiveClickListener(getPositiveClickListener())
                .setPositiveEnabledByInputText(getInputText(), oldName)
                .setPositiveEnabledByChangedText(binding.et, oldName)
                .setPositiveCallOnClickWhenImeClicked(binding.et)
                .create();
    }

    protected abstract String getTitle();
}