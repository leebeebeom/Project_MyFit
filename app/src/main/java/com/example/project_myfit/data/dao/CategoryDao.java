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
    @Query("SELECT * FROM Category WHERE isDeleted = 0 ORDER BY orderNumber")
    LiveData<List<Category>> getCategoryLive();

    @Query("SELECT * FROM Category WHERE id = :id")
    LiveData<Category> getCategoryLive(long id);

    @Query("SELECT * FROM Category WHERE isDeleted = 1")
    LiveData<List<Category>> getDeletedCategoryLive();

    @Query("SELECT * FROM Category WHERE isDeleted = 0 ORDER BY orderNumber")
    List<Category> getCategoryList();

    @Query("SELECT * FROM Category WHERE parentCategory = :parentCategory AND isDeleted = 0 ORDER BY orderNumber")
    List<Category> getCategoryList(String parentCategory);

    @Query("SELECT categoryName FROM Category WHERE isDeleted = 1")
    LiveData<List<String>> getDeletedCategoryNameLive();

    @Query("SELECT categoryName FROM Category WHERE isDeleted = 0 AND parentCategory = :parentCategory")
    List<String> getCategoryNameList(String parentCategory);

    @Query("SELECT * FROM Category WHERE id = :id")
    Category getCategory(long id);

    @Query("SELECT max(orderNumber) FROM Category")
    int getCategoryLargestOrder();

    @Insert
    void categoryInsert(Category category);

    @Insert
    void categoryInsert(List<Category> categoryList);

    @Update
    void categoryUpdate(Category category);

    @Update
    void categoryUpdate(List<Category> categoryList);
}
