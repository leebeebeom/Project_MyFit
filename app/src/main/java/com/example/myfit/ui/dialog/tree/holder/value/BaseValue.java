package com.example.myfit.ui.dialog.tree.holder.value;

import com.example.myfit.data.model.tuple.BaseTuple;

public class BaseValue<T extends BaseTuple> {
    private final T tuple;

    public BaseValue(T tuple) {
        this.tuple = tuple;
    }

    public T getTuple() {
        return tuple;
    }

    public long getId() {
        return tuple.getId();
    }

    public int getParentIndex(){
        return tuple.getParentIndex();
    }
}
