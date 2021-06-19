package com.leebeebeom.closetnote.ui.dialog.eidttext.edit;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.leebeebeom.closetnote.ui.dialog.eidttext.BaseEditTextDialog;
import com.leebeebeom.closetnote.ui.dialog.eidttext.BaseEditTextViewModel;

public abstract class BaseEditDialog extends BaseEditTextDialog {
    private String mOldName;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOldName = getName();
        setBackStackLive();
    }

    @Override
    protected void task() {
        getModel().update();
        setBackStackActionModeOff();
    }

    protected abstract BaseEditTextViewModel.BaseEditViewModel getModel();

    @Override
    protected AlertDialog getAlertDialog() {
        return mDialogBuilder
                .makeEditTextDialog(getTitle(), mBinding.getRoot())
                .setPositiveClickListener(getPositiveClickListener())
                .setPositiveEnabledByInputText(getModel().getName(), mOldName)
                .setPositiveEnabledByChangedText(mBinding.et, mOldName)
                .setPositiveCallOnClickWhenImeClicked(mBinding.et)
                .create();
    }

    protected abstract String getTitle();
}
