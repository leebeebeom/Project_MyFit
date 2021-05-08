package com.example.project_myfit.data.model;

public class ParentModel {
    private final long id;
    private long parentId;
    private int parentCategoryIndex;

    public ParentModel(long id, long parentId, int parentCategoryIndex) {
        this.id = id;
        this.parentId = parentId;
        this.parentCategoryIndex = parentCategoryIndex;
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

    public int getParentCategoryIndex() {
        return parentCategoryIndex;
    }

    public void setParentCategoryIndex(int parentCategoryIndex) {
        this.parentCategoryIndex = parentCategoryIndex;
    }
}
