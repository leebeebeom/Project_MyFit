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
    List<Size> getAllSizeByFolderNotLive(int id);
    //Use at Main Fragment Delete

    @Query("SELECT * FROM Size WHERE folderId = :id ORDER BY orderNumber")
    LiveData<List<Size>> getAllSize(int id);

    @Query("SELECT MAX(orderNumber) FROM Size")
    int getLargestOrder();

    @Insert
    void insert(Size size);

    @Update
    void update(Size size);

    @Update
    void updateOrder(List<Size> sizeList);

    @Delete
    void delete(Size size);

    @Delete
    void delete(List<Size> sizeList);

    @Query("DELETE FROM Size WHERE folderId = :id")
    void deleteSizeByFolder(int id);

    @Insert
    void restoreDeletedSize(List<Size> sizes);

    @Query("SELECT * FROM Size WHERE id = :id")
    Size getSizeById(int id);
}
