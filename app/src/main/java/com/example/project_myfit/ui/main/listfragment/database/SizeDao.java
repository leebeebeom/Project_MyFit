package com.example.project_myfit.ui.main.listfragment.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SizeDao {
    @Query("SELECT * FROM Size WHERE folderId = :folderId AND isDeleted = :isDeleted ORDER BY orderNumber DESC")
    LiveData<List<Size>> getAllSizeLiveByFolder(long folderId, int isDeleted);

    @Query("SELECT * FROM size WHERE isDeleted = :isDeleted ORDER BY name")
    LiveData<List<Size>> getAllSizeLive(int isDeleted);

    @Query("SELECT folderId FROM Size WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted")
    List<Long> getSizeFolderIdByParent(String parentCategory, int isDeleted);

    @Query("SELECT * FROM Size WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted ORDER BY name")
    List<Size> getAllSizeListByParent(String parentCategory, int isDeleted);

    @Query("SELECT brand FROM Size WHERE isDeleted = :isDeleted ORDER BY brand")
    List<String> getSizeBrandList(int isDeleted);

    @Query("SELECT name FROM Size WHERE isDeleted = :isDeleted ORDER BY brand")
    List<String> getSizeNameList(int isDeleted);

    @Query("SELECT * FROM Size WHERE id = :id")
    Size getSize(int id);

    @Query("SELECT max(orderNumber) FROM Size")
    int getSizeLargestOrder();

    @Insert
    void sizeInsert(Size size);

    @Update
    void sizeUpdate(Size size);

    @Update
    void sizeUpdate(List<Size> sizeList);

    @Query("SELECT * FROM Size WHERE isDeleted = :isDeleted ORDER BY name")
    List<Size> getAllSize(int isDeleted);
}
