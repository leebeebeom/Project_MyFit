package com.example.project_myfit.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Category {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private final String parentCategory;
    private int orderNumber;
    private boolean isDeleted, dummy;
    private String categoryName;

    public Category(String categoryName, String parentCategory, int orderNumber) {
        this.categoryName = categoryName;
        this.parentCategory = parentCategory;
        this.orderNumber = orderNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id){this.id = id;}

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String category) {
        this.categoryName = category;
    }

    public String getParentCategory() {
        return parentCategory;
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

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
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