package com.example.myfit.data.model.tuple;

import java.util.Objects;

public class ParentDeletedTuple {
    private long id;
    private boolean deleted, parentDeleted;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public boolean isParentDeleted() {
        return parentDeleted;
    }

    public void setParentDeleted(boolean parentDeleted) {
        this.parentDeleted = parentDeleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParentDeletedTuple)) return false;
        ParentDeletedTuple that = (ParentDeletedTuple) o;
        return getId() == that.getId() &&
                isDeleted() == that.isDeleted() &&
                isParentDeleted() == that.isParentDeleted();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), isDeleted(), isParentDeleted());
    }
}
