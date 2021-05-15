package com.example.myfit.data.model.tuple;

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
}
