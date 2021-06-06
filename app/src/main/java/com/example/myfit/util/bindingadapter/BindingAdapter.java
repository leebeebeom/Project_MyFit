package com.example.myfit.util.bindingadapter;

import com.google.android.material.textfield.TextInputEditText;

public class BindingAdapter {
    @androidx.databinding.BindingAdapter("android:text")
    public static void text(TextInputEditText editText, String text) {
        if (text != null) {
            editText.setText(text);
            editText.setSelection(text.length());
        }
    }
}
