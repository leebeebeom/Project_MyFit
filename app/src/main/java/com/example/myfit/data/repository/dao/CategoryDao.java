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
import com.example.myfit.util.constant.SortValue;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

@Dao
public abstract class CategoryDao extends BaseDao<Category, CategoryFolderTuple> {

    //to main
    public LiveData<List<List<CategoryFolderTuple>>> getClassifiedTuplesLive(int sort) {
        LiveData<List<CategoryFolderTuple>> tuplesLive = this.getTuplesLive();
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(tuplesLive, false);

        return super.getClassifiedTuplesLive(tuplesLive, contentsSizesLive, sort);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Category WHERE isDeleted = 0")
    protected abstract LiveData<List<CategoryFolderTuple>> getTuplesLive();

    //to recycleBin
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedClassifiedTuplesLive() {
        LiveData<List<CategoryFolderTuple>> deletedTuplesLive = this.getDeletedTuplesLive();
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(deletedTuplesLive, true);

        return super.getClassifiedTuplesLive(deletedTuplesLive, contentsSizesLive);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Category WHERE isDeleted = 1 ORDER BY deletedTime DESC")
    protected abstract LiveData<List<CategoryFolderTuple>> getDeletedTuplesLive();

    //to recycleBin search
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedSearchTuplesLive(String keyWord) {
        LiveData<List<CategoryFolderTuple>> deletedSearchTuplesLive = getSearchTuplesLive(keyWord, true);
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(deletedSearchTuplesLive, true);

        return super.getClassifiedTuplesLive(deletedSearchTuplesLive, contentsSizesLive, SortValue.SORT_NAME.getValue());
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Category WHERE isDeleted = :isDeleted AND name LIKE :keyWord ORDER BY name")
    protected abstract LiveData<List<CategoryFolderTuple>> getSearchTuplesLive(String keyWord, boolean isDeleted);

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

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Category WHERE parentIndex = :parentIndex AND isDeleted = 0")
    protected abstract List<CategoryFolderTuple> getTuplesByParentIndex(byte parentIndex);

    @Transaction
    //to treeView
    public CategoryFolderTuple getTupleById(long id) {
        CategoryFolderTuple tuple = this.getTupleById2(id);
        int contentsSize = getContentsSizeByParentId(id);
        tuple.setContentsSize(contentsSize);
        return tuple;
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Category WHERE id = :id AND isDeleted = 0")
    protected abstract CategoryFolderTuple getTupleById2(long id);

    @Transaction
    //from addCategory Dialog
    public long insert(String categoryName, byte parentIndex) {
        int orderNumber = this.getLargestOrderNumber() + 1;
        Category category = ModelFactory.makeCategory(categoryName, parentIndex, orderNumber);
        return insert(category);
    }

    @Query("SELECT max(orderNumber) FROM Category")
    protected abstract int getLargestOrderNumber();

    @Transaction
    //from restore Dialog
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

    @Insert
    protected abstract Long[] insert(Category[] categories);

    @Transaction
    //from nameEdit dialog
    public void update(long id, String name) {
        CategoryFolderTuple tuple = this.getTupleById2(id);
        tuple.setName(name);
        this.update(tuple);
    }

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    protected abstract void update(CategoryFolderTuple categoryTuple);

    @Transaction
    //from delete dialog, restore dialog
    public void deleteOrRestore(long[] categoryIds, boolean isDeleted) {
        DeletedTuple[] deletedTuples = this.getDeletedTuplesByIds(categoryIds);
        super.setDeletedTuples(deletedTuples, isDeleted);
        this.update(deletedTuples);

        this.setChildrenParentDeleted(categoryIds, isDeleted);
    }

    @Query("SELECT id, isDeleted FROM Category WHERE id IN (:ids)")
    protected abstract DeletedTuple[] getDeletedTuplesByIds(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    protected abstract void update(DeletedTuple[] deletedTuples);

    //from addCategory dialog
    @Query("SELECT EXISTS(SELECT name, parentIndex FROM Category WHERE name =:categoryName AND parentIndex=:parentIndex AND isDeleted = 0)")
    public abstract boolean isExistingName(String categoryName, byte parentIndex);

    //from adapter drag drop
    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    public abstract void update(LinkedList<CategoryFolderTuple> categoryTuples);
}
