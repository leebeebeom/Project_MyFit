package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
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
import com.example.myfit.util.constant.Sort;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@Dao
public abstract class CategoryDao extends BaseDao<CategoryTuple> {

    //to main
    public LiveData<List<List<CategoryTuple>>> getClassifiedTuplesLive() {
        LiveData<List<CategoryTuple>> tuplesLive = this.getTuplesLive();
        LiveData<int[]> contentSizesLive = super.getContentSizesLive(tuplesLive, false);

        return super.getClassifiedTuplesLive(tuplesLive, contentSizesLive);
    }

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime FROM Category WHERE deleted = 0")
    protected abstract LiveData<List<CategoryTuple>> getTuplesLive();

    //to recycleBin
    public LiveData<List<List<CategoryTuple>>> getDeletedClassifiedTuplesLive() {
        LiveData<List<CategoryTuple>> deletedTuplesLive = this.getDeletedTuplesLive();
        LiveData<int[]> contentSizesLive = super.getContentSizesLive(deletedTuplesLive, true);

        return super.getClassifiedTuplesLive(deletedTuplesLive, contentSizesLive);
    }

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime FROM Category WHERE deleted = 1 ORDER BY deletedTime DESC")
    protected abstract LiveData<List<CategoryTuple>> getDeletedTuplesLive();

    //to recycleBin search
    public LiveData<List<List<CategoryTuple>>> getDeletedSearchTuplesLive() {
        LiveData<List<CategoryTuple>> deletedSearchTuplesLive = getDeletedSearchTuplesLive2();
        LiveData<int[]> contentSizesLive = super.getContentSizesLive(deletedSearchTuplesLive, true);

        return super.getClassifiedTuplesLive(deletedSearchTuplesLive, contentSizesLive);
    }

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime FROM Category WHERE deleted = 1 ORDER BY name")
    protected abstract LiveData<List<CategoryTuple>> getDeletedSearchTuplesLive2();

    @Transaction
    //to treeView (disposable)
    public List<CategoryTuple> getTuplesByParentIndex(int parentIndex, Sort sort) {
        List<CategoryTuple> tuples = this.getTuplesByParentIndex(parentIndex);
        long[] ids = super.getTupleIds(tuples);
        int[] contentSizes = getContentSizesByParentIds(ids);
        super.setContentSize(tuples, contentSizes);
        super.sortTuples(sort, tuples);
        return tuples;
    }

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime FROM Category WHERE parentIndex = :parentIndex AND deleted = 0")
    protected abstract List<CategoryTuple> getTuplesByParentIndex(int parentIndex);

    //to listFragment
    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime FROM Category WHERE id = :id")
    public abstract LiveData<CategoryTuple> getTupleLiveById(long id);

    @Transaction
    //to treeView(disposable)
    public CategoryTuple getTupleById(long id) {
        CategoryTuple tuple = this.getTupleById2(id);
        int contentSize = getContentSizeByParentId(id);
        tuple.setContentSize(contentSize);
        return tuple;
    }

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime FROM Category WHERE id = :id")
    protected abstract CategoryTuple getTupleById2(long id);

    @Transaction
    //from addCategory dialog(disposable)
    public long insert(String name, int parentIndex) {
        int sortNumber = this.getLargestSortNumber() + 1;
        Category category = ModelFactory.makeCategory(name, parentIndex, sortNumber);
        return this.insert(category);
    }

    @Query("SELECT max(sortNumber) FROM Category")
    protected abstract int getLargestSortNumber();

    @Insert
    protected abstract long insert(Category category);

    @Insert
    //from appDateBase
    public abstract long[] insert(Category[] categories);

    @Transaction
    //from restore dialog(disposable)
    public long[] insertRestoreCategories(@NotNull int[] parentIndex) {
        String name = "복구됨";
        int sortNumber = this.getLargestSortNumber() + 1;

        int count = parentIndex.length;
        Category[] categories = new Category[count];
        for (int i = 0; i < count; i++) {
            categories[i] = ModelFactory.makeCategory(name, parentIndex[i], sortNumber);
            sortNumber++;
        }
        return this.insert(categories);
    }

    @Transaction
    //from nameEdit dialog
    public void update(long id, String name) {
        CategoryTuple tuple = this.getTupleById2(id);
        tuple.setName(name);
        this.update(tuple);
    }

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    protected abstract void update(CategoryTuple categoryTuple);

    //from adapter drag drop
    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    public abstract void update(List<CategoryTuple> categoryTuples);

    @Transaction
    //from delete dialog, restore dialog
    public void deleteOrRestore(long[] ids) {
        DeletedTuple[] deletedTuples = this.getDeletedTuplesByIds(ids);
        super.setDeletedTuples(deletedTuples);
        this.update(deletedTuples);

        this.setChildrenParentDeleted(ids);
    }

    @Query("SELECT id, deleted, deletedTime FROM Category WHERE id IN (:ids)")
    protected abstract DeletedTuple[] getDeletedTuplesByIds(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    protected abstract void update(DeletedTuple[] deletedTuples);

    //from addCategory dialog
    @Query("SELECT EXISTS(SELECT name, parentIndex FROM Category WHERE name =:name AND parentIndex=:parentIndex AND deleted = 0)")
    public abstract boolean isExistingName(String name, int parentIndex);

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime FROM Category WHERE id IN (:ids)")
    public abstract CategoryTuple[] getTuplesByIds(long[] ids);
}
