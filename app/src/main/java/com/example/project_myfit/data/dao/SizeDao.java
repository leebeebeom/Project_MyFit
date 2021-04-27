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
    @Query("SELECT * FROM size WHERE isDeleted = 0 AND parentIsDeleted = 0 ORDER BY name")
    LiveData<List<Size>> getSizeLive();

    @Query("SELECT * FROM Size WHERE parentId = :parentId AND isDeleted = 0 AND parentIsDeleted = 0 ORDER BY orderNumber DESC")
    LiveData<List<Size>> getSizeLive(long parentId);

    @Query("SELECT * FROM Size WHERE isDeleted = 1")
    LiveData<List<Size>> getDeletedSizeLive();

    @Query("SELECT * FROM Size WHERE isDeleted = 0 AND parentIsDeleted = 0 ORDER BY orderNumber")
    List<Size> getSizeList();

    @Query("SELECT * FROM Size WHERE parentId = :parentId AND isDeleted = 0 AND parentIsDeleted = 0 ")
    List<Size> getSizeList(long parentId);

    @Query("SELECT parentId FROM Size WHERE parentCategory = :parentCategory AND isDeleted = 0 AND parentIsDeleted = 0")
    List<Long> getSizeParentIdList(String parentCategory);

    @Query("SELECT parentId FROM Size WHERE isDeleted =0 AND parentIsDeleted =1")
    List<Long> getParentDeletedSizeParentIdList();

    @Query("SELECT brand FROM Size WHERE isDeleted = 0 AND parentIsDeleted = 0")
    LiveData<List<String>> getSizeBrandLive();

    @Query("SELECT name FROM Size WHERE isDeleted = 0 AND parentIsDeleted = 0")
    LiveData<List<String>> getSizeNameLive();

    @Query("SELECT brand FROM Size WHERE isDeleted = 0 AND parentIsDeleted = 0 ORDER BY brand")
    List<String> getSizeBrandList();

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
}
