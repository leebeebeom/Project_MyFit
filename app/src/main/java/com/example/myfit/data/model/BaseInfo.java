package com.example.myfit.data.model;

import java.util.Objects;

public class BaseInfo {
    private final int parentIndex;
    private int orderNumber;
    private boolean deleted;
    private long deletedTime;

    public BaseInfo(int parentIndex, int orderNumber) {
        this.parentIndex = parentIndex;
        this.orderNumber = orderNumber;
    }

    public int getParentIndex() {
        return parentIndex;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public long getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(long deletedTime) {
        this.deletedTime = deletedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseInfo)) return false;
        BaseInfo baseInfo = (BaseInfo) o;
        return getParentIndex() == baseInfo.getParentIndex() &&
                isDeleted() == baseInfo.isDeleted() &&
                getDeletedTime() == baseInfo.getDeletedTime();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParentIndex(), isDeleted(), getDeletedTime());
    }
}
