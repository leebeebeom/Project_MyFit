package com.example.myfit.util.constant;

public enum ViewType {
    LIST_VIEW(0), GRID_VIEW(1);

    private final int value;

    ViewType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
