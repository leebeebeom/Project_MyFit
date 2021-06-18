package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myfit.data.model.model.Category;
import com.example.myfit.data.tuple.ContentSizeTuple;
import com.example.myfit.data.tuple.tuple.CategoryTuple;

import java.util.List;

@Dao
public abstract class CategoryDao extends BaseCategoryFolderDao<Category> {
    @Override
    protected LiveData<List<Category>> getAllModelsLive() {
        return getAllCategoriesLive();
    }

    @Override
    protected LiveData<List<ContentSizeTuple>> getFolderContentSizeTuplesLive() {
        return getFolderContentSizeTuplesLiveImpl();
    }

    @Override
    protected LiveData<List<ContentSizeTuple>> getSizeContentSizeTuplesLive() {
        return getSizeContentSizeTuplesLiveImpl();
    }

    @Query("SELECT * FROM Category")
    protected abstract LiveData<List<Category>> getAllCategoriesLive();

    @Query("SELECT category.id AS parentId, SUM(folder.parentId IS NOT NULL AND folder.deleted = 0) AS size FROM Category " +
            "LEFT OUTER JOIN Folder ON folder.parentId = category.id " +
            "GROUP BY category.id")
    protected abstract LiveData<List<ContentSizeTuple>> getFolderContentSizeTuplesLiveImpl();

    @Query("SELECT category.id AS parentId, SUM(size.parentId IS NOT NULL AND size.deleted = 0) AS size FROM Category " +
            "LEFT OUTER JOIN Size ON size.parentId = category.id " +
            "GROUP BY category.id")
    protected abstract LiveData<List<ContentSizeTuple>> getSizeContentSizeTuplesLiveImpl();

    //from adapter drag drop
    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    public abstract void updateTuples(List<CategoryTuple> categoryTuples);
}
