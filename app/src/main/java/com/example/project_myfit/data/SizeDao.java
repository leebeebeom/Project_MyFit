package com.example.project_myfit.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SizeDao {
    @Query("SELECT * FROM Size WHERE folderId = :folderId AND isDeleted = :isDeleted ORDER BY orderNumber DESC")
    LiveData<List<Size>> getSizeLiveByFolderId(long folderId, boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT folderId FROM Size WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted")
    List<Long> getSizeFolderIdByParent(String parentCategory, boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT * FROM size WHERE isDeleted = :isDeleted ORDER BY name")
    LiveData<List<Size>> getAllSizeLive(boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT * FROM Size WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted ORDER BY name")
    List<Size> getAllSizeListByParent(String parentCategory, boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT brand FROM Size WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted ORDER BY brand")
    List<String> getSizeBrandList(boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT name FROM Size WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted ORDER BY brand")
    List<String> getSizeNameList(boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT * FROM Size WHERE id = :id")
    Size getSize(long id);

    @Query("SELECT max(orderNumber) FROM Size")
    int getSizeLargestOrder();

    @Insert
    void sizeInsert(Size size);

    @Update
    void sizeUpdate(Size size);

    @Update
    void sizeUpdate(List<Size> sizeList);

    @Query("SELECT * FROM Size WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted ORDER BY name")
    List<Size> getAllSize(boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT * FROM Size WHERE folderId = :folderId AND isDeleted = :isDeleted")
    List<Size> getSizeByFolderId(long folderId, boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT * FROM Size WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted")
    LiveData<List<Size>> getSizeLiveForSearch(boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT * FROM Size WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted")
    List<Size> getAllSizeForSearch(boolean isDeleted, boolean parentIsDeleted);
}
