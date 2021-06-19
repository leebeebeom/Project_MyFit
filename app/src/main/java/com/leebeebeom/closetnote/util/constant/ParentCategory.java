package com.leebeebeom.closetnote.util.constant;

public enum ParentCategory {
    TOP(0), BOTTOM(1), OUTER(2), ETC(3);

    private final int value;

    ParentCategory(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
