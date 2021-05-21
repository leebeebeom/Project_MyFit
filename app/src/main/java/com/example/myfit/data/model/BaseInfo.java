package com.example.myfit.data.model;

import java.util.Objects;
//TODO delete 타임 넣기
public class BaseInfo {
    private final int parentIndex;
    private int orderNumber;
    private boolean isDeleted, dummy;
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
                getOrderNumber() == baseInfo.getOrderNumber() &&
                isDeleted() == baseInfo.isDeleted() &&
                isDummy() == baseInfo.isDummy() &&
                getDeletedTime() == baseInfo.getDeletedTime();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParentIndex(), getOrderNumber(), isDeleted(), isDummy(), getDeletedTime());
    }
}
