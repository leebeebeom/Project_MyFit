package com.example.myfit.util.adapter;

import com.example.myfit.data.tuple.BaseTuple;

import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdapterUtil {
    public static <T extends BaseTuple> void itemMove(int from, int to, List<T> list) {
        if (from < to) {//down
            for (int i = from; i < to; i++)
                if (list.get(i + 1).getId() != -1)
                    swapOrderNumber(list, i, i + 1);
        } else //up
            for (int i = from; i > to; i--)
                swapOrderNumber(list, i, i - 1);
    }

    private static <T extends BaseTuple> void swapOrderNumber(List<T> list, int i, int i2) {
        list.get(i).setSortNumber(list.get(i2).getSortNumber());
        list.get(i2).setSortNumber(list.get(i).getSortNumber());
        Collections.swap(list, i, i2);
    }
}
