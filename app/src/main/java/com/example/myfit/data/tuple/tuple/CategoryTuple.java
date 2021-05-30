package com.example.myfit.data.tuple.tuple;

import com.example.myfit.data.tuple.BaseTuple;

import java.util.Objects;

public class CategoryTuple extends BaseTuple {
    private int contentsSize;

    public int getContentsSize() {
        return contentsSize;
    }

    public void setContentsSize(int contentsSize) {
        this.contentsSize = contentsSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryTuple)) return false;
        if (!super.equals(o)) return false;
        CategoryTuple that = (CategoryTuple) o;
        return getContentsSize() == that.getContentsSize();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getContentsSize());
    }
}
