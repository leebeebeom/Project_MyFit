package com.example.myfit.util.constant;

public enum Sort {
    SORT("sort"), SORT_LIST("sort list"), SORT_MAIN("sort main");

    private final String text;

    Sort(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
