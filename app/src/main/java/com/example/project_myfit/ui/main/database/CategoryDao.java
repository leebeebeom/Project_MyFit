package com.example.project_myfit.ui.main.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM ChildCategory ORDER BY orderNumber")
    LiveData<List<ChildCategory>> getAllChildByOrder();

    @Query("SELECT max(orderNumber) FROM ChildCategory")
    int getLargestOrder();

    @Insert
    void insert(ChildCategory childCategory);

    @Update
    void update(ChildCategory childCategory);

    @Update
    void updateOrder(List<ChildCategory> childCategoryList);

    @Delete
    void delete(ChildCategory childCategory);

}
