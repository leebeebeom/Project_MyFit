package com.example.myfit.util.constant;

public enum ParentCategory {
    TOP(0), BOTTOM(1), OUTER(2), ETC(3);

    private int index;

    ParentCategory(int index) {
    }

    public int getIndex() {
        return index;
    }
}
