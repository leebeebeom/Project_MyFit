package com.example.myfit.data.repository.dao;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myfit.R;
import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.ModelFactory;
import com.example.myfit.data.model.category.Category;
import com.example.myfit.data.model.category.CategoryDeletedRelation;
import com.example.myfit.data.model.folder.FolderDeletedRelation;
import com.example.myfit.data.model.tuple.CategoryFolderTuple;
import com.example.myfit.data.model.tuple.DeletedTuple;
import com.example.myfit.data.model.tuple.ParentDeletedTuple;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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

    //to main, recycleBin
    public LiveData<List<List<CategoryFolderTuple>>> getClassifiedCategoryTuplesLive(boolean isDeleted, int sort) {
        LiveData<List<CategoryFolderTuple>> categoryTuplesLive = this.getCategoryTuplesLive(isDeleted);
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(categoryTuplesLive, isDeleted);

        return getClassifiedCategoryTuplesLive(categoryTuplesLive, contentsSizesLive, sort);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Category WHERE isDeleted = :isDeleted")
    protected abstract LiveData<List<CategoryFolderTuple>> getCategoryTuplesLive(boolean isDeleted);

    @NotNull
    private LiveData<List<List<CategoryFolderTuple>>> getClassifiedCategoryTuplesLive(LiveData<List<CategoryFolderTuple>> categoryTuplesLive,
                                                                                      LiveData<int[]> contentsSizesLive,
                                                                                      int sort) {
        return Transformations.map(contentsSizesLive, contentsSizes -> {
            List<CategoryFolderTuple> categoryTuples = categoryTuplesLive.getValue();
            super.setContentsSize(categoryTuples, contentsSizes);
            List<List<CategoryFolderTuple>> classifiedList = super.getClassifiedListByParentIndex(categoryTuples);
            return super.orderCategoryFolderTuplesList(sort, classifiedList);
        });
    }


    //to recycleBin search (maybe searchView?)
    public LiveData<List<List<CategoryFolderTuple>>> getSearchCategoryTuplesList(int sort, String keyWord) {
        LiveData<List<CategoryFolderTuple>> searchCategoryTuplesLive = getSearchCategoryTuplesLive(keyWord);
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(searchCategoryTuplesLive, true);

        return getClassifiedCategoryTuplesLive(searchCategoryTuplesLive, contentsSizesLive, sort);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Category WHERE isDeleted = 1 AND name LIKE :keyWord")
    protected abstract LiveData<List<CategoryFolderTuple>> getSearchCategoryTuplesLive(String keyWord);


    @Transaction
    //to treeView (disposable)
    public List<CategoryFolderTuple> getCategoryTuplesByParentIndex(byte parentIndex, int sort) {
        List<CategoryFolderTuple> categoryTuples = this.getCategoryTuplesByParentIndex2(parentIndex);
        long[] categoryIds = super.getItemIds(categoryTuples);
        int[] contentsSizes = getContentsSizesByParentIds(categoryIds, false);
        super.setContentsSize(categoryTuples, contentsSizes);
        return super.orderCategoryFolderTuples(sort, categoryTuples);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Category WHERE parentIndex = :parentIndex AND isDeleted = 0")
    protected abstract List<CategoryFolderTuple> getCategoryTuplesByParentIndex2(byte parentIndex);


    @Transaction
    //to treeView
    public CategoryFolderTuple getCategoryTupleById(long id) {
        CategoryFolderTuple categoryTuple = this.getCategoryTupleById2(id);
        int contentsSize = getContentsSizeByParentId(id, false);
        categoryTuple.setContentsSize(contentsSize);
        return categoryTuple;
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Category WHERE id = :id AND isDeleted = 0")
    protected abstract CategoryFolderTuple getCategoryTupleById2(long id);

    @Transaction
    //from addCategory Dialog
    public long insertCategory(String categoryName, byte parentIndex) {
        int orderNumber = getCategoryLargestOrderNumber() + 1;
        Category category = ModelFactory.makeCategory(categoryName, parentIndex, orderNumber);
        return insert(category);
    }

    @Transaction
    //from restore Dialog
    public Long[] insertRestoreCategories(@NotNull byte[] parentIndex) {
        String restoreCategoryName = context.getString(R.string.restore_category_name);
        int orderNumber = getCategoryLargestOrderNumber() + 1;

        int count = parentIndex.length;
        Category[] categories = new Category[count];
        for (int i = 0; i < count; i++) {
            categories[i] = ModelFactory.makeCategory(restoreCategoryName, parentIndex[i], orderNumber);
            orderNumber++;
        }
        return insert(categories);
    }

    @Query("SELECT max(orderNumber) FROM Category")
    protected abstract int getCategoryLargestOrderNumber();

    @Transaction
    //from nameEdit dialog
    public void updateCategory(long id, String name) {
        CategoryFolderTuple categoryTuple = getCategoryTupleById2(id);
        categoryTuple.setName(name);
        updateTuple(categoryTuple);
    }

    @Transaction
    //from delete dialog, restore dialog
    public void deleteOrRestoreCategories(long[] categoryIds, boolean isDeleted) {
        CategoryDeletedRelation[] categoryDeletedRelations = this.getCategoryDeletedRelationsByIds(categoryIds, !isDeleted);
        DeletedTuple[] categoryDeletedTuples = super.getCategoryDeletedTuples(categoryDeletedRelations);
        setDeletedTuples(categoryDeletedTuples, !isDeleted);
        updateDeletedTuples(categoryDeletedTuples);

        setChildrenParentDeleted(categoryDeletedRelations, isDeleted);
    }


    private void setChildrenParentDeleted(CategoryDeletedRelation[] categoryDeletedTupleWithChildren, boolean isParentDeleted) {
        if (folderDao == null) folderDao = appDataBase.folderDao();
        if (sizeDao == null) sizeDao = appDataBase.sizeDao();

        LinkedList<ParentDeletedTuple> childFolderParentDeletedTuples = getCategoryChildFolderParentDeletedTuples(categoryDeletedTupleWithChildren);
        long[] childFolderIds = getParentTuplesIds(childFolderParentDeletedTuples);
        //childFolderParentDeletedTuples -> allChildFolderParentDeletedTuples
        addAllChildFolderParentDeletedTuples(childFolderIds, childFolderParentDeletedTuples, isParentDeleted);

        LinkedList<ParentDeletedTuple> childSizeParentDeletedTuples = getCategoryChildSizeParentDeletedTuples(categoryDeletedTupleWithChildren);
        long[] allFolderIds = getParentTuplesIds(childFolderParentDeletedTuples);
        //categoryChildSizesParentDeletedTuples -> allChildSizeParentDeletedTuples
        addAllChildSizeParentDeletedTuples(allFolderIds, childSizeParentDeletedTuples, isParentDeleted);

        super.setParentDeletedTuples(childFolderParentDeletedTuples, !isParentDeleted);
        super.setParentDeletedTuples(childSizeParentDeletedTuples, !isParentDeleted);
        folderDao.updateParentDeletedTuples(childFolderParentDeletedTuples);
        sizeDao.updateParentDeletedTuples(childFolderParentDeletedTuples);
    }

    /**
     * @param childFolderParentDeletedTuples -> allChildrenFolderParentDeletedTuples
     *                                       from the second category -> folder
     */
    private void addAllChildFolderParentDeletedTuples(long[] categoryChildFolderIds,
                                                      @NotNull LinkedList<ParentDeletedTuple> childFolderParentDeletedTuples,
                                                      boolean isParentDeleted) {
        FolderDeletedRelation[] childFolderDeletedRelation = folderDao.getFolderDeleteRelationByParentIds(categoryChildFolderIds, isParentDeleted);

        LinkedList<ParentDeletedTuple> folderChildFolderParentDeletedTuples = getFolderChildFolderParentDeletedTuples(childFolderDeletedRelation);
        childFolderParentDeletedTuples.addAll(folderChildFolderParentDeletedTuples);

        Arrays.stream(childFolderDeletedRelation)
                .filter(FolderDeletedRelation::areChildFoldersNotEmpty)
                .forEach(folderDeletedRelation ->
                        addAllChildFolderParentDeletedTuples(folderDeletedRelation.getChildFolderIds(),
                                childFolderParentDeletedTuples,
                                isParentDeleted));
    }

    /**
     * @param childrenSizeParentDeletedTuples -> allChildSizeParentDeletedTuples
     */
    private void addAllChildSizeParentDeletedTuples(long[] allChildrenFolderIds, @NotNull LinkedList<ParentDeletedTuple> childrenSizeParentDeletedTuples, boolean isParentDeleted) {
        childrenSizeParentDeletedTuples.addAll(sizeDao.getSizeParentDeletedTuplesByParentIds(allChildrenFolderIds, isParentDeleted));
    }

    @Query("SELECT id,isDeleted FROM Category WHERE id IN (:ids) AND isDeleted = :isDeleted")
    protected abstract CategoryDeletedRelation[] getCategoryDeletedRelationsByIds(long[] ids, boolean isDeleted);

    //from addCategory dialog
    @Query("SELECT EXISTS(SELECT name, parentIndex FROM Category WHERE name =:categoryName AND parentIndex=:parentIndex AND isDeleted = 0)")
    public abstract boolean isExistingCategoryName(String categoryName, byte parentIndex);

    //from restore dialog restore category
    @Query("SELECT EXISTS(SELECT id FROM Category WHERE id IN (:ids) AND isDeleted = 0)")
    public abstract Boolean[] isExistingCategories(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    protected abstract void updateTuple(CategoryFolderTuple categoryTuple);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    public abstract void updateTuples(LinkedList<CategoryFolderTuple> categoryTuples);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Category.class)
    protected abstract void updateDeletedTuples(DeletedTuple[] deletedTuples);

    public interface CategoryDaoInterFace {
        LiveData<List<List<CategoryFolderTuple>>> getClassifiedCategoryTuplesLive(boolean isDeleted);

        LiveData<List<List<CategoryFolderTuple>>> getSearchCategoryTuplesList(String keyWord);

        LiveData<List<CategoryFolderTuple>> getCategoryTuplesByParentIndex(byte parentIndex);

        LiveData<CategoryFolderTuple> getCategoryTupleById(long id);

        LiveData<Long> insertCategory(String categoryName, byte parentIndex);

        LiveData<Long[]> insertRestoreCategories(@NotNull byte[] parentIndex);

        void updateCategory(long id, String name);

        void updateCategories(LinkedList<CategoryFolderTuple> categoryTuples);

        void deleteOrRestoreCategories(long[] categoryIds, boolean isDeleted);

        LiveData<Boolean> isExistingCategoryName(String categoryName, byte parentIndex);

        LiveData<Boolean[]> isExistingCategories(long[] ids);
    }
}
