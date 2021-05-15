package com.example.myfit.data.model.tuple;

import java.util.Objects;

public class OrderNumberTuple {
    private long id;
    private int orderNumber;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
    return id;
}

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderNumberTuple)) return false;
        OrderNumberTuple that = (OrderNumberTuple) o;
        return getId() == that.getId() &&
                getOrderNumber() == that.getOrderNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOrderNumber());
    }
}
