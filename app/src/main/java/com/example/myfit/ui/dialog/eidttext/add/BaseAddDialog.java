package com.example.myfit.ui.dialog.eidttext.add;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import com.example.myfit.databinding.ItemDialogEditTextBinding;
import com.example.myfit.ui.dialog.DialogBuilder;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextDialog;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;

public abstract class BaseAddDialog extends BaseEditTextDialog {
    @Override
    protected String getInputText(Bundle savedInstanceState) {
        return savedInstanceState == null ? "" : savedInstanceState.getString(INPUT_TEXT);
    }

    @Override
    protected void task() {
        getModel().insert();
    }

    protected abstract BaseEditTextViewModel.BaseAddViewModel getModel();

    @Override
    protected AlertDialog getAlertDialog(DialogBuilder dialogBuilder) {
        ItemDialogEditTextBinding binding = getBinding();
        return dialogBuilder
                .makeEditTextDialog(getTitle(), binding.getRoot())
                .setPositiveClickListener(getPositiveClickListener())
                .setPositiveEnabledByInputText(binding.et.getText())
                .setPositiveEnabledByChangedText(binding.et)
                .setPositiveCallOnClickWhenImeClicked(binding.et)
                .create();
    }

    protected abstract String getTitle();
}
