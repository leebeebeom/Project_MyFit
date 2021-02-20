package com.example.project_myfit.ui.main.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM Category WHERE isDeleted = :isDeleted ORDER BY orderNumber")
    LiveData<List<Category>> getAllCategoryLive(int isDeleted);

    @Query("SELECT * FROM Category WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted ORDER BY orderNumber")
    List<Category> getCategoryListByParent(String parentCategory, int isDeleted);

    @Query("SELECT * FROM Category ORDER BY id DESC limit 1")
    Category getLatestCategory();

    @Query("SELECT max(orderNumber) FROM Category")
    int getCategoryLargestOrder();

    @Query("SELECT * FROM Category WHERE id = :id")
    Category getCategory(long id);

    @Insert
    void categoryInsert(Category category);

    @Update
    void categoryUpdate(Category category);

    @Update
    void categoryUpdate(List<Category> categoryList);
}
