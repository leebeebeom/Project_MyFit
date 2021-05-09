package com.example.project_myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.project_myfit.data.model.Size;

import java.util.List;

@Dao
public interface SizeDao {
    @Query("SELECT * FROM size WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted ")
    LiveData<Size[]> getSizesLive(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Size WHERE parentId = :parentId AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    LiveData<Size[]> getSizesLive(long parentId, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Size WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    Size[] getSizes(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Size WHERE parentId = :parentId AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    Size[] getSizes(long parentId, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT parentId FROM Size WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    long[] getSizeParentIds(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT parentId FROM Size WHERE parentCategoryIndex = :parentCategoryIndex AND isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    long[] getSizeParentIds(int parentCategoryIndex, boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT brand FROM Size WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    LiveData<String[]> getSizeBrandsLive(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT name FROM Size WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    LiveData<String[]> getSizeNamesLive(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT brand FROM Size WHERE isDeleted = :isDeleted AND isParentDeleted = :isParentDeleted")
    String[] getSizeBrands(boolean isDeleted, boolean isParentDeleted);

    @Query("SELECT * FROM Size WHERE id = :id")
    Size getSize(long id);

    @Query("SELECT max(orderNumber) FROM Size")
    int getSizeLargestOrder();

    @Insert
    long insertSize(Size size);

    @Update
    void updateSize(Size size);

    @Update
    void updateSize(Size[] sizes);

    @Update
    void updateSize(List<Size> sizes);
}
