package com.example.project_myfit.ui.main.listfragment.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SizeDao {
    @Query("SELECT * FROM Size WHERE folderId LIKE :id")
    LiveData<List<Size>> getAllSizeByFolderId(int id);

    @Insert
    void insert(Size size);

    @Update
    void update(Size size);

    @Delete
    void delete(Size size);
}
