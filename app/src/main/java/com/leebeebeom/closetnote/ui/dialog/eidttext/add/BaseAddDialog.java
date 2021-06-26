package com.leebeebeom.closetnote.ui.dialog.eidttext.add;

import androidx.appcompat.app.AlertDialog;

import com.leebeebeom.closetnote.ui.dialog.eidttext.BaseEditTextDialog;
import com.leebeebeom.closetnote.ui.dialog.eidttext.BaseEditTextViewModel;

public abstract class BaseAddDialog extends BaseEditTextDialog {
    @Override
    protected String getInitialText() {
        return "";
    }

    @Override
    protected void task() {
        getModel().insert();
    }

    protected abstract BaseEditTextViewModel.BaseAddViewModel getModel();

    @Override
    protected AlertDialog getAlertDialog() {
        mBinding.setDialogBuilder(mDialogBuilder);
        return mDialogBuilder
                .makeEditTextDialog(getTitle(), mBinding.getRoot())
                .setPositiveClickListener(getPositiveClickListener())
                .setPositiveEnabledByInputText(mBinding.et.getText())
                .setPositiveCallOnClickWhenImeClicked(mBinding.et)
//                .setPositiveEnabledByChangedText(mBinding.et) <- 버그 있음
                .create();
    }

    protected abstract String getTitle();
}
