package com.example.myfit.util.adapter;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.myfit.R;
import com.example.myfit.data.tuple.BaseTuple;
import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdapterUtil {
    public static <T extends BaseTuple> void itemMove(int from, int to, List<T> list) {
        if (from < to) {//down
            for (int i = from; i < to; i++) {
                if (list.get(i++).getId() != -1)
                    swapOrderNumber(list, i, i++);
                Collections.swap(list, i, i++);
            }
        } else {//up
            for (int i = from; i > to; i--) {
                swapOrderNumber(list, i, i--);
                Collections.swap(list, i, i--);
            }
        }
    }

    private static <T extends BaseTuple> void swapOrderNumber(List<T> list, int i, int i2) {
        BaseTuple baseTuple1 = list.get(i);
        BaseTuple baseTuple2 = list.get(i2);
        int order1 = baseTuple1.getSortNumber();
        int order2 = baseTuple2.getSortNumber();
        baseTuple1.setSortNumber(order2);
        baseTuple2.setSortNumber(order1);
    }
}
