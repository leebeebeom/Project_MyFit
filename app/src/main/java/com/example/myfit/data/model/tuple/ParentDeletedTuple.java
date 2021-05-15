package com.example.myfit.data.model.tuple;

import java.util.Objects;

public class ParentDeletedTuple {
    private long id;
    private boolean isParentDeleted;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public boolean isParentDeleted() {
        return isParentDeleted;
    }

    public void setParentDeleted(boolean parentDeleted) {
        isParentDeleted = parentDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParentDeletedTuple)) return false;
        ParentDeletedTuple that = (ParentDeletedTuple) o;
        return getId() == that.getId() &&
                isParentDeleted() == that.isParentDeleted();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), isParentDeleted());
    }
}
