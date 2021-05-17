package com.example.myfit.ui.dialog;

import android.view.KeyEvent;
import android.view.LayoutInflater;

import com.example.myfit.R;
import com.example.myfit.databinding.ItemDialogEditTextBinding;

import javax.inject.Inject;

public class DialogBindingBuilder {
    private final ItemDialogEditTextBinding mBinding;

    @Inject
    public DialogBindingBuilder(LayoutInflater inflater) {
        this.mBinding = ItemDialogEditTextBinding.inflate(inflater);
        mBinding.et.requestFocus();
        this.showErrorIfMoreThan30Characters();
    }

    public DialogBindingBuilder setHint(String hint) {
        mBinding.setHint(hint);
        return this;
    }

    public DialogBindingBuilder setPlaceHolder(String placeHolder) {
        mBinding.setPlaceHolder(placeHolder);
        return this;
    }

    private void showErrorIfMoreThan30Characters() {
        mBinding.et.setOnKeyListener((v, keyCode, event) -> {
            if (isTextLength30() && keyCode != KeyEvent.KEYCODE_DEL && keyCode != KeyEvent.FLAG_EDITOR_ACTION)
                mBinding.layout.setError(v.getContext().getString(R.string.dialog_et_max_length));
            else mBinding.layout.setErrorEnabled(false);
            return false;
        });
    }

    private boolean isTextLength30() {
        return String.valueOf(mBinding.et.getText()).length() == 30;
    }

    public ItemDialogEditTextBinding create() {
        return mBinding;
    }
}
