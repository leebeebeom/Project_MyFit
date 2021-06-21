package com.leebeebeom.closetnote.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.leebeebeom.closetnote.R;

import org.jetbrains.annotations.NotNull;

public class SignInButton extends MaterialButton {
    public SignInButton(@NonNull @NotNull Context context) {
        super(getThemeContext(context));
        init();
    }

    public SignInButton(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(getThemeContext(context), attrs);
        init();
    }

    public SignInButton(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(getThemeContext(context), attrs, defStyleAttr);
        init();
    }

    @NotNull
    private static ContextThemeWrapper getThemeContext(@NotNull Context context) {
        return new ContextThemeWrapper(context, R.style.mySignInButton);
    }

    private void init() {
        setAllCaps(false);
        setTypeface(Typeface.DEFAULT_BOLD);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen._9sdp));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getLayoutParams().height = getContext().getResources().getDimensionPixelSize(R.dimen._48sdp);
    }
}
