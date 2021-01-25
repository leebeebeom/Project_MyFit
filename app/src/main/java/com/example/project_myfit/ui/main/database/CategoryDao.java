package com.example.project_myfit.ui.main.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM Category WHERE parentCategory = :parentCategory ORDER BY orderNumber")
    LiveData<List<Category>> getCategoryLive(String parentCategory);

    @Query("SELECT * FROM Category WHERE parentCategory = :parentCategory ORDER BY orderNumber")
    List<Category> getCategoryList(String parentCategory);

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

    @Delete
    void delete(Category category);

}
