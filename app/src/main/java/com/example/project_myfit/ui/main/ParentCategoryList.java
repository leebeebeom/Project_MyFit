package com.example.project_myfit.ui.main;

import com.example.project_myfit.ui.main.database.ParentCategory;

import java.util.List;

public class ParentCategoryList {
    private int id;
    private List<ParentCategory> parentCategories;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ParentCategory> getParentCategories() {
        return parentCategories;
    }

    public void setParentCategories(List<ParentCategory> parentCategories) {
        this.parentCategories = parentCategories;
    }
}
