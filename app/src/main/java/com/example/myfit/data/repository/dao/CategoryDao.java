package com.example.myfit.data.repository.dao;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myfit.R;
import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.ModelFactory;
import com.example.myfit.data.model.category.Category;
import com.example.myfit.data.model.tuple.CategoryFolderTuple;
import com.example.myfit.data.model.tuple.DeletedTuple;
import com.example.myfit.data.model.tuple.ParentDeletedTuple;
import com.example.myfit.util.constant.SortValue;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

@Dao
public abstract class CategoryDao extends BaseDao<Category, CategoryFolderTuple> {
    private FolderDao folderDao;
    private SizeDao sizeDao;
    private final AppDataBase appDataBase;
    private final Context context;

    @Inject
    public CategoryDao(Context context) {
        this.context = context;
        this.appDataBase = AppDataBase.getsInstance(context);
    }

    //to main
    public LiveData<List<List<CategoryFolderTuple>>> getClassifiedTuplesLive(int sort) {
        LiveData<List<CategoryFolderTuple>> tuplesLive = this.getTuplesLive(false);
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(tuplesLive, false);

        return super.getClassifiedTuplesLive(tuplesLive, contentsSizesLive, sort);
    }

    //to recycleBin
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedClassifiedTuplesLive() {
        LiveData<List<CategoryFolderTuple>> deletedTuplesLive = this.getTuplesLive(true);
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(deletedTuplesLive, true);

        return super.getClassifiedTuplesLive(deletedTuplesLive, contentsSizesLive, SortValue.SORT_DELETED.getValue());
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Category WHERE isDeleted = :isDeleted")
    protected abstract LiveData<List<CategoryFolderTuple>> getTuplesLive(boolean isDeleted);

    //to recycleBin search (maybe searchView?)
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedSearchTuplesLive(String keyWord) {
        LiveData<List<CategoryFolderTuple>> deletedSearchTuplesLive = getSearchTuplesLive(keyWord, true);
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(deletedSearchTuplesLive, true);

        return super.getClassifiedTuplesLive(deletedSearchTuplesLive, contentsSizesLive, SortValue.SORT_NAME.getValue());
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Category WHERE isDeleted = :isDeleted AND name LIKE :keyWord")
    protected abstract LiveData<List<CategoryFolderTuple>> getSearchTuplesLive(String keyWord, boolean isDeleted);


    @Transaction
    //to treeView (disposable)
    public List<CategoryFolderTuple> getTuplesByParentIndex(byte parentIndex, int sort) {
        List<CategoryFolderTuple> categoryTuples = this.getTuplesByParentIndex(parentIndex);
        long[] categoryIds = super.getItemIds(categoryTuples);
        int[] contentsSizes = getContentsSizesByParentIds(categoryIds);
        super.setContentsSize(categoryTuples, contentsSizes);
        super.orderTuples(sort, categoryTuples);
        return categoryTuples;
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Category WHERE parentIndex = :parentIndex AND isDeleted = 0")
    protected abstract List<CategoryFolderTuple> getTuplesByParentIndex(byte parentIndex);


    @Transaction
    //to treeView
    public CategoryFolderTuple getTupleById(long id) {
        CategoryFolderTuple categoryTuple = this.getTupleById2(id);
        int contentsSize = getContentsSizeByParentId(id);
        categoryTuple.setContentsSize(contentsSize);
        return categoryTuple;
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Category WHERE id = :id AND isDeleted = 0")
    protected abstract CategoryFolderTuple getTupleById2(long id);

    @Transaction
    //from addCategory Dialog
    public long insertCategory(String categoryName, byte parentIndex) {
        int orderNumber = this.getLargestOrderNumber() + 1;
        Category category = ModelFactory.makeCategory(categoryName, parentIndex, orderNumber);
        return insert(category);
    }

    @Query("SELECT max(orderNumber) FROM Category")
    protected abstract int getLargestOrderNumber();

    @Transaction
    //from restore Dialog
    public Long[] insertRestoreCategories(@NotNull byte[] parentIndex) {
        String restoreCategoryName = context.getString(R.string.restore_category_name);
        int orderNumber = this.getLargestOrderNumber() + 1;

        int count = parentIndex.length;
        Category[] categories = new Category[count];
        for (int i = 0; i < count; i++) {
            categories[i] = ModelFactory.makeCategory(restoreCategoryName, parentIndex[i], orderNumber);
            orderNumber++;
        }
        return this.insert(categories);
    }

    @Insert
    protected abstract Long[] insert(Category[] categories);

    @Transaction
    //from nameEdit dialog
    public void updateCategory(long id, String name) {
        CategoryFolderTuple categoryTuple = this.getTupleById2(id);
        categoryTuple.setName(name);
        this.updateTuple(categoryTuple);
    }

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    protected abstract void updateTuple(CategoryFolderTuple categoryTuple);

    @Transaction
    //from delete dialog, restore dialog
    public void deleteOrRestoreCategories(long[] categoryIds, boolean isDeleted) {
        DeletedTuple[] categoryDeletedTuples = this.getDeletedTuplesByIds(categoryIds);
        super.setDeletedTuples(categoryDeletedTuples, isDeleted);
        this.updateDeletedTuples(categoryDeletedTuples);

        this.setChildrenParentDeleted(categoryIds, isDeleted);
    }

    @Query("SELECT id, isDeleted FROM Category WHERE id IN (:ids)")
    protected abstract DeletedTuple[] getDeletedTuplesByIds(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    protected abstract void updateDeletedTuples(DeletedTuple[] deletedTuples);

    private void setChildrenParentDeleted(long[] categoryIds, boolean isDeleted) {
        if (folderDao == null) folderDao = appDataBase.folderDao();
        if (sizeDao == null) sizeDao = appDataBase.sizeDao();

        LinkedList<ParentDeletedTuple> allFolderParentDeletedTuples = new LinkedList<>();
        this.addAllChildFolderParentDeletedTuples(categoryIds, allFolderParentDeletedTuples);

        LinkedList<ParentDeletedTuple> allSizeParentDeletedTuples = new LinkedList<>(sizeDao.getParentDeletedTuplesByParentIds(categoryIds));
        long[] allFolderIds = allFolderParentDeletedTuples.stream()
                .filter(parentDeletedTuple -> !parentDeletedTuple.isDeleted())
                .mapToLong(ParentDeletedTuple::getId).toArray();
        //categoryChildSizesParentDeletedTuples -> allChildSizeParentDeletedTuples
        this.addAllChildSizeParentDeletedTuples(allFolderIds, allSizeParentDeletedTuples);

        super.setParentDeletedTuples(allFolderParentDeletedTuples, isDeleted);
        super.setParentDeletedTuples(allSizeParentDeletedTuples, isDeleted);
        folderDao.updateParentDeletedTuples(allFolderParentDeletedTuples);
        sizeDao.updateParentDeletedTuples(allSizeParentDeletedTuples);
    }

    private void addAllChildFolderParentDeletedTuples(long[] parentIds, @NotNull LinkedList<ParentDeletedTuple> allParentDeletedTuples) {
        List<ParentDeletedTuple> childFolderParentDeletedTuples = folderDao.getParentDeletedTuplesByParentIds(parentIds);
        if (!childFolderParentDeletedTuples.isEmpty()) {
            allParentDeletedTuples.addAll(childFolderParentDeletedTuples);
            long[] childFolderIds = childFolderParentDeletedTuples.stream()
                    .filter(parentDeletedTuple -> !parentDeletedTuple.isDeleted())
                    .mapToLong(ParentDeletedTuple::getId).toArray();
            addAllChildFolderParentDeletedTuples(childFolderIds, allParentDeletedTuples);
        }
    }

    private void addAllChildSizeParentDeletedTuples(long[] parentIds, @NotNull LinkedList<ParentDeletedTuple> allParentDeletedTuples) {
        allParentDeletedTuples.addAll(sizeDao.getParentDeletedTuplesByParentIds(parentIds));
    }

    //from addCategory dialog
    @Query("SELECT EXISTS(SELECT name, parentIndex FROM Category WHERE name =:categoryName AND parentIndex=:parentIndex AND isDeleted = 0)")
    public abstract boolean isExistingCategoryName(String categoryName, byte parentIndex);

    //from adapter drag drop
    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    public abstract void updateTuples(LinkedList<CategoryFolderTuple> categoryTuples);

    public interface CategoryDaoInterFace {
        LiveData<List<List<CategoryFolderTuple>>> getClassifiedCategoryTuplesLive();

        LiveData<List<List<CategoryFolderTuple>>> getDeletedClassifiedCategoryTuplesLive();

        LiveData<List<List<CategoryFolderTuple>>> getSearchCategoryTuplesList(String keyWord);

        LiveData<List<CategoryFolderTuple>> getCategoryTuplesByParentIndex(byte parentIndex);

        LiveData<CategoryFolderTuple> getCategoryTupleById(long id);

        LiveData<Long> insertCategory(String categoryName, byte parentIndex);

        LiveData<Long[]> insertRestoreCategories(@NotNull byte[] parentIndex);

        void updateCategory(long id, String name);

        void updateCategories(LinkedList<CategoryFolderTuple> categoryTuples);

        void deleteOrRestoreCategories(long[] categoryIds, boolean isDeleted);

        LiveData<Boolean> isExistingCategoryName(String categoryName, byte parentIndex);
    }
}
