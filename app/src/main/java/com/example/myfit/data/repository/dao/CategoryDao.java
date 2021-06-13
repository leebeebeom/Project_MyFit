package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myfit.data.model.ModelFactory;
import com.example.myfit.data.model.model.Category;
import com.example.myfit.data.tuple.DeletedTuple;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.util.CommonUtil;
import com.example.myfit.util.constant.Sort;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Dao
public abstract class CategoryDao extends BaseDao<CategoryTuple> {

    //to main
    public LiveData<List<List<CategoryTuple>>> getClassifiedTuplesLive() {
        LiveData<List<CategoryTuple>> tuplesLive = this.getTuplesLive();
        LiveData<List<Integer>> contentSizesLive = this.getCategoryContentSizesLive(tuplesLive);

        return super.getClassifiedTuplesLive(tuplesLive, contentSizesLive);
    }

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime FROM Category WHERE deleted = 0")
    protected abstract LiveData<List<CategoryTuple>> getTuplesLive();

    @NotNull
    protected LiveData<List<Integer>> getCategoryContentSizesLive(LiveData<List<CategoryTuple>> tuplesLive) {
        return Transformations.switchMap(tuplesLive, tuples -> {
            List<Long> itemIds = getTupleIds(tuples);
            return getCategoryContentSizesLive(itemIds);
        });
    }

    @Query("SELECT SUM((folder.parentId IS NOT NULL AND folder.deleted = 0) + (size.parentId IS NOT NULL AND size.deleted = 0)) FROM Category " +
            "LEFT OUTER JOIN Folder ON category.id = folder.parentId " +
            "LEFT OUTER JOIN Size ON category.id = size.parentId " +
            "WHERE category.id IN (:ids) " +
            "GROUP BY category.id")
    protected abstract LiveData<List<Integer>> getCategoryContentSizesLive(List<Long> ids);

    //to recycleBin
    public LiveData<List<List<CategoryTuple>>> getDeletedClassifiedTuplesLive() {
        LiveData<List<CategoryTuple>> deletedTuplesLive = this.getDeletedTuplesLive();
        LiveData<List<Integer>> contentSizesLive = this.getCategoryContentSizesLive(deletedTuplesLive);

        return super.getClassifiedTuplesLive(deletedTuplesLive, contentSizesLive);
    }

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime FROM Category WHERE deleted = 1 ORDER BY deletedTime DESC")
    protected abstract LiveData<List<CategoryTuple>> getDeletedTuplesLive();

    //to recycleBin search
    public LiveData<List<List<CategoryTuple>>> getDeletedSearchTuplesLive() {
        LiveData<List<CategoryTuple>> deletedSearchTuplesLive = getDeletedSearchTuplesLive2();
        LiveData<List<Integer>> contentSizesLive = this.getCategoryContentSizesLive(deletedSearchTuplesLive);

        return super.getClassifiedTuplesLive(deletedSearchTuplesLive, contentSizesLive);
    }

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime FROM Category WHERE deleted = 1 ORDER BY name")
    protected abstract LiveData<List<CategoryTuple>> getDeletedSearchTuplesLive2();

    @Transaction
    //to treeView (disposable)
    public List<CategoryTuple> getTuplesByParentIndex(int parentIndex, Sort sort) {
        List<CategoryTuple> tuples = this.getTuplesByParentIndex(parentIndex);
        List<Long> ids = super.getTupleIds(tuples);
        List<Integer> contentSizes = getCategoryContentSizes(ids);
        super.setContentSize(tuples, contentSizes);
        super.sortTuples(sort, tuples);
        return tuples;
    }

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime FROM Category WHERE parentIndex = :parentIndex AND deleted = 0")
    protected abstract List<CategoryTuple> getTuplesByParentIndex(int parentIndex);

