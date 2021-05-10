package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myfit.data.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM Category WHERE isDeleted = 0")
    LiveData<List<Category>> getCategoriesLive();

    @Query("SELECT * FROM Category WHERE id = :id")
    LiveData<Category> getCategoryLiveById(long id);

    @Query("SELECT * FROM Category WHERE isDeleted = 0")
    List<Category> getCategories();

    @Query("SELECT * FROM Category WHERE parentCategoryIndex = :parentCategoryIndex AND isDeleted = 0")
    List<Category> getCategoriesByParentCategory(int parentCategoryIndex);

    @Query("SELECT * FROM Category WHERE id IN (:categoryIdAs) AND isDeleted = 0")
    List<Category> getCategoriesByIds(long[] categoryIdAs);

    @Query("SELECT categoryName FROM Category WHERE isDeleted = 0")
    LiveData<List<String>> getCategoryNamesLive();

    @Query("SELECT categoryName FROM Category WHERE parentCategoryIndex = :parentCategoryIndex AND isDeleted = 0 ")
    List<String> getCategoryNamesByParentCategory(int parentCategoryIndex);

    @Query("SELECT * FROM Category WHERE id = :id AND isDeleted = 0")
    Category getCategoryById(long id);

    @Query("SELECT * FROM category WHERE categoryName = :categoryName AND parentCategoryIndex = :parentCategoryIndex AND isDeleted = 0")
    Category getCategoryByNameAndParentCategory(String categoryName, int parentCategoryIndex);

    @Query("SELECT max(orderNumber) FROM Category")
    int getCategoryLargestOrder();

    @Insert
    long insertCategory(Category category);

    @Insert
    List<Long> insertCategory(List<Category> categories);

    @Update
    void updateCategory(Category category);

    @Update
    void updateCategory(List<Category> categories);
}
