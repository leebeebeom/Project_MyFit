package com.leebeebeom.closetnote.util;

import android.text.TextUtils;

import androidx.databinding.adapters.TextViewBindingAdapter;

import com.google.android.material.textfield.TextInputLayout;

public class EditTextErrorKeyListener extends OnTextChange {
    private final TextInputLayout mTextInputLayout;

    public EditTextErrorKeyListener(TextInputLayout textInputLayout) {
        mTextInputLayout = textInputLayout;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mTextInputLayout.isErrorEnabled() && !TextUtils.isEmpty(s))
            mTextInputLayout.setErrorEnabled(false);
    }
}
