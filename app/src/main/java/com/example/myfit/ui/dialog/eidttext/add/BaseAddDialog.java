package com.example.myfit.ui.dialog.eidttext.add;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import com.example.myfit.databinding.ItemDialogEditTextBinding;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextDialog;

public abstract class BaseAddDialog extends BaseEditTextDialog {
    private String initialText;

    @Override
    protected String getInitialText(Bundle savedInstanceState) {
        if (initialText == null)
            initialText = savedInstanceState == null ? "" : savedInstanceState.getString(INPUT_TEXT);
        return initialText;
    }

    @Override
    protected void task() {
        getModel().insert();
    }

    protected abstract BaseAddViewModel getModel();

    @Override
    protected AlertDialog getAlertDialog() {
        ItemDialogEditTextBinding binding = getBinding();
        return dialogBuilder
                .makeEditTextDialog(getTitle(), binding.getRoot())
                .setPositiveClickListener(getPositiveClickListener())
                .setPositiveEnabledByInputText(initialText)
                .setPositiveEnabledByChangedText(binding.et)
                .setPositiveCallOnClickWhenImeClicked(binding.et)
                .create();
    }

    protected abstract String getTitle();
}