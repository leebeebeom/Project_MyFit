package com.example.myfit.data.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class BaseInfo {
    private final int parentIndex;
    private int orderNumber;
    private boolean deleted;
    private long deletedTime;

    public BaseInfo(int parentIndex, int orderNumber) {
        this.parentIndex = parentIndex;
        this.orderNumber = orderNumber;
    }
}
