package com.leebeebeom.closetnote.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.leebeebeom.closetnote.R;

import org.jetbrains.annotations.NotNull;

public class OutlinedTextInputLayout extends TextInputLayout {
    public OutlinedTextInputLayout(@NonNull @NotNull Context context) {
        super(context);
        init();

    }

    public OutlinedTextInputLayout(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public OutlinedTextInputLayout(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        setHintTextAppearance(R.style.myTextSize9sdp);
        setErrorTextAppearance(R.style.myTextSize9sdp);
        setPlaceholderTextAppearance(R.style.myTextSize9sdp);
        float corner = getContext().getResources().getDimension(R.dimen._6sdp);
        setBoxCornerRadii(corner, corner, corner, corner);
    }
}
