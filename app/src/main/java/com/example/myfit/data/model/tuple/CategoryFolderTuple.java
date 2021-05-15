package com.example.myfit.data.model.tuple;

import java.util.Objects;

public class CategoryFolderTuple extends BaseTuple {
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
        if (!(o instanceof CategoryFolderTuple)) return false;
        if (!super.equals(o)) return false;
        CategoryFolderTuple that = (CategoryFolderTuple) o;
        return getContentsSize() == that.getContentsSize();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getContentsSize());
    }
}
