package com.example.project_myfit.data.model;

public class ParentModel {
    private final long id;
    private long parentId;
    private String parentCategory;

    public ParentModel(long id, long parentId, String parentCategory) {
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

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    public String getParentCategory() {
        return parentCategory;
    }
}
