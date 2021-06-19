package com.leebeebeom.closetnote.util.constant;

public enum SharedPreferenceKey {
    SORT_MAIN("sort main"), SORT_LIST("sort list"), SORT("sort"),
    VIEW_TYPE("view type"), FOLDER_TOGGLE("folder toggle"),
    CATEGORY_LARGEST_SORT_NUMBER("category largest sort number"),
    LARGEST_SORT_NUMBER("largest sort number");

    private final String text;

    SharedPreferenceKey(String text) {
        this.text = text;
    }

    public String getValue() {
        return text;
    }
}
