package com.example.project_myfit.ui.main.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

//Dao 클래스
@Dao
public interface CategoryDao {
    @Query("SELECT * FROM ChildCategory")
    LiveData<List<ChildCategory>> getAllCategory();

    @Query("SELECT * FROM ChildCategory")
    List<ChildCategory> getAllCategoryNotLive();

    @Insert
    void insert(ChildCategory childCategory);

    @Delete
    void delete(ChildCategory childCategory);

    @Query("SELECT * FROM ChildCategory ORDER BY id DESC")
    LiveData<List<ChildCategory>> getAllCategoryByIdInverse();

    @Query("SELECT * FROM ChildCategory ORDER BY childCategory ASC")
    LiveData<List<ChildCategory>> getAllCategoryByName();

    @Query("SELECT * FROM ChildCategory ORDER BY childCategory DESC")
    LiveData<List<ChildCategory>> getAllCategoryByNameInverse();

}
