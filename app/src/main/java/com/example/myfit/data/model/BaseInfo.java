package com.example.myfit.data.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
//TODO delete 타임 넣기
public class BaseInfo {
    private final byte parentIndex;
    private int orderNumber;
    private boolean isDeleted, dummy;

    public BaseInfo(byte parentIndex, int orderNumber) {
        this.parentIndex = parentIndex;
        this.orderNumber = orderNumber;
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
        return getParentIndex() == baseInfo.getParentIndex() &&
                getOrderNumber() == baseInfo.getOrderNumber() &&
                isDeleted() == baseInfo.isDeleted() &&
                isDummy() == baseInfo.isDummy();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParentIndex(), getOrderNumber(), isDeleted(), isDummy());
    }

    @NotNull
    @Override
    public String toString() {
        return "BaseInfo{" +
                "parentIndex=" + parentIndex +
                ", orderNumber=" + orderNumber +
                ", isDeleted=" + isDeleted +
                ", dummy=" + dummy +
                '}';
    }
}
