package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
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
public abstract class CategoryDao extends BaseDao {
    @Transaction
    public LiveData<List<Category>> getAllCategoriesLiveWithContentSize() {
        LiveData<List<Category>> allCategoriesLive = getAllCategoriesLive();
        LiveData<List<ContentSizeTuple>> folderContentSizeTuplesLive = getFolderContentSizeTuplesLive();
        LiveData<List<ContentSizeTuple>> sizeContentSizeTuplesLive = getSizeContentSizeTuplesLive();
        MediatorLiveData<List<Category>> mediatorLive = new MediatorLiveData<>();

        mediatorLive.addSource(allCategoriesLive, categories -> {
            if (folderContentSizeTuplesLive.getValue() != null && sizeContentSizeTuplesLive.getValue() != null) {
                setFolderContentSize(categories, folderContentSizeTuplesLive.getValue());
                setSizeContentSize(categories, sizeContentSizeTuplesLive.getValue());
            }
            mediatorLive.setValue(categories);
        });
        mediatorLive.addSource(folderContentSizeTuplesLive, folderContentSizeTuples -> {
            if (allCategoriesLive.getValue() != null && sizeContentSizeTuplesLive.getValue() != null) {
                setFolderContentSize(allCategoriesLive.getValue(), folderContentSizeTuples);
                setSizeContentSize(allCategoriesLive.getValue(), sizeContentSizeTuplesLive.getValue());
                mediatorLive.setValue(allCategoriesLive.getValue());
            }
        });
        mediatorLive.addSource(sizeContentSizeTuplesLive, sizeContentSizeTuples -> {
            if (allCategoriesLive.getValue() != null && folderContentSizeTuplesLive.getValue() != null) {
                setFolderContentSize(allCategoriesLive.getValue(), folderContentSizeTuplesLive.getValue());
                setSizeContentSize(allCategoriesLive.getValue(), sizeContentSizeTuplesLive.getValue());
                mediatorLive.setValue(allCategoriesLive.getValue());
            }
        });

        return mediatorLive;
    }

    @Query("SELECT * FROM Category")
    protected abstract LiveData<List<Category>> getAllCategoriesLive();

    @Query("SELECT category.id AS parentId, SUM(folder.parentId IS NOT NULL AND folder.deleted = 0) AS size FROM Category " +
            "LEFT OUTER JOIN Folder ON folder.parentId = category.id " +
            "GROUP BY category.id")
    protected abstract LiveData<List<ContentSizeTuple>> getFolderContentSizeTuplesLive();

    @Query("SELECT category.id AS parentId, SUM(size.parentId IS NOT NULL AND size.deleted = 0) AS size FROM Category " +
            "LEFT OUTER JOIN Size ON size.parentId = category.id " +
            "GROUP BY category.id")
    protected abstract LiveData<List<ContentSizeTuple>> getSizeContentSizeTuplesLive();

    @Insert
    public abstract List<Long> insert(List<Category> categories);

    @Transaction
    //from addCategory dialog(disposable)
    public void insert(String name, int parentIndex) {
        int sortNumber = this.getLargestSortNumber() + 1;
        Category category = new Category(parentIndex, sortNumber, name);
        this.insert(category);
    }

    @Query("SELECT max(sortNumber) FROM Category")
    protected abstract int getLargestSortNumber();

    @Insert
    protected abstract void insert(Category category);

    @Transaction
    //from restore dialog(disposable)
    public List<Long> insertRestoreCategories(@NotNull List<Integer> parentIndexes) {
        String name = "복구됨";

        List<Category> categories = new LinkedList<>();
        int sortNumber = this.getLargestSortNumber();
        for (int parentIndex : parentIndexes)
            categories.add(new Category(parentIndex, ++sortNumber, name));

        AtomicLong id = new AtomicLong(CommonUtil.createId());
        categories.forEach(category -> category.setId(id.incrementAndGet()));
        return this.insert(categories);
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(Category category);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<Category> categories);

    //from adapter drag drop
    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    public abstract void updateTuples(List<CategoryTuple> categoryTuples);

    //from addCategory dialog
    @Query("SELECT EXISTS(SELECT name, parentIndex FROM Category WHERE name =:name AND parentIndex=:parentIndex AND deleted = 0)")
    public abstract boolean isExistingName(String name, int parentIndex);
}
