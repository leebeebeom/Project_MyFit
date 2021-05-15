package com.example.myfit.data.model.tuple;

import java.util.Objects;

public class ParentIdTuple {
    private long id, parentId;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParentIdTuple)) return false;
        ParentIdTuple that = (ParentIdTuple) o;
        return getId() == that.getId() &&
                getParentId() == that.getParentId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getParentId());
    }
}
