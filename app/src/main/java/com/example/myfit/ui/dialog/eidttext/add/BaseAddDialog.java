package com.example.myfit.ui.dialog.eidttext.add;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import com.example.myfit.ui.dialog.eidttext.BaseEditTextDialog;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;

public abstract class BaseAddDialog extends BaseEditTextDialog {
    @Override
    protected String getInitialText(Bundle savedInstanceState) {
        return savedInstanceState == null ? "" : savedInstanceState.getString(INPUT_TEXT);
    }

    @Override
    protected void task() {
        getModel().insert();
    }

    protected abstract BaseEditTextViewModel.BaseAddViewModel getModel();

    @Override
    protected AlertDialog getAlertDialog() {
        return mDialogBuilder
                .makeEditTextDialog(getTitle(), mBinding.getRoot())
                .setPositiveClickListener(getPositiveClickListener())
                .setPositiveEnabledByInputText(mBinding.et.getText())
                .setPositiveEnabledByChangedText(mBinding.et)
                .setPositiveCallOnClickWhenImeClicked(mBinding.et)
                .create();
    }

    protected abstract String getTitle();
}
