package com.example.myfit.data.repository.dao;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myfit.data.model.BaseModel;
import com.example.myfit.data.model.category.CategoryDeletedRelation;
import com.example.myfit.data.model.folder.FolderDeletedRelation;
import com.example.myfit.data.model.tuple.BaseTuple;
import com.example.myfit.data.model.tuple.CategoryFolderTuple;
import com.example.myfit.data.model.tuple.DeletedTuple;
import com.example.myfit.data.model.tuple.ParentDeletedTuple;
import com.example.myfit.util.SortUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Dao
public abstract class BaseDao<T extends BaseModel, R extends BaseTuple> {

    //to searchView
    @Query("SELECT(SELECT name FROM Category WHERE isDeleted = 0) + " +
            "(SELECT name FROM Folder WHERE isDeleted = 0 AND isParentDeleted = 0) + " +
            "(SELECT name FROM Size WHERE isDeleted = 0 AND isParentDeleted = 0) + " +
            "(SELECT brand FROM Size WHERE isDeleted = 0 AND isParentDeleted = 0)")
    public abstract LiveData<List<String>> getAutoCompleteWordsLive();

    //to recycleBin search
    @Query("SELECT(SELECT name FROM Category WHERE isDeleted = 1) + " +
            "(SELECT name FROM Folder WHERE isDeleted = 1 OR isParentDeleted = 1) + " +
            "(SELECT name FROM Size WHERE isDeleted = 1 OR isParentDeleted = 1) + " +
            "(SELECT brand FROM Size WHERE isDeleted = 1 OR isParentDeleted = 1)")
    public abstract LiveData<List<String>> getDeletedAutoCompleteWordsLive();

    @NotNull
    protected LiveData<int[]> getContentsSizesLive(LiveData<List<R>> tuplesLive, boolean isParentDeleted) {
        return Transformations.switchMap(tuplesLive, items -> {
            long[] itemIds = getItemIds(items);
            return getContentsSizesLiveByParentIds(itemIds, isParentDeleted);
        });
    }

    protected long[] getItemIds(@NotNull List<R> tuples) {
        return tuples.stream().mapToLong(BaseTuple::getId).toArray();
    }

    protected void setContentsSize(List<CategoryFolderTuple> items, int[] contentsSizes) {
        try {
            int itemsSize = items.size();
            for (int i = 0; i < itemsSize; i++)
                items.get(i).setContentsSize(contentsSizes[i]);
        } catch (NullPointerException e) {
            logE(e);
        }
    }

