package com.example.myfit.data.model;

import androidx.room.Embedded;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BaseModel {
    @Embedded
    private final DefaultInfo defaultInfo;

    public BaseModel(@NotNull DefaultInfo defaultInfo) {
        this.defaultInfo = defaultInfo;
    }

    public long getId() {
        return defaultInfo.getId();
    }

    public byte getParentCategoryIndex() {
        return defaultInfo.getParentCategoryIndex();
    }

    public int getOrderNumber() {
        return defaultInfo.getOrderNumber();
    }

    public void setOrderNumber(int orderNumber) {
        this.defaultInfo.setOrderNumber(orderNumber);
    }

    public boolean isDeleted() {
        return defaultInfo.isDeleted();
    }

    public void setDeleted(boolean deleted) {
        this.defaultInfo.setDeleted(deleted);
    }

    public boolean isDummy() {
        return defaultInfo.isDummy();
    }

    public void setDummy() {
        this.defaultInfo.setDummy(!isDummy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseModel)) return false;
        BaseModel baseModel = (BaseModel) o;
        return defaultInfo.equals(baseModel.defaultInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultInfo);
    }
}
