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
    @Query("SELECT * FROM Category WHERE isDeleted = :isDeleted ORDER BY orderNumber DESC")
    LiveData<List<Category>> getAllCategoryLive(boolean isDeleted);

    @Query("SELECT * FROM Category WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted ORDER BY orderNumber")
    List<Category> getCategoryListByParent(String parentCategory, boolean isDeleted);

    @Query("SELECT max(orderNumber) FROM Category")
    int getCategoryLargestOrder();

    @Query("SELECT * FROM Category WHERE id = :id")
    Category getCategory(long id);

    @Query("SELECT * FROM Category ORDER BY id DESC limit 1")
    Category getLatestCategory();

    @Insert
    void categoryInsert(Category category);

    @Insert
    void categoryInsert(List<Category> categoryList);

    @Update
    void categoryUpdate(Category category);

    @Update
    void categoryUpdate(List<Category> categoryList);
}
