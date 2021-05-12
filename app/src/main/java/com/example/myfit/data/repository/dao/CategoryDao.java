package com.example.myfit.data.repository.dao;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.category.Category;
import com.example.myfit.data.model.category.CategoryDeletedTuple;
import com.example.myfit.data.model.folder.FolderDeletedTuple;
import com.example.myfit.data.model.tuple.DeletedTuple;
import com.example.myfit.data.model.tuple.OrderNumberTuple;
import com.example.myfit.data.model.tuple.ParentDeletedTuple;
import com.example.myfit.util.StreamUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

@Dao
public abstract class CategoryDao extends BaseDao<Category> {
    private final FolderDao folderDao;
    private final SizeDao sizeDao;

    @Inject
    public CategoryDao(@NotNull AppDataBase appDataBase) {
        this.folderDao = appDataBase.folderDao();
        this.sizeDao = appDataBase.sizeDao();
    }

    @Transaction
    public LiveData<List<List<Category>>> getCategoriesListLive() {
        MediatorLiveData<List<List<Category>>> mediatorCategoryLive = getMediatorLive();

        return Transformations.switchMap(mediatorCategoryLive, categoriesList -> new LiveData<List<List<Category>>>() {
            @Override
            protected void setValue(List<List<Category>> value) {
                super.setValue(categoriesList);
            }
        });
    }

    @NotNull
    private MediatorLiveData<List<List<Category>>> getMediatorLive() {
        LiveData<List<Category>> categoriesLive = getCategoriesLive();
        LiveData<long[]> folderParentIdsLive = folderDao.getFolderParentIdsLive();
        LiveData<long[]> sizeParentIdsLive = sizeDao.getSizeParentIdsLive();
        MediatorLiveData<List<List<Category>>> mediatorLiveData = new MediatorLiveData<>();
        List<List<Category>> categoriesList = new ArrayList<>(4);

        mediatorLiveData.addSource(categoriesLive, categories -> {
            clearCategoriesList(categoriesList);
            setCategoryContentsSize(categories, getFolderParentIds(), getSizeParentIds());
            sortByParentCategory(categories, categoriesList);
            mediatorLiveData.setValue(categoriesList);
        });

        mediatorLiveData.addSource(folderParentIdsLive, folderParentIds -> {
            clearCategoriesList(categoriesList);
            List<Category> categories = getCategories(categoriesLive);
            setCategoryContentsSize(categories, folderParentIds, getSizeParentIds());
            sortByParentCategory(categories, categoriesList);
            mediatorLiveData.setValue(categoriesList);
        });

        mediatorLiveData.addSource(sizeParentIdsLive, sizeParentId -> {
            clearCategoriesList(categoriesList);
            List<Category> categories = getCategories(categoriesLive);
            setCategoryContentsSize(categories, getFolderParentIds(), sizeParentId);
            sortByParentCategory(categories, categoriesList);
            mediatorLiveData.setValue(categoriesList);
        });
        return mediatorLiveData;
    }

    private void clearCategoriesList(@NotNull List<List<Category>> categoriesList) {
        if (!categoriesList.isEmpty()) categoriesList.clear();
    }

    private List<Category> getCategories(@NotNull LiveData<List<Category>> categoriesLive) {
        return categoriesLive.getValue();
    }

    private long[] getFolderParentIds() {
        return folderDao.getFolderParentIds();
    }

    private long[] getSizeParentIds() {
        return sizeDao.getSizeParentIds();
    }

    private void setCategoryContentsSize(List<Category> categories, long[] folderParentIds, long[] sizeParentId) {
        try {
            categories.forEach(category -> {
                int contentsSize = Arrays.stream(folderParentIds).filter(id -> category.getId() == id).toArray().length;
                contentsSize += Arrays.stream(sizeParentId).filter(id -> category.getId() == id).toArray().length;
                category.setContentsSize(contentsSize);
            });
        } catch (NullPointerException e) {
            logError(e);
        }
    }

    private void sortByParentCategory(List<Category> categories, List<List<Category>> categoriesList) {
        try {
            categories.forEach(category -> categoriesList.get(category.getParentCategoryIndex()).add(category));
        } catch (NullPointerException e) {
            logError(e);
        }
    }

    private void logError(NullPointerException e) {
        Log.e("에러", "setCategoryContentsSize: Null Pointer Excapter" + e, null);
    }

    @Query("SELECT * FROM Category WHERE isDeleted = 0")
    protected abstract LiveData<List<Category>> getCategoriesLive();

    @Query("SELECT id, orderNumber FROM Category WHERE isDeleted = 0")
    public abstract List<OrderNumberTuple> getCategoryOrderNumberTuple();

    public List<List<Category>> getCategoriesListByParentCategory(byte parentCategoryIndex) {
        List<Category> categories = getCategoriesByParentCategory(parentCategoryIndex);
        List<List<Category>> categoriesList = new ArrayList<>(4);
        setCategoryContentsSize(categories, getFolderParentIds(), getSizeParentIds());
        sortByParentCategory(categories, categoriesList);
        return categoriesList;
    }

    @Query("SELECT * FROM Category WHERE parentCategoryIndex = :parentCategoryIndex AND isDeleted = 0")
    protected abstract List<Category> getCategoriesByParentCategory(int parentCategoryIndex);

