package com.example.myfit.data.model.folder;

import com.example.myfit.data.model.category.CategoryTuple;

import java.util.Objects;

public class FolderTuple extends CategoryTuple {
    private long parentId;

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FolderTuple)) return false;
        if (!super.equals(o)) return false;
        FolderTuple that = (FolderTuple) o;
        return getParentId() == that.getParentId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getParentId());
    }
}
