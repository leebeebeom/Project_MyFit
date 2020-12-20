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
    @Query("SELECT * FROM ChildCategory")
    LiveData<List<ChildCategory>> getAllChildCategory();

    @Insert
    void insert(ChildCategory childCategory);

    @Update
    void update(ChildCategory childCategory);

    @Delete
    void delete(ChildCategory childCategory);

}
