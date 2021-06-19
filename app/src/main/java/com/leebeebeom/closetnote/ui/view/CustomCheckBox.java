package com.leebeebeom.closetnote.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.Nullable;

import com.leebeebeom.closetnote.R;
import com.google.android.material.checkbox.MaterialCheckBox;

public class CustomCheckBox extends MaterialCheckBox {
    public CustomCheckBox(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        setAlpha(0.8f);
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
        setButtonTintList(ColorStateList.valueOf(typedValue.data));
        setClickable(false);
        setEnabled(false);
    }
}
