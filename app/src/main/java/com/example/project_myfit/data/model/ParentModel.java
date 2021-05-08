package com.example.project_myfit.data.model;

public class ParentModel {
    private final long id;
    private long parentId;
    private int parentCategory;

    public ParentModel(long id, long parentId, int parentCategory) {
        this.id = id;
        this.parentId = parentId;
        this.parentCategory = parentCategory;
    }

    public long getId() {
        return id;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getParentId() {
        return parentId;
    }

    public int getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(int parentCategory) {
        this.parentCategory = parentCategory;
    }
}
