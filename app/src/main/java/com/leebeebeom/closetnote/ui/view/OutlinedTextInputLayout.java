package com.leebeebeom.closetnote.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;

import com.google.android.material.textfield.TextInputLayout;
import com.leebeebeom.closetnote.R;

import org.jetbrains.annotations.NotNull;

public class OutlinedTextInputLayout extends TextInputLayout {
    public OutlinedTextInputLayout(@NonNull @NotNull Context context) {
        super(getThemeContext(context));
        init();

    }

    @NotNull
    private static ContextThemeWrapper getThemeContext(@NonNull @NotNull Context context) {
        return new ContextThemeWrapper(context, R.style.myOutlineEditText);
    }

    public OutlinedTextInputLayout(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(getThemeContext(context), attrs);
        init();

    }

    public OutlinedTextInputLayout(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(getThemeContext(context), attrs, defStyleAttr);
        init();

    }

    private void init() {
        setHintTextAppearance(R.style.myTextSize8sdp);
        setErrorTextAppearance(R.style.myTextSize8sdp);
        float corner = getContext().getResources().getDimension(R.dimen._6sdp);
        setBoxCornerRadii(corner, corner, corner, corner);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getLayoutParams().height = getContext().getResources().getDimensionPixelSize(R.dimen._44sdp);
    }
}
