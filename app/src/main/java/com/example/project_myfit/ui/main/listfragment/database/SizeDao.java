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
    @Query("SELECT * FROM Size WHERE folderId = :id")
    List<Size> getAllSizeByFolder(int id);

    @Query("SELECT * FROM Size WHERE folderId = :id")
    LiveData<List<Size>> getAllSizeByFolderIdLive(int id);

    @Insert
    void insert(Size size);

    @Update
    void update(Size size);

    @Delete
    void delete(Size size);

    @Query("DELETE FROM Size WHERE folderId = :id")
    void deleteSizeByFolder(int id);

    @Insert
    void restoreDeletedSize(List<Size> sizes);
}
