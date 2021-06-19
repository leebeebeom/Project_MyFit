package com.leebeebeom.closetnote.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.leebeebeom.closetnote.R;
import com.google.android.material.card.MaterialCardView;

public class CustomCardView extends MaterialCardView {
    public CustomCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.myCardViewBackground, typedValue, true);
        setCardBackgroundColor(typedValue.data);
        setRadius(getResources().getDimension(R.dimen._4sdp));
        setElevation(getResources().getDimension(R.dimen._2sdp));
    }
}
