package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myfit.data.model.Size;

import java.util.List;

@Dao
public interface SizeDao {
    @Query("SELECT * FROM size WHERE isDeleted = 0 AND isParentDeleted = 0 ")
    LiveData<List<Size>> getSizesLive();

    @Query("SELECT * FROM Size WHERE parentId = :parentId AND isDeleted = 0 AND isParentDeleted = 0")
    LiveData<List<Size>> getSizesLiveByParentId(long parentId);

    @Query("SELECT * FROM Size WHERE isDeleted = 0 AND isParentDeleted = 0")
    List<Size> getSizes();

    @Query("SELECT * FROM Size WHERE parentId = :parentId AND isDeleted = 0 AND isParentDeleted = 0")
    List<Size> getSizesByParentId(long parentId);

    @Query("SELECT parentId FROM Size WHERE isDeleted = 0 AND isParentDeleted = 0")
    List<Long> getSizeParentIds();

    @Query("SELECT parentId FROM Size WHERE parentCategoryIndex = :parentCategoryIndex AND isDeleted = 0 AND isParentDeleted = 0")
    List<Long> getSizeParentIdsByParentCategory(int parentCategoryIndex);

    @Query("SELECT brand FROM Size WHERE isDeleted = 0 AND isParentDeleted = 0")
    LiveData<List<String>> getSizeBrandsLive();

    @Query("SELECT name FROM Size WHERE isDeleted = 0 AND isParentDeleted = 0")
    LiveData<List<String>> getSizeNamesLive();

    @Query("SELECT brand FROM Size WHERE isDeleted = 0 AND isParentDeleted = 0")
    List<String> getSizeBrands();

    @Query("SELECT * FROM Size WHERE id = :id")
    Size getSizeById(long id);

    @Query("SELECT max(orderNumber) FROM Size")
    int getSizeLargestOrder();

    @Insert
    long insertSize(Size size);

    @Update
    void updateSize(Size size);

    @Update
    void updateSize(List<Size> sizes);
}
