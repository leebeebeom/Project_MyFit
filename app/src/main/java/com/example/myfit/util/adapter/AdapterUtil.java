package com.example.myfit.util.adapter;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.myfit.R;
import com.example.myfit.data.model.tuple.BaseTuple;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class AdapterUtil<T extends BaseTuple> {
    private Animation openAnimation;

    public void listActionModeOn(@NotNull MaterialCardView cardView) {
        if (openAnimation == null)
            openAnimation = AnimationUtils.loadAnimation(cardView.getContext(), R.anim.item_list_slide_right);
        if (!openAnimation.hasStarted())
            cardView.setAnimation(openAnimation);
    }

    public void listActionModeOff(@NotNull MaterialCardView cardView) {
        cardView.setAnimation(AnimationUtils.loadAnimation(cardView.getContext(), R.anim.item_list_slide_left));
        openAnimation = null;
    }

    public void gridActionModeOn(@NotNull MaterialCheckBox checkBox) {
        checkBox.setVisibility(View.VISIBLE);
    }

    public void gridActionModeOff(@NotNull MaterialCheckBox checkBox) {
        checkBox.jumpDrawablesToCurrentState();
        checkBox.setVisibility(View.GONE);
    }

    public void itemMove(int from, int to, List<T> list) {
        if (from < to) {//down
            for (int i = from; i < to; i++) {
                swapOrderNumber(list, i, i + 1);
                Collections.swap(list, i, i + 1);
            }
        } else {//up
            for (int i = from; i > to; i--) {
                swapOrderNumber(list, i, i - 1);
                Collections.swap(list, i, i - 1);
            }
        }
    }

    private void swapOrderNumber(List<T> list, int i, int i2) {
        BaseTuple baseTuple1 = list.get(i);
        BaseTuple baseTuple2 = list.get(i2);
        int order1 = baseTuple1.getOrderNumber();
        int order2 = baseTuple2.getOrderNumber();
        baseTuple1.setOrderNumber(order2);
        baseTuple2.setOrderNumber(order1);
    }
}
