package com.example.myfit.ui.dialog.eidttext.edit;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.ui.dialog.eidttext.BaseEditTextDialog;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    protected String getInitialText(Bundle savedInstanceState) {
        return savedInstanceState == null ? mOldName : savedInstanceState.getString(INPUT_TEXT);
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
                .setPositiveEnabledByInputText(mBinding.et.getText(), mOldName)
                .setPositiveEnabledByChangedText(mBinding.et, mOldName)
                .setPositiveCallOnClickWhenImeClicked(mBinding.et)
                .create();
    }

    protected abstract String getTitle();
}
