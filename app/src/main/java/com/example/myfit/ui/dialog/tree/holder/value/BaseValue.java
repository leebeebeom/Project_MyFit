package com.example.myfit.ui.dialog.tree.holder.value;

import com.example.myfit.data.tuple.tuple.CategoryTuple;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
@AllArgsConstructor
public class BaseValue<T extends CategoryTuple> {
    @Getter
    private final T mTuple;
}
