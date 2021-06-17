package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myfit.data.model.model.Category;
import com.example.myfit.data.tuple.ContentSizeTuple;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.util.CommonUtil;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Dao
public abstract class CategoryDao extends BaseDao<Category> {
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

    @Transaction
    //from addCategory dialog(disposable)
    public void insert(String name, int parentIndex) {
        int sortNumber = getLargestSortNumber() + 1;
        Category category = new Category(parentIndex, sortNumber, name);
        insert(category);
    }

    @Query("SELECT max(sortNumber) FROM Category")
    protected abstract int getLargestSortNumber();

    @Transaction
    //from restore dialog(disposable)
    public List<Long> insertRestoreCategories(@NotNull List<Integer> parentIndexes) {
        String name = "복구됨";

        List<Category> categories = new LinkedList<>();
        int sortNumber = getLargestSortNumber();
        for (int parentIndex : parentIndexes)
            categories.add(new Category(parentIndex, ++sortNumber, name));

        AtomicLong id = new AtomicLong(CommonUtil.createId());
        categories.forEach(category -> category.setId(id.incrementAndGet()));
        return insert(categories);
    }

    //from adapter drag drop
    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    public abstract void updateTuples(List<CategoryTuple> categoryTuples);

    //from addCategory dialog
    @Query("SELECT EXISTS(SELECT name, parentIndex FROM Category WHERE name =:name AND parentIndex=:parentIndex AND deleted = 0)")
    public abstract boolean isExistingName(String name, int parentIndex);
}
