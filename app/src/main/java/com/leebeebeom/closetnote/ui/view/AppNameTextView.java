package com.leebeebeom.closetnote.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.leebeebeom.closetnote.R;

import org.jetbrains.annotations.NotNull;

public class AppNameTextView extends MaterialTextView {
    public AppNameTextView(@NonNull @NotNull Context context) {
        super(context);
        init(context);
        if (!isInEditMode())
            init(context);
    }

    public AppNameTextView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        if (!isInEditMode())
            init(context);
    }

    public AppNameTextView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        if (!isInEditMode())
            init(context);
    }

    public AppNameTextView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
        if (!isInEditMode())
            init(context);
    }

    private void init(Context context) {
        this.setText(context.getString(R.string.app_name));
        this.setFontFamily(context);
    }

    private void setFontFamily(Context context) {
        Typeface typeface = context.getResources().getFont(R.font.ballet_harmony);
        this.setTypeface(typeface);
    }
}
