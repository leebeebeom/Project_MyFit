package com.example.myfit.util.adapter;

import com.example.myfit.data.tuple.BaseTuple;

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
            }
        } else {//up
            for (int i = from; i > to; i--) {
                swapOrderNumber(list, i, i--);
            }
        }
    }

    private static <T extends BaseTuple> void swapOrderNumber(List<T> list, int i, int i2) {
        BaseTuple baseTuple1 = list.get(i);
        BaseTuple baseTuple2 = list.get(i2);
        baseTuple1.setSortNumber(baseTuple2.getSortNumber());
        baseTuple2.setSortNumber(baseTuple1.getSortNumber());
    }
}
