package com.example.myfit.data.repository.dao;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myfit.data.model.folder.Folder;
import com.example.myfit.data.model.size.Size;
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
public abstract class BaseDao<T extends BaseTuple> {

    @NotNull
    protected LiveData<int[]> getContentsSizesLive(LiveData<List<T>> tuplesLive, boolean isParentDeleted) {
        return Transformations.switchMap(tuplesLive, tuples -> {
            long[] itemIds = getItemIds(tuples);
            return getContentsSizesLiveByParentIds(itemIds, isParentDeleted);
        });
    }

    protected long[] getItemIds(@NotNull List<T> tuples) {
        return tuples.stream()
                .mapToLong(BaseTuple::getId)
                .toArray();
    }

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId IN (:parentIds) AND isDeleted = 0 AND isParentDeleted = :isParentDeleted) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId IN(:parentIds) AND isDeleted = 0 AND isParentDeleted = :isParentDeleted))")
    protected abstract LiveData<int[]> getContentsSizesLiveByParentIds(long[] parentIds, boolean isParentDeleted);

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

    @NotNull
    protected LiveData<List<List<CategoryFolderTuple>>> getClassifiedTuplesLive(LiveData<List<CategoryFolderTuple>> tuplesLive,
                                                                                LiveData<int[]> contentsSizesLive) {
        return Transformations.map(contentsSizesLive, contentsSizes -> {
            List<CategoryFolderTuple> tuples = tuplesLive.getValue();
            setContentsSize(tuples, contentsSizes);
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

    @NotNull
    private List<List<CategoryFolderTuple>> getClassifiedTuplesByParentIndex(List<CategoryFolderTuple> tuples) {
        List<LinkedList<CategoryFolderTuple>> classifiedLinkedList = Arrays.asList(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
        List<List<CategoryFolderTuple>> classifiedList = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        try {
            tuples.forEach(tuple -> classifiedLinkedList.get(tuple.getParentIndex()).add(tuple));
            for (int i = 0; i < 4; i++) classifiedList.get(i).addAll(classifiedLinkedList.get(i));
        } catch (NullPointerException e) {
            logE(e);
        }

        return classifiedList;
    }

    protected void logE(Exception e) {
        Log.e("에러", "Exception " + e.getMessage(), e);
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
        Arrays.stream(deletedTuples).forEach(deletedTuple -> deletedTuple.setDeletedTime(currentTime));
    }

    private long getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(System.currentTimeMillis())));
    }

    private void clearDeletedTime(DeletedTuple[] deletedTuples) {
        Arrays.stream(deletedTuples).forEach(deletedTuple -> deletedTuple.setDeletedTime(0));
    }

    protected void setChildrenParentDeleted(long[] parentIds, boolean isDeleted) {
        LinkedList<ParentDeletedTuple> allFolderParentDeletedTuples = new LinkedList<>();
        addAllChildFolderParentDeletedTuples(parentIds, allFolderParentDeletedTuples);

        LinkedList<ParentDeletedTuple> allSizeParentDeletedTuples = new LinkedList<>(this.getSizeParentDeletedTuplesByParentIds(parentIds));
        long[] allFolderIds = getParentDeletedTupleIds(allFolderParentDeletedTuples);
        addAllChildSizeParentDeletedTuples(allFolderIds, allSizeParentDeletedTuples);

        setParentDeletedTuples(allFolderParentDeletedTuples, isDeleted);
        setParentDeletedTuples(allSizeParentDeletedTuples, isDeleted);
        updateFolderParentDeletedTuples(allFolderParentDeletedTuples);
        updateSizeParentDeletedTuples(allSizeParentDeletedTuples);
    }

    protected void addAllChildFolderParentDeletedTuples(long[] parentIds, @NotNull LinkedList<ParentDeletedTuple> allParentDeletedTuples) {
        List<ParentDeletedTuple> childFolderParentDeletedTuples = this.getFolderParentDeletedTuplesByParentIds(parentIds);
        if (!childFolderParentDeletedTuples.isEmpty()) {
            allParentDeletedTuples.addAll(childFolderParentDeletedTuples);
            long[] childFolderIds = getParentDeletedTupleIds(childFolderParentDeletedTuples);
            addAllChildFolderParentDeletedTuples(childFolderIds, allParentDeletedTuples);
        }
    }

    private long[] getParentDeletedTupleIds(@NotNull List<ParentDeletedTuple> childFolderParentDeletedTuples) {
        return childFolderParentDeletedTuples.stream()
                .filter(parentDeletedTuple -> !parentDeletedTuple.isDeleted())
                .mapToLong(ParentDeletedTuple::getId).toArray();
    }

    @Query("SELECT id, isDeleted ,isParentDeleted FROM Folder WHERE parentId IN (:parentIds)")
    protected abstract List<ParentDeletedTuple> getFolderParentDeletedTuplesByParentIds(long[] parentIds);

    protected void addAllChildSizeParentDeletedTuples(long[] parentIds, @NotNull LinkedList<ParentDeletedTuple> allParentDeletedTuples) {
        allParentDeletedTuples.addAll(getSizeParentDeletedTuplesByParentIds(parentIds));
    }

    @Query("SELECT id, isDeleted, isParentDeleted FROM Size WHERE parentId IN(:parentIds)")
    protected abstract List<ParentDeletedTuple> getSizeParentDeletedTuplesByParentIds(long[] parentIds);

    protected void setParentDeletedTuples(@NotNull List<ParentDeletedTuple> parentDeletedTuples, boolean isParentDeleted) {
        parentDeletedTuples.forEach(parentDeletedTuple -> parentDeletedTuple.setParentDeleted(isParentDeleted));
    }

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void updateFolderParentDeletedTuples(List<ParentDeletedTuple> parentDeletedTuples);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    protected abstract void updateSizeParentDeletedTuples(List<ParentDeletedTuple> parentDeletedTuples);

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId IN (:parentIds) AND isDeleted = 0 AND isParentDeleted = 0) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId IN(:parentIds) AND isDeleted =0 AND isParentDeleted = 0))")
    protected abstract int[] getContentsSizesByParentIds(long[] parentIds);

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId = :parentId AND isDeleted = 0 AND isParentDeleted = 0) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId = :parentId AND isDeleted =0 AND isParentDeleted = 0))")
    protected abstract int getContentsSizeByParentId(long parentId);
}
