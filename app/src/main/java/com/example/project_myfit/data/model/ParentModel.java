package com.example.project_myfit.data.model;

public class ParentModel {
    private final long id;
    private final String parentCategory;

    public ParentModel(long id, String parentCategory) {
        this.id = id;
        this.parentCategory = parentCategory;
    }

    public long getId() {
        return id;
    }

    public String getParentCategory() {
        return parentCategory;
    }
}
