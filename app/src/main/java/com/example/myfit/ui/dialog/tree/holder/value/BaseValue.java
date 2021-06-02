package com.example.myfit.ui.dialog.tree.holder.value;

import com.example.myfit.data.tuple.BaseTuple;

public class BaseValue<T extends BaseTuple> {
    private final T mTuple;

    protected BaseValue(T tuple) {
        this.mTuple = tuple;
    }

    public T getTuple() {
        return mTuple;
    }

    public long getTupleId() {
        return mTuple.getId();
    }

    public int getParentIndex() {
        return mTuple.getParentIndex();
    }
}
