package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myfit.data.model.ModelFactory;
import com.example.myfit.data.model.category.Category;
import com.example.myfit.data.model.tuple.CategoryFolderTuple;
import com.example.myfit.data.model.tuple.DeletedTuple;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

@Dao
public abstract class CategoryDao extends BaseDao<CategoryFolderTuple> {

    //to main
    public LiveData<List<List<CategoryFolderTuple>>> getClassifiedTuplesLive(int sort) {
        LiveData<List<CategoryFolderTuple>> tuplesLive = this.getTuplesLive();
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(tuplesLive, false);

        return super.getClassifiedTuplesLive(tuplesLive, contentsSizesLive, sort);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize, deletedTime FROM Category WHERE isDeleted = 0")
    protected abstract LiveData<List<CategoryFolderTuple>> getTuplesLive();

    //to recycleBin
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedClassifiedTuplesLive() {
        LiveData<List<CategoryFolderTuple>> deletedTuplesLive = this.getDeletedTuplesLive();
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(deletedTuplesLive, true);

        return super.getClassifiedTuplesLive(deletedTuplesLive, contentsSizesLive);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize, deletedTime FROM Category WHERE isDeleted = 1 ORDER BY deletedTime DESC")
    protected abstract LiveData<List<CategoryFolderTuple>> getDeletedTuplesLive();

    //to recycleBin search
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedSearchTuplesLive() {
        LiveData<List<CategoryFolderTuple>> deletedSearchTuplesLive = getDeletedSearchTuplesLive2();
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(deletedSearchTuplesLive, true);

        return super.getClassifiedTuplesLive(deletedSearchTuplesLive, contentsSizesLive);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize, deletedTime FROM Category WHERE isDeleted = 1 AND name ORDER BY name")
    protected abstract LiveData<List<CategoryFolderTuple>> getDeletedSearchTuplesLive2();

    @Transaction
    //to treeView (disposable)
    public List<CategoryFolderTuple> getTuplesByParentIndex(byte parentIndex, int sort) {
        List<CategoryFolderTuple> tuples = this.getTuplesByParentIndex(parentIndex);
        long[] ids = super.getItemIds(tuples);
        int[] contentsSizes = getContentsSizesByParentIds(ids);
        super.setContentsSize(tuples, contentsSizes);
        super.orderTuples(sort, tuples);
        return tuples;
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize, deletedTime FROM Category WHERE parentIndex = :parentIndex AND isDeleted = 0")
    protected abstract List<CategoryFolderTuple> getTuplesByParentIndex(byte parentIndex);

    @Transaction
    //to treeView(disposable)
    public CategoryFolderTuple getTupleById(long id) {
        CategoryFolderTuple tuple = this.getTupleById2(id);
        int contentsSize = getContentsSizeByParentId(id);
        tuple.setContentsSize(contentsSize);
        return tuple;
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize, deletedTime FROM Category WHERE id = :id AND isDeleted = 0")
    protected abstract CategoryFolderTuple getTupleById2(long id);

    @Transaction
    //from addCategory dialog(disposable)
    public long insert(String name, byte parentIndex) {
        int orderNumber = this.getLargestOrderNumber() + 1;
        Category category = ModelFactory.makeCategory(name, parentIndex, orderNumber);
        return this.insert(category);
    }

    @Query("SELECT max(orderNumber) FROM Category")
    protected abstract int getLargestOrderNumber();

    @Insert
    protected abstract long insert(Category category);

    @Insert
    //from appDateBase
    public abstract Long[] insert(Category[] categories);

    @Transaction
    //from restore dialog(disposable)
    public Long[] insertRestoreCategories(@NotNull byte[] parentIndex) {
        String name = "복구됨";
        int orderNumber = this.getLargestOrderNumber() + 1;

        int count = parentIndex.length;
        Category[] categories = new Category[count];
        for (int i = 0; i < count; i++) {
            categories[i] = ModelFactory.makeCategory(name, parentIndex[i], orderNumber);
            orderNumber++;
        }
        return this.insert(categories);
    }

    @Transaction
    //from nameEdit dialog
    public void update(long id, String name) {
        CategoryFolderTuple tuple = this.getTupleById2(id);
        tuple.setName(name);
        this.update(tuple);
    }

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    protected abstract void update(CategoryFolderTuple categoryTuple);

    //from adapter drag drop
    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    public abstract void update(LinkedList<CategoryFolderTuple> categoryTuples);

    @Transaction
    //from delete dialog, restore dialog
    public void deleteOrRestore(long[] ids, boolean isDeleted) {
        DeletedTuple[] deletedTuples = this.getDeletedTuplesByIds(ids);
        super.setDeletedTuples(deletedTuples, isDeleted);
        this.update(deletedTuples);

        this.setChildrenParentDeleted(ids, isDeleted);
    }

    @Query("SELECT id, isDeleted, deletedTime FROM Category WHERE id IN (:ids)")
    protected abstract DeletedTuple[] getDeletedTuplesByIds(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    protected abstract void update(DeletedTuple[] deletedTuples);

    //from addCategory dialog
    @Query("SELECT EXISTS(SELECT name, parentIndex FROM Category WHERE name =:name AND parentIndex=:parentIndex AND isDeleted = 0)")
    public abstract boolean isExistingName(String name, byte parentIndex);
}
