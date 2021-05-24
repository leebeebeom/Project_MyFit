package com.example.myfit.util.constant;

public enum Sort {
    SORT_CUSTOM(0),
    SORT_CREATE(1),
    SORT_CREATE_REVERSE(2),
    SORT_BRAND(3),
    SORT_BRAND_REVERSE(4),
    SORT_NAME(5),
    SORT_NAME_REVERSE(6),
    SORT_DELETED(7);

    private final int value;

    Sort(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
