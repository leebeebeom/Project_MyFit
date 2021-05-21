package com.example.myfit.data.model.tuple;

import java.util.Objects;

public class BaseTuple {
    private long id;
    private int parentIndex, orderNumber;
    private String name;
    private long deletedTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getParentIndex() {
        return parentIndex;
    }

    public void setParentIndex(int parentIndex) {
        this.parentIndex = parentIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(o instanceof BaseTuple)) return false;
        BaseTuple baseTuple = (BaseTuple) o;
        return getId() == baseTuple.getId() &&
                getParentIndex() == baseTuple.getParentIndex() &&
                getOrderNumber() == baseTuple.getOrderNumber() &&
                getDeletedTime() == baseTuple.getDeletedTime() &&
                getName().equals(baseTuple.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getParentIndex(), getOrderNumber(), getName(), getDeletedTime());
    }
}