    @Query("SELECT name FROM Category WHERE isDeleted = 0")
    public abstract LiveData<List<String>> getCategoryNamesLive();

    @Query("SELECT name FROM Category WHERE parentCategoryIndex = :parentCategoryIndex AND isDeleted = 0 ")
    public abstract List<String> getCategoryNamesByParentCategory(int parentCategoryIndex);

    @Query("SELECT * FROM Category WHERE id = :id AND isDeleted = 0")
    public abstract Category getCategoryById(long id);

    @Query("SELECT EXISTS(SELECT * FROM Category WHERE name =:categoryName AND parentCategoryIndex=:parentCategoryIndex AND isDeleted= 0)")
    public abstract boolean doesThisCategoryExistByName(String categoryName, int parentCategoryIndex);

    @Query("SELECT EXISTS(SELECT * FROM Category WHERE name =:categoryName AND isDeleted= 0)")
    public abstract boolean[] doesThisCategoryExistByName(String categoryName);

    public int getCategoryOrderNumber() {
        return getCategoryLargestOrderNumber() + 1;
    }

    @Query("SELECT max(orderNumber) FROM Category")
    protected abstract int getCategoryLargestOrderNumber();

    @Transaction
    public void deleteCategories(long[] categoryIds) {
        CategoryDeletedTuple[] categoryDeletedTupleArray = getCategoryDeletedTuples(categoryIds);
        Arrays.stream(categoryDeletedTupleArray).forEach(CategoryDeletedTuple::setCategoryDeleted);
        DeletedTuple[] categoryDeletedTuples = StreamUtil.getCategoryDeletedTuples(categoryDeletedTupleArray);
        updateDeletedTuples(categoryDeletedTuples);

        deleteChildren(categoryDeletedTupleArray);
    }

    private void deleteChildren(CategoryDeletedTuple[] categoryDeletedTupleArray) {
        long[] categoryChildFolderIds = StreamUtil.getCategoryChildFolderIds(categoryDeletedTupleArray);
        List<ParentDeletedTuple> categoryChildFolderParentDeletedTuples = StreamUtil.getCategoryChildFolderParentDeletedTuples(categoryDeletedTupleArray);
        //categoryChildFolderParentDeletedTuples -> allChildFolderParentDeletedTuples
        addAllChildFolderParentDeletedTuples(categoryChildFolderIds, categoryChildFolderParentDeletedTuples);
        deleteChildFolders(categoryChildFolderParentDeletedTuples);

        long[] allFolderIds = StreamUtil.getIdsByParentDeletedTuples(categoryChildFolderParentDeletedTuples);
        List<ParentDeletedTuple> categoryChildSizeParentDeletedTuples = StreamUtil.getCategoryChildSizeParentDeletedTuples(categoryDeletedTupleArray);
        //categoryChildSizesParentDeletedTuples -> allChildSizeParentDeletedTuples
        addAllChildSizeParentDeletedTuples(allFolderIds, categoryChildSizeParentDeletedTuples);
        deleteChildSizes(categoryChildSizeParentDeletedTuples);
    }

    private void deleteChildSizes(@NotNull List<ParentDeletedTuple> allChildSizesParentDeletedTuples) {
        allChildSizesParentDeletedTuples.forEach(parentDeletedTuple -> parentDeletedTuple.setParentDeleted(true));
        updateParentDeletedTuples(allChildSizesParentDeletedTuples);
    }

    private void deleteChildFolders(@NotNull List<ParentDeletedTuple> allChildFolderParentDeletedTuples) {
        allChildFolderParentDeletedTuples.forEach(parentDeletedTuple -> parentDeletedTuple.setParentDeleted(true));
        updateParentDeletedTuples(allChildFolderParentDeletedTuples);
    }

    /**
     * @param categoryChildFolderParentDeletedTuples -> allChildFolderParentDeletedTuples
     */
    private void addAllChildFolderParentDeletedTuples(long[] parentIds, @NotNull List<ParentDeletedTuple> categoryChildFolderParentDeletedTuples) {
        FolderDeletedTuple[] childFolderDeletedTupleArray = folderDao.getFolderDeleteTuples(parentIds);

        categoryChildFolderParentDeletedTuples.addAll(StreamUtil.getFolderChildFolderParentDeletedTuples(childFolderDeletedTupleArray));

        Arrays.stream(childFolderDeletedTupleArray)
                .filter(FolderDeletedTuple::areChildFoldersNotEmpty)
                .forEach(folderDeletedTuple -> addAllChildFolderParentDeletedTuples(folderDeletedTuple.getChildFolderIds(), categoryChildFolderParentDeletedTuples));
    }

    /**
     * @param categoryChildSizeParentDeletedTuples -> allChildSizeParentDeletedTuples
     */
    private void addAllChildSizeParentDeletedTuples(long[] allFolderIds, @NotNull List<ParentDeletedTuple> categoryChildSizeParentDeletedTuples) {
        categoryChildSizeParentDeletedTuples.addAll(sizeDao.getSizeParentDeletedTuples(allFolderIds));
    }

    @Query("SELECT id,isDeleted FROM Category WHERE id IN (:ids) AND isDeleted = 0")
    public abstract CategoryDeletedTuple[] getCategoryDeletedTuples(long[] ids);
}