    protected List<List<R>> getClassifiedListByParentIndex(List<R> tuples) {
        List<LinkedList<R>> classifiedList = Arrays.asList(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
        List<List<R>> classifiedList2 = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        try {
            tuples.forEach(tuple -> classifiedList.get(tuple.getParentIndex()).add(tuple));
            for (int i = 0; i < 4; i++) classifiedList2.get(i).addAll(classifiedList2.get(i));
        } catch (NullPointerException e) {
            logE(e);
        }

        return classifiedList2;
    }

    protected void logE(NullPointerException e) {
        Log.e("에러", "Null Pointer Exception" + e.getMessage(), e);
    }

    protected void setParentDeletedTuples(@NotNull List<ParentDeletedTuple> parentDeletedTuples, boolean isParentDeleted) {
        parentDeletedTuples.forEach(parentDeletedTuple -> parentDeletedTuple.setParentDeleted(isParentDeleted));
    }

    protected DeletedTuple[] getCategoryDeletedTuples(CategoryDeletedRelation[] categoryDeletedRelations) {
        return Arrays.stream(categoryDeletedRelations)
                .map(CategoryDeletedRelation::getCategoryDeletedTuple)
                .toArray(DeletedTuple[]::new);
    }

    protected DeletedTuple[] getFolderDeletedTuples(FolderDeletedRelation[] folderDeletedTupleWithChildren) {
        return Arrays.stream(folderDeletedTupleWithChildren)
                .map(FolderDeletedRelation::getFolderDeletedTuple)
                .toArray(DeletedTuple[]::new);
    }

    @NotNull
    protected LinkedList<ParentDeletedTuple> getCategoryChildFolderParentDeletedTuples(CategoryDeletedRelation[] categoryDeletedTupleWithChildren) {
        return Arrays.stream(categoryDeletedTupleWithChildren)
                .map(CategoryDeletedRelation::getChildFolderParentDeletedTuples)
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    protected void setDeletedTuples(DeletedTuple[] deletedTuples, boolean isDeleted) {
        Arrays.stream(deletedTuples)
                .forEach(deletedTuple -> deletedTuple.setDeleted(isDeleted));
    }

    protected long[] getParentTuplesIds(@NotNull LinkedList<ParentDeletedTuple> parentDeletedTuples) {
        return parentDeletedTuples.stream()
                .mapToLong(ParentDeletedTuple::getId)
                .toArray();
    }

    @NotNull
    protected LinkedList<ParentDeletedTuple> getFolderChildFolderParentDeletedTuples(FolderDeletedRelation[] folderDeletedTupleWithChildren) {
        return Arrays.stream(folderDeletedTupleWithChildren)
                .map(FolderDeletedRelation::getChildFolderParentDeletedTuples)
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @NotNull
    protected LinkedList<ParentDeletedTuple> getCategoryChildSizeParentDeletedTuples(CategoryDeletedRelation[] categoryDeletedTupleWithChildren) {
        return Arrays.stream(categoryDeletedTupleWithChildren)
                .filter(CategoryDeletedRelation::areChildSizesNotEmpty)
                .map(CategoryDeletedRelation::getChildSizeParentDeletedTuples)
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    protected List<List<CategoryFolderTuple>> orderCategoryFolderTuplesList(int sort, List<List<CategoryFolderTuple>> categoryFolderTuplesList) {
        try {
            categoryFolderTuplesList
                    .forEach(categoryFolderTuples -> SortUtil.orderCategoryFolderTuples(sort, categoryFolderTuples));
        } catch (NullPointerException e) {
            logE(e);
        }
        return categoryFolderTuplesList;
    }

    protected List<CategoryFolderTuple> orderCategoryFolderTuples(int sort, List<CategoryFolderTuple> categoryFolderTuplesList) {
        try {
            SortUtil.orderCategoryFolderTuples(sort, categoryFolderTuplesList);
        } catch (NullPointerException e) {
            logE(e);
        }
        return categoryFolderTuplesList;
    }

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId IN (:parentIds) AND isDeleted = 0 AND isParentDeleted = :isParentDeleted) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId IN(:parentIds) AND isDeleted =0 AND isParentDeleted = :isParentDeleted))")
    protected abstract LiveData<int[]> getContentsSizesLiveByParentIds(long[] parentIds, boolean isParentDeleted);

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId IN (:parentIds) AND isDeleted = 0 AND isParentDeleted = 0) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId IN(:parentIds) AND isDeleted =0 AND isParentDeleted = 0))")
    protected abstract int[] getContentsSizesByParentIds(long[] parentIds);

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId = :parentId AND isDeleted = 0 AND isParentDeleted = 0) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId = :parentId AND isDeleted =0 AND isParentDeleted = 0))")
    protected abstract int getContentsSizeByParentId(long parentId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(T item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Long[] insert(T[] items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(T item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<T> items);

    @Query("SELECT EXISTS(SELECT id FROM Category WHERE id IN (:ids) AND isDeleted = 0)+" +
            "(SELECT id FROM Folder WHERE id IN (:ids) AND isDeleted = 0 AND isParentDeleted = 0)")
    public abstract Boolean[] isExistingParents(long ids);

    public interface BaseDaoInterFace {
        LiveData<List<String>> getAutoCompleteWordsLive();

        LiveData<List<String>> getDeletedAutoCompleteWordsLive();

        fLiveData<Boolean[]> isExistingParents(long ids);
    }

}
