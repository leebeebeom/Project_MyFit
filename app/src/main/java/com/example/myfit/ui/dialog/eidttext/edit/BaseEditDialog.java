package com.example.myfit.ui.dialog.eidttext.edit;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.databinding.ItemDialogEditTextBinding;
import com.example.myfit.ui.dialog.DialogBuilder;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextDialog;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;

public abstract class BaseEditDialog extends BaseEditTextDialog {
    private String mOldName;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOldName = getName();
        setBackStackLive();
    }

    protected abstract String getName();

    @Override
    protected String getInputText(Bundle savedInstanceState) {
        return savedInstanceState == null ? mOldName : savedInstanceState.getString(INPUT_TEXT);
    }

    @Override
    protected void task() {
        getModel().update();
        setBackStackActionModeOff();
    }

    protected abstract BaseEditTextViewModel.BaseEditViewModel getModel();

    @Override
    protected AlertDialog getAlertDialog(DialogBuilder dialogBuilder) {
        ItemDialogEditTextBinding binding = getBinding();
        return dialogBuilder
                .makeEditTextDialog(getTitle(), binding.getRoot())
                .setPositiveClickListener(getPositiveClickListener())
                .setPositiveEnabledByInputText(binding.et.getText(), mOldName)
                .setPositiveEnabledByChangedText(binding.et, mOldName)
                .setPositiveCallOnClickWhenImeClicked(binding.et)
                .create();
    }

    protected abstract String getTitle();
}
