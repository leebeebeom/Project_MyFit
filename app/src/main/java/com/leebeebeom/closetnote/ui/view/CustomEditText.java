package com.leebeebeom.closetnote.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.leebeebeom.closetnote.R;

import org.jetbrains.annotations.NotNull;

public class CustomEditText extends TextInputEditText {
    public CustomEditText(@NonNull @NotNull Context context) {
        super(context);
        init();
    }

    public CustomEditText(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditText(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen._11sdp));
    }
}
