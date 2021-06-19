package com.leebeebeom.closetnote.util.constant;

public enum RecentSearchType {
    SEARCH(0), RECYCLE_BIN(1), DAILY_LOOK(2), WISH_LIST(3);

    private final int value;

    RecentSearchType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
