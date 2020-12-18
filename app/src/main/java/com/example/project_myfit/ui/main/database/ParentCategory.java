package com.example.project_myfit.ui.main.database;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.node.BaseExpandNode;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ParentCategory extends BaseExpandNode {

    private String parentCategory;
    private List<BaseNode> childCategoryList;

    public ParentCategory(String parentCategory, List<BaseNode> childCategoryList) {
        this.parentCategory = parentCategory;
        this.childCategoryList = childCategoryList;

        setExpanded(false);
    }

    public String getParentCategory() {
        return parentCategory;
    }

    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return childCategoryList;
    }
}
