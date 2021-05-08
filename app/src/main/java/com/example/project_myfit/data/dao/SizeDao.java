package com.example.project_myfit.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.project_myfit.data.model.Size;

import java.util.List;

@Dao
public interface SizeDao {
    @Query("SELECT * FROM size WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted ORDER BY name")
    LiveData<List<Size>> getSizeLive(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Size WHERE parentId = :parentId AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted ORDER BY orderNumber DESC")
    LiveData<List<Size>> getSizeLive(long parentId, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Size WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted ORDER BY orderNumber")
    List<Size> getSizeList(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Size WHERE parentId = :parentId AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    List<Size> getSizeList(long parentId, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT parentId FROM Size WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    List<Long> getSizeParentIdList(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT parentId FROM Size WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    List<Long> getSizeParentIdList(int parentCategory, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT brand FROM Size WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    LiveData<List<String>> getSizeBrandLive(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT name FROM Size WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    LiveData<List<String>> getSizeNameLive(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT brand FROM Size WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted ORDER BY brand")
    List<String> getSizeBrandList(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Size WHERE id = :id")
    Size getSize(long id);

    @Query("SELECT max(orderNumber) FROM Size")
    int getSizeLargestOrder();

    @Insert
    void insertSize(Size size);

    @Update
    void updateSize(Size size);

    @Update
    void updateSize(List<Size> sizeList);
}
