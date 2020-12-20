package com.example.project_myfit.ui.main.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Entity
public class ChildCategory extends BaseNode {
    @PrimaryKey(autoGenerate = true)
    private int id;
    /*
    TODO
    order 추가, PrimaryKeys 공부
     */
    private String childCategory;
    private String parentCategory;


    public ChildCategory(String childCategory, String parentCategory) {
        this.childCategory = childCategory;
        this.parentCategory = parentCategory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChildCategory() {
        return childCategory;
    }

    public void setChildCategory(String childCategory) {
        this.childCategory = childCategory;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    @NotNull
    @Override
    public String toString() {
        return "Clothing{" +
                "clothing='" + childCategory + '\'' +
                '}';
    }

    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return null;
    }
}
