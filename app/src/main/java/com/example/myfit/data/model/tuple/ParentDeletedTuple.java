package com.example.myfit.data.model.tuple;

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
}
