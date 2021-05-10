package com.example.myfit.data.model;

import androidx.room.PrimaryKey;

import java.util.Objects;

public class DefaultInfo {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private final byte parentCategoryIndex;
    private int orderNumber;
    private boolean isDeleted, dummy;

    public DefaultInfo(byte parentCategoryIndex, int orderNumber) {
        this.parentCategoryIndex = parentCategoryIndex;
        this.orderNumber = orderNumber;
    }

    public long getId() {
        return id;
    }

    public byte getParentCategoryIndex() {
        return parentCategoryIndex;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultInfo)) return false;
        DefaultInfo that = (DefaultInfo) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
