package com.example.myfit.data.model.tuple;

import java.util.Objects;

public class BaseTuple {
    private long id;
    private byte parentIndex;
    private int orderNumber;
    private String name;

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

    public byte getParentIndex() {
        return parentIndex;
    }

    public void setParentIndex(byte parentIndex) {
        this.parentIndex = parentIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseTuple)) return false;
        BaseTuple baseTuple = (BaseTuple) o;
        return getId() == baseTuple.getId() &&
                getParentIndex() == baseTuple.getParentIndex() &&
                getOrderNumber() == baseTuple.getOrderNumber() &&
                getName().equals(baseTuple.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getParentIndex(), getOrderNumber(), getName());
    }
}
