package com.example.myfit.ui.dialog.eidttext.edit;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;
import com.example.myfit.ui.dialog.eidttext.BaseSameNameDialog;

public abstract class BaseEditSameNameDialog extends BaseSameNameDialog {
    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackStackLive();
    }

    @Override
    protected void task() {
        getModel().update();
        actionModeOff();
    }

    protected abstract BaseEditTextViewModel.BaseEditViewModel getModel();
}
