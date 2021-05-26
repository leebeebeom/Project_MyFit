package com.example.myfit.ui.dialog.eidttext.add;

import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;
import com.example.myfit.ui.dialog.eidttext.BaseSameNameDialog;

public abstract class BaseAddSameNameDialog extends BaseSameNameDialog {
    @Override
    protected void task() {
        getModel().insert();
    }

    protected abstract BaseEditTextViewModel.BaseAddViewModel getModel();
}
