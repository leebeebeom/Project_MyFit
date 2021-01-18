package com.example.project_myfit.ui.main.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Category {
    private final String parentCategory;
    @PrimaryKey(autoGenerate = true)
    private long id;
    private int orderNumber;
    private String category;
    private String itemAmount;


    public Category(String category, String parentCategory, int orderNumber) {
        this.category = category;
        this.parentCategory = parentCategory;
        this.orderNumber = orderNumber;
        this.itemAmount = "0";
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

    public String getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(String itemAmount) {
        this.itemAmount = itemAmount;
    }
}
