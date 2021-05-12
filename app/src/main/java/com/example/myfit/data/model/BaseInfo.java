package com.example.myfit.data.model;

import androidx.room.PrimaryKey;

import java.util.Objects;

public class BaseInfo {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private final byte parentIndex;
    private int orderNumber;
    private boolean isDeleted, dummy;

    public BaseInfo(byte parentIndex, int orderNumber) {
        this.parentIndex = parentIndex;
        this.orderNumber = orderNumber;
    }

    public long getId() {
        return id;
    }

    public byte getParentIndex() {
        return parentIndex;
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
        if (!(o instanceof BaseInfo)) return false;
        BaseInfo baseInfo = (BaseInfo) o;
        return getId() == baseInfo.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
