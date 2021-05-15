package com.example.myfit.data.model.tuple;

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
}
