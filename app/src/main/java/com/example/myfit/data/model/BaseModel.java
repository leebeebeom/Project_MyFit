package com.example.myfit.data.model;

import androidx.room.Embedded;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BaseModel {
    @Embedded
    private final BaseInfo baseInfo;

    public BaseModel(@NotNull BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public long getId() {
        return baseInfo.getId();
    }

    public byte getParentCategoryIndex() {
        return baseInfo.getParentCategoryIndex();
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
