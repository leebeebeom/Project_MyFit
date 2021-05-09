package com.example.project_myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.project_myfit.data.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM Category WHERE isDeleted = :isDeleted")
    LiveData<Category[]> getCategoriesLive(boolean isDeleted);

    @Query("SELECT * FROM Category WHERE id = :id")
    LiveData<Category> getCategoriesLive(long id);

    @Query("SELECT * FROM Category WHERE isDeleted = :isDeleted")
    Category[] getCategories(boolean isDeleted);

    @Query("SELECT * FROM Category WHERE parentCategoryIndex = :parentCategoryIndex AND isDeleted = :isDeleted")
    Category[] getCategories(int parentCategoryIndex, boolean isDeleted);

    @Query("SELECT * FROM Category WHERE id IN (:categoryIdArray) AND isDeleted =:isDeleted")
    List<Category> getCategories(long[] categoryIdArray, boolean isDeleted);

    @Query("SELECT categoryName FROM Category WHERE isDeleted = :isDeleted")
    LiveData<List<String>> getCategoryNameLive(boolean isDeleted);

    @Query("SELECT categoryName FROM Category WHERE isDeleted = :isDeleted AND parentCategoryIndex = :parentCategoryIndex")
    String[] getCategoryNames(int parentCategoryIndex, boolean isDeleted);

    @Query("SELECT * FROM Category WHERE id = :id AND isDeleted = :isDeleted")
    Category getCategory(long id, boolean isDeleted);

    @Query("SELECT * FROM category WHERE categoryName = :categoryName AND parentCategoryIndex = :parentCategoryIndex AND isDeleted = :isDeleted")
    Category getCategory(String categoryName, int parentCategoryIndex, boolean isDeleted);

    @Query("SELECT max(orderNumber) FROM Category")
    int getCategoryLargestOrder();

    @Insert
    long insertCategory(Category category);

    @Insert
    long insertCategory(List<Category> categoryList);

    @Insert
    long insertCategory(Category[] categoryArray);

    @Update
    void updateCategory(Category category);

    @Update
    void updateCategory(List<Category> categoryList);

    @Update
    void updateCategory(Category[] categoryArray);
}
