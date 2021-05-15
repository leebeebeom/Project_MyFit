package com.example.myfit.data.model.tuple;

import java.util.Objects;

public class DeletedTuple {
    private long id;
    private boolean isDeleted;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeletedTuple)) return false;
        DeletedTuple that = (DeletedTuple) o;
        return getId() == that.getId() &&
                isDeleted() == that.isDeleted();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), isDeleted());
    }
}
