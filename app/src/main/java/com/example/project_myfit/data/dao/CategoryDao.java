package com.example.project_myfit.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.project_myfit.data.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM Category WHERE isDeleted = :isDeleted ORDER BY orderNumber")
    LiveData<List<Category>> getCategoryLive(boolean isDeleted);

    @Query("SELECT * FROM Category WHERE id = :id")
    LiveData<Category> getCategoryLive(long id);

    @Query("SELECT * FROM Category WHERE isDeleted = :isDeleted ORDER BY orderNumber")
    List<Category> getCategoryList(boolean isDeleted);

    @Query("SELECT * FROM Category WHERE parentCategoryIndex = :parentCategoryIndex AND isDeleted = :isDeleted ORDER BY orderNumber")
    List<Category> getCategoryList(int parentCategoryIndex, boolean isDeleted);

    @Query("SELECT categoryName FROM Category WHERE isDeleted = :isDeleted")
    LiveData<List<String>> getCategoryNameLive(boolean isDeleted);

    @Query("SELECT categoryName FROM Category WHERE isDeleted = :isDeleted AND parentCategoryIndex = :parentCategoryIndex")
    List<String> getCategoryNameList(int parentCategoryIndex, boolean isDeleted);

    @Query("SELECT * FROM Category WHERE id = :id AND isDeleted = :isDeleted")
    Category getCategory(long id, boolean isDeleted);

    @Query("SELECT * FROM category WHERE categoryName = :categoryName AND parentCategoryIndex = :parentCategoryIndex AND isDeleted = :isDeleted")
    Category getCategory(String categoryName, int parentCategoryIndex, boolean isDeleted);

    @Query("SELECT max(orderNumber) FROM Category")
    int getCategoryLargestOrder();

    @Insert
    void insertCategory(Category category);

    @Insert
    void insertCategory(List<Category> categoryList);

    @Update
    void updateCategory(Category category);

    @Update
    void updateCategory(List<Category> categoryList);
}
