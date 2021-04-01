package com.example.project_myfit.data;

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
    private String category;

    public Category(String category, String parentCategory, int orderNumber) {
        this.category = category;
        this.parentCategory = parentCategory;
        this.orderNumber = orderNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
