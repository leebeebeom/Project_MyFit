package com.example.myfit.data.repository.dao;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myfit.data.model.BaseModel;
import com.example.myfit.data.model.size.SizeTuple;
import com.example.myfit.data.model.tuple.BaseTuple;
import com.example.myfit.data.model.tuple.CategoryFolderTuple;
import com.example.myfit.data.model.tuple.DeletedTuple;
import com.example.myfit.data.model.tuple.ParentDeletedTuple;
import com.example.myfit.util.SortUtil;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

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
            "(SELECT name FROM Folder WHERE isDeleted = 1) + " +
            "(SELECT name FROM Size WHERE isDeleted = 1) + " +
            "(SELECT brand FROM Size WHERE isDeleted = 1)")
    public abstract LiveData<List<String>> getDeletedAutoCompleteWordsLive();

    @NotNull
    protected LiveData<int[]> getContentsSizesLive(LiveData<List<R>> tuplesLive, boolean isParentDeleted) {
        return Transformations.switchMap(tuplesLive, tuples -> {
            long[] itemIds = getItemIds(tuples);
            return getContentsSizesLiveByParentIds(itemIds, isParentDeleted);
        });
    }

    protected long[] getItemIds(@NotNull List<R> tuples) {
        return tuples.stream().mapToLong(BaseTuple::getId).toArray();
    }

    @NotNull
    protected LiveData<List<List<CategoryFolderTuple>>> getClassifiedTuplesLive(LiveData<List<CategoryFolderTuple>> tuplesLive,
                                                                              LiveData<int[]> contentsSizesLive,
                                                                              int sort) {
        return Transformations.map(contentsSizesLive, contentsSizes -> {
            List<CategoryFolderTuple> tuples = tuplesLive.getValue();
            setContentsSize(tuples, contentsSizes);
            orderTuples(sort, tuples);
            return getClassifiedTuplesByParentIndex(tuples);
        });
    }

    protected void setContentsSize(List<CategoryFolderTuple> items, int[] contentsSizes) {
        try {
            int itemsSize = items.size();
            for (int i = 0; i < itemsSize; i++)
                items.get(i).setContentsSize(contentsSizes[i]);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            logE(e);
        }
    }

    protected void orderTuples(int sort, List<CategoryFolderTuple> categoryFolderTuplesList) {
        try {
            SortUtil.orderCategoryFolderTuples(sort, categoryFolderTuplesList);
        } catch (NullPointerException e) {
            logE(e);
        }
    }

    protected List<List<CategoryFolderTuple>> getClassifiedTuplesByParentIndex(List<CategoryFolderTuple> tuples) {
        List<LinkedList<CategoryFolderTuple>> classifiedList = Arrays.asList(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
        List<List<CategoryFolderTuple>> classifiedList2 = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        try {
            tuples.forEach(tuple -> classifiedList.get(tuple.getParentIndex()).add(tuple));
            for (int i = 0; i < 4; i++) classifiedList2.get(i).addAll(classifiedList2.get(i));
        } catch (NullPointerException e) {
            logE(e);
        }

        return classifiedList2;
    }

    protected void logE(Exception e) {
        Log.e("에러", "Exception " + e.getMessage(), e);
    }

    protected void setParentDeletedTuples(@NotNull List<ParentDeletedTuple> parentDeletedTuples, boolean isParentDeleted) {
        parentDeletedTuples.forEach(parentDeletedTuple -> parentDeletedTuple.setParentDeleted(isParentDeleted));
    }

    protected void setDeletedTuples(DeletedTuple[] deletedTuples, boolean isDeleted) {
        Arrays.stream(deletedTuples)
                .forEach(deletedTuple -> deletedTuple.setDeleted(isDeleted));

        if (isDeleted) setDeletedTime(deletedTuples);
        else clearDeletedTime(deletedTuples);
    }

    @Contract(pure = true)
    private void setDeletedTime(@NotNull DeletedTuple[] deletedTuples) {
        long currentTime = getCurrentTime();
        int count = deletedTuples.length;
        for (DeletedTuple deletedTuple : deletedTuples) {
            deletedTuple.setDeletedTime(currentTime);
            currentTime++;
        }
    }

    private long getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(System.currentTimeMillis())));
    }

    private void clearDeletedTime(DeletedTuple[] deletedTuples) {
        Arrays.stream(deletedTuples).forEach(deletedTuple -> deletedTuple.setDeletedTime(0));
    }

    protected void orderSizeTuples(int sort, List<SizeTuple> sizeTuples) {
        try {
            SortUtil.orderSizeTuples(sort, sizeTuples);
        } catch (NullPointerException e) {
            logE(e);
        }
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
    protected abstract long insert(T item);

    public interface BaseDaoInterFace {
        LiveData<List<String>> getAutoCompleteWordsLive();

        LiveData<List<String>> getDeletedAutoCompleteWordsLive();
    }

}
