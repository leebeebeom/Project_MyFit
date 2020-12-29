package com.example.project_myfit.ui.main.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class Category {
    private final String parentCategory;
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int orderNumber;
    private String category;


    public Category(String category, String parentCategory, int orderNumber) {
        this.category = category;
        this.parentCategory = parentCategory;
        this.orderNumber = orderNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @NotNull
    @Override
    public String toString() {
        return "ChildCategory{" +
                "id=" + id +
                ", order=" + orderNumber +
                ", childCategory='" + category + '\'' +
                ", parentCategory='" + parentCategory + '\'' +
                '}';
    }
}
