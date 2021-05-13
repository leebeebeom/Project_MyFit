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
import com.example.myfit.data.model.category.Category;
import com.example.myfit.data.model.tuple.DeletedTuple;
import com.example.myfit.data.model.tuple.ParentDeletedTuple;
import com.example.myfit.data.model.tuple.ParentIdTuple;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Dao
public abstract class BaseDao<T extends BaseModel> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(T item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long[] insert(T[] items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long[] insert(List<T> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(T item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<T> items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(T[] items);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateDeletedTuples(DeletedTuple[] deletedTuples);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateParentDeletedTuples(List<ParentDeletedTuple> parentDeletedTuples);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateParentIdTuples(ParentIdTuple[] parentIdTuples);

    protected <R extends Category> void clearItemsList(@NotNull List<List<R>> itemsList) {
        if (!itemsList.isEmpty()) itemsList.clear();
    }

    protected <R extends Category> void setItemsContentsSize(List<R> items, int[] contentsSizes) {
        try {
            int itemsSize = items.size();
            for (int i = 0; i < itemsSize; i++)
                items.get(i).setContentsSize(contentsSizes[i]);
        } catch (NullPointerException e) {
            logError(e);
        }
    }

    protected void sortByParentIndex(List<T> items, List<List<T>> itemsList) {
        try {
            items.forEach(category -> itemsList.get(category.getParentIndex()).add(category));
        } catch (NullPointerException e) {
            logError(e);
        }
    }

    protected void logError(NullPointerException e) {
        Log.e("에러", "setCategoryContentsSize: Null Pointer Excapter" + e, null);
    }

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId IN (:parentIds) AND isDeleted = 0 AND isParentDeleted = 0) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId IN(:parentIds) AND isDeleted =0 AND isParentDeleted = 0))")
    protected abstract LiveData<int[]> getContentsSizesLiveByParentIds(long[] parentIds);

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId IN (:parentIds) AND isDeleted = 0 AND isParentDeleted = 0) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId IN(:parentIds) AND isDeleted =0 AND isParentDeleted = 0))")
    protected abstract int[] getContentsSizesByParentIds(long[] parentIds);

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId = :parentId AND isDeleted = 0 AND isParentDeleted = 0) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId = :parentId AND isDeleted =0 AND isParentDeleted = 0))")
    protected abstract LiveData<Integer> getContentsSizeLiveByParentId(long parentId);

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId = :parentId AND isDeleted = 0 AND isParentDeleted = 0) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId = :parentId AND isDeleted =0 AND isParentDeleted = 0))")
    protected abstract int getContentsSizeByParentId(long parentId);

    @NotNull
    @Contract("_ -> new")
    protected LiveData<int[]> getContentsSizesLive(LiveData<List<T>> itemsLive) {
        return Transformations.switchMap(itemsLive, items -> {
            long[] itemsIds = getItemsIds(items);
            return getContentsSizesLiveByParentIds(itemsIds);
        });
    }

    protected long[] getItemsIds(@NotNull List<T> items) {
        return items.stream().mapToLong(BaseModel::getId).toArray();
    }
}
