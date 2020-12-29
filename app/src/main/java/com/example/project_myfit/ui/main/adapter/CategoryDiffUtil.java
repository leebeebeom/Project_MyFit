package com.example.project_myfit.ui.main.adapter;

import androidx.recyclerview.widget.DiffUtil;

import com.example.project_myfit.ui.main.database.Category;

import java.util.List;

public class CategoryDiffUtil extends DiffUtil.Callback {
    private final List<Category> mOldCategoryList;
    private final List<Category> mNewCategoryList;

    public CategoryDiffUtil(List<Category> oldCategoryList, List<Category> newCategoryList) {
        mOldCategoryList = oldCategoryList;
        mNewCategoryList = newCategoryList;
    }

    @Override
    public int getOldListSize() {
        return mOldCategoryList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewCategoryList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldCategoryList.get(oldItemPosition).getId() == mNewCategoryList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Category oldCategory = mOldCategoryList.get(oldItemPosition);
        Category newCategory = mNewCategoryList.get(newItemPosition);
        return oldCategory.getCategory().equals(newCategory.getCategory())
                && oldCategory.getOrderNumber() == newCategory.getOrderNumber();
    }
}