    @Query("SELECT SUM((folder.parentId IS NOT NULL AND folder.deleted = 0) + (size.parentId IS NOT NULL AND size.deleted = 0)) FROM Category " +
            "LEFT OUTER JOIN Folder ON category.id = folder.parentId " +
            "LEFT OUTER JOIN Size ON category.id = size.parentId " +
            "WHERE category.id IN (:ids) " +
            "GROUP BY category.id")
    protected abstract List<Integer> getCategoryContentSizes(List<Long> ids);

    //to listFragment
    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime FROM Category WHERE id = :id")
    public abstract LiveData<CategoryTuple> getTupleLiveById(long id);

    @Transaction
    //to treeView(disposable)
    public CategoryTuple getTupleById(long id) {
        CategoryTuple tuple = this.getTupleById2(id);
        int contentSize = getCategoryContentSize(id);
        tuple.setContentSize(contentSize);
        return tuple;
    }

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime FROM Category WHERE id = :id")
    protected abstract CategoryTuple getTupleById2(long id);

    @Query("SELECT SUM((folder.parentId IS NOT NULL AND folder.deleted = 0) + (size.parentId IS NOT NULL AND size.deleted = 0)) FROM Category " +
            "LEFT OUTER JOIN Folder ON category.id = folder.parentId " +
            "LEFT OUTER JOIN Size ON category.id = size.parentId " +
            "WHERE category.id = :id " +
            "GROUP BY category.id")
    protected abstract int getCategoryContentSize(long id);

    @Transaction
    //from addCategory dialog(disposable)
    public long insert(String name, int parentIndex) {
        int sortNumber = this.getLargestSortNumber() + 1;
        Category category = ModelFactory.makeCategory(name.trim(), parentIndex, sortNumber);
        return this.insert(category);
    }

    @Query("SELECT max(sortNumber) FROM Category")
    protected abstract int getLargestSortNumber();

    @Insert
    protected abstract long insert(Category category);

    @Insert
    //from appDateBase
    public abstract List<Long> insert(List<Category> categories);

    @Transaction
    //from restore dialog(disposable)
    public List<Long> insertRestoreCategories(@NotNull List<Integer> parentIndex) {
        String name = "복구됨";
        int sortNumber = this.getLargestSortNumber() + 1;

        List<Category> categories = new LinkedList<>();
        AtomicLong id = new AtomicLong(CommonUtil.getCurrentDate());
        for (int index : parentIndex) {
            categories.add(ModelFactory.makeCategory(name, index, sortNumber));
            sortNumber++;
        }
        categories.forEach(category -> category.setId(id.incrementAndGet()));
        return this.insert(categories);
    }

    @Transaction
    //from nameEdit dialog
    public void update(long id, String name) {
        CategoryTuple tuple = this.getTupleById2(id);
        tuple.setName(name.trim());
        this.update(tuple);
    }

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    protected abstract void update(CategoryTuple categoryTuple);

    //from adapter drag drop
    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    public abstract void updateTuples(List<CategoryTuple> categoryTuples);

    @Transaction
    //from delete dialog, restore dialog
    public void deleteOrRestore(List<Long> ids) {
        List<DeletedTuple> deletedTuples = this.getDeletedTuplesByIds(ids);
        super.setDeletedTuples(deletedTuples);
        this.updateDeletedTuples(deletedTuples);

        this.setChildrenParentDeleted(ids);
    }

    @Query("SELECT id, deleted, deletedTime FROM Category WHERE id IN (:ids)")
    protected abstract List<DeletedTuple> getDeletedTuplesByIds(List<Long> ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    public abstract void updateDeletedTuples(List<DeletedTuple> deletedTuples);

    //from addCategory dialog
    @Query("SELECT EXISTS(SELECT name, parentIndex FROM Category WHERE name =:name AND parentIndex=:parentIndex AND deleted = 0)")
    public abstract boolean isExistingName(String name, int parentIndex);

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime FROM Category WHERE id IN (:ids)")
    public abstract List<CategoryTuple> getTuplesByIds(List<Long> ids);
}
