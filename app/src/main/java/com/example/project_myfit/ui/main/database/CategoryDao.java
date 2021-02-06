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
    LiveData<List<Category>> getCategoryLive(boolean isDeleted);

    @Query("SELECT * FROM Category WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted ORDER BY orderNumber")
    List<Category> getCategoryList(String parentCategory, boolean isDeleted);

    @Query("SELECT * FROM Category ORDER BY id DESC limit 1")
    Category getLatestCategory();

    @Query("SELECT max(orderNumber) FROM Category")
    int getLargestOrder();

    @Query("SELECT * FROM Category WHERE id = :id")
    Category getCategory(long id);

    @Insert
    void insert(Category category);

    @Update
    void update(Category category);

    @Update
    void update(List<Category> categoryList);

}
