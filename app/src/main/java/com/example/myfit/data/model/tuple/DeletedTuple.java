package com.example.myfit.data.model.tuple;

public class DeletedTuple {
    private long id;
    private boolean isDeleted;

    public long getId() {
        return id;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}