package com.example.project_myfit.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Category implements Model {
    @PrimaryKey
    private final long id;
    private final int parentCategoryIndex;
    private int orderNumber;
    private boolean isDeleted, dummy;
    private String categoryName;

    public Category(long id, String categoryName, int parentCategoryIndex, int orderNumber) {
        this.id = id;
        this.categoryName = categoryName;
        this.parentCategoryIndex = parentCategoryIndex;
        this.orderNumber = orderNumber;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public int getParentCategoryIndex() {
        return parentCategoryIndex;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean getDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public long getParentId() {
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return getId() == category.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
