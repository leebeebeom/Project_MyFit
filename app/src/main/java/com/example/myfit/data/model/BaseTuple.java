package com.example.myfit.data.model;

public class BaseTuple {
    private final long id;
    private final byte parentCategoryIndex;
    private int orderNumber;
    private boolean dummy;
    private String name;

    public BaseTuple(long id, byte parentCategoryIndex) {
        this.id = id;
        this.parentCategoryIndex = parentCategoryIndex;
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

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
