package com.leebeebeom.closetnote.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.leebeebeom.closetnote.R;

import org.jetbrains.annotations.NotNull;

public class IconDot extends AppCompatImageView {
    public IconDot(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        setImageResource(R.drawable.icon_dot);
//        Glide.with(this).load(R.drawable.icon_dot).error(R.drawable.icon_dot).into(this);
        setAlpha(0.8f);
        setScaleX(0.3f);
        setScaleY(0.3f);
        setVisibility(VISIBLE);

        if (!isInEditMode()) {
            setImageResource(R.drawable.icon_dot);
//            Glide.with(this).load(R.drawable.icon_dot).error(R.drawable.icon_dot).into(this);
            setAlpha(0.8f);
            setScaleX(0.3f);
            setScaleY(0.3f);
            setVisibility(VISIBLE);
        }

    }
}
