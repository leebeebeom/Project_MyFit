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
import com.example.myfit.data.model.tuple.BaseTuple;
import com.example.myfit.data.model.tuple.CategoryFolderTuple;
import com.example.myfit.data.model.tuple.ParentDeletedTuple;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

    protected void setItemsContentsSize(List<CategoryFolderTuple> items, int[] contentsSizes) {
        try {
            int itemsSize = items.size();
            for (int i = 0; i < itemsSize; i++)
                items.get(i).setContentsSize(contentsSizes[i]);
        } catch (NullPointerException e) {
            logE(e);
        }
    }

    protected List<LinkedList<R>> getClassifiedListByParentIndex(List<R> tuples) {
        List<LinkedList<R>> classifiedList = Arrays.asList(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>());

        try {
            tuples.forEach(tuple -> classifiedList.get(tuple.getParentIndex()).add(tuple));
        } catch (NullPointerException e) {
            logE(e);
        }
        return classifiedList;
    }

    protected void logE(NullPointerException e) {
        Log.e("에러", "Null Pointer Exception" + e, e);
    }

    protected void setParentDeletedTuples(@NotNull List<ParentDeletedTuple> parentDeletedTuples, boolean isParentDeleted) {
        parentDeletedTuples.forEach(parentDeletedTuple -> parentDeletedTuple.setParentDeleted(isParentDeleted));
    }

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId IN (:parentIds) AND isDeleted = 0 AND isParentDeleted = :isParentDeleted) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId IN(:parentIds) AND isDeleted =0 AND isParentDeleted = :isParentDeleted))")
    protected abstract LiveData<int[]> getContentsSizesLiveByParentIds(long[] parentIds, boolean isParentDeleted);

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId IN (:parentIds) AND isDeleted = 0 AND isParentDeleted = :isParentDeleted) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId IN(:parentIds) AND isDeleted =0 AND isParentDeleted = :isParentDeleted))")
    protected abstract int[] getContentsSizesByParentIds(long[] parentIds, boolean isParentDeleted);

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId = :parentId AND isDeleted = 0 AND isParentDeleted = :isParentDeleted) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId = :parentId AND isDeleted =0 AND isParentDeleted = :isParentDeleted))")
    protected abstract int getContentsSizeByParentId(long parentId, boolean isParentDeleted);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(T item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long[] insert(T[] items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(T item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<T> items);

    public interface BaseDaoInterFace {
        LiveData<List<String>> getAutoCompleteWordsLive();

        LiveData<List<String>> getDeletedAutoCompleteWordsLive();
    }

}
