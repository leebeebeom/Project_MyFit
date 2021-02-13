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
    @Query("SELECT * FROM Size WHERE folderId = :folderId AND isDeleted = :isDeleted ORDER BY orderNumber DESC")
    LiveData<List<Size>> getSizeLive(long folderId, int isDeleted);

    @Query("SELECT * FROM Size WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted ORDER BY name")
    List<Size> getAllSizeList(String parentCategory, int isDeleted);

    @Query("SELECT brand FROM Size WHERE isDeleted = :isDeleted ORDER BY brand")
    List<String> getBrandList(int isDeleted);

    @Query("SELECT name FROM Size WHERE isDeleted = :isDeleted ORDER BY brand")
    List<String> getNameList(int isDeleted);

    @Query("SELECT * FROM Size WHERE id = :id")
    Size getSize(int id);

    @Query("SELECT MAX(orderNumber) FROM Size")
    int getLargestOrder();

    @Insert
    void insert(Size size);

    @Update
    void update(Size size);

    @Update
    void update(List<Size> sizeList);

    @Delete
    void delete(Size size);

    @Delete
    void delete(List<Size> sizeList);
}
