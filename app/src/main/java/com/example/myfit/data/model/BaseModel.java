package com.example.myfit.data.model;

import androidx.room.Embedded;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BaseModel {
    @Embedded
    private BaseInfo baseInfo;

    public BaseModel(@NotNull BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public long getId() {
        return baseInfo.getId();
    }

    public byte getParentIndex() {
        return baseInfo.getParentIndex();
    }

    public int getOrderNumber() {
        return baseInfo.getOrderNumber();
    }

    public void setOrderNumber(int orderNumber) {
        this.baseInfo.setOrderNumber(orderNumber);
    }

    public boolean isDeleted() {
        return baseInfo.isDeleted();
    }

    public void setDeleted(boolean deleted) {
        this.baseInfo.setDeleted(deleted);
    }

    public boolean isDummy() {
        return baseInfo.isDummy();
    }

    public void setDummy() {
        this.baseInfo.setDummy(!isDummy());
    }

    public BaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseModel)) return false;
        BaseModel baseModel = (BaseModel) o;
        return baseInfo.equals(baseModel.baseInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseInfo);
    }
}
