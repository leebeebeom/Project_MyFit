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
    LiveData<List<Category>> getAllCategoryLive();

    @Query("SELECT * FROM Category WHERE isDeleted = 0 ORDER BY orderNumber")
    List<Category> getAllCategoryList();

    @Query("SELECT * FROM Category WHERE parentCategory = :parentCategory AND isDeleted = 0 ORDER BY orderNumber")
    List<Category> getCategoryListByParentCategory(String parentCategory);

    @Query("SELECT categoryName FROM Category WHERE isDeleted = 0 AND parentCategory = :parentCategory")
    List<String> getCategoryNameListByParentCategory(String parentCategory);

    @Query("SELECT * FROM Category WHERE id = :id")
    Category getCategory(long id);

    @Query("SELECT * FROM Category WHERE id = :id")
    LiveData<Category> getCategoryLive(long id);

    @Query("SELECT * FROM Category ORDER BY id DESC limit 1")
    Category getLatestCategory();

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
