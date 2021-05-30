package com.example.myfit.data.repository.dao;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.data.model.model.Folder;
import com.example.myfit.data.model.model.size.Size;
import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.data.tuple.DeletedTuple;
import com.example.myfit.data.tuple.ParentDeletedTuple;
import com.example.myfit.util.SortUtil;
import com.example.myfit.util.constant.Sort;

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
    public LiveData<int[]> getContentSizesLive(LiveData<List<T>> tuplesLive, boolean parentDeleted) {
        return Transformations.switchMap(tuplesLive, tuples -> {
            long[] itemIds = getItemIds(tuples);
            return getContentsSizesLiveByParentIds(itemIds, parentDeleted);
        });
    }

    protected long[] getItemIds(@NotNull List<T> tuples) {
        return tuples.stream()
                .mapToLong(BaseTuple::getId)
                .toArray();
    }

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId IN (:parentIds) AND deleted = 0 AND parentDeleted = :parentDeleted) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId IN(:parentIds) AND deleted = 0 AND parentDeleted = :parentDeleted))")
    protected abstract LiveData<int[]> getContentsSizesLiveByParentIds(long[] parentIds, boolean parentDeleted);


    @NotNull
    protected <R extends CategoryTuple> LiveData<List<List<R>>> getClassifiedTuplesLive(LiveData<List<R>> tuplesLive,
                                                                                        LiveData<int[]> contentsSizesLive) {
        return Transformations.map(contentsSizesLive, contentsSizes -> {
            List<R> tuples = tuplesLive.getValue();
            setContentsSize(tuples, contentsSizes);
            return getClassifiedTuplesByParentIndex(tuples);
        });
    }

    protected <R extends CategoryTuple> void setContentsSize(List<R> items, int[] contentsSizes) {
        try {
            int itemsSize = items.size();
            for (int i = 0; i < itemsSize; i++)
                items.get(i).setContentsSize(contentsSizes[i]);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            logE(e);
        }
    }

    protected <R extends CategoryTuple> void orderTuples(Sort sort, List<R> tuples) {
        try {
            SortUtil.orderCategoryFolderTuples(sort, tuples);
        } catch (NullPointerException e) {
            logE(e);
        }
    }

    @NotNull
    protected <R extends CategoryTuple> List<List<R>> getClassifiedTuplesByParentIndex(List<R> tuples) {
        List<LinkedList<R>> classifiedLinkedList = Arrays.asList(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
        List<List<R>> classifiedList = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

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

    protected void setDeletedTuples(DeletedTuple[] deletedTuples, boolean deleted) {
        Arrays.stream(deletedTuples)
                .forEach(deletedTuple -> deletedTuple.setDeleted(deleted));

        if (deleted) setDeletedTime(deletedTuples);
        else clearDeletedTime(deletedTuples);
    }

    @Contract(pure = true)
    private void setDeletedTime(@NotNull DeletedTuple[] deletedTuples) {
        long currentTime = getCurrentTime();
        Arrays.stream(deletedTuples).forEach(deletedTuple -> deletedTuple.setDeletedTime(currentTime));
    }

    protected long getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(System.currentTimeMillis())));
    }

    private void clearDeletedTime(DeletedTuple[] deletedTuples) {
        Arrays.stream(deletedTuples).forEach(deletedTuple -> deletedTuple.setDeletedTime(0));
    }

    protected void setChildrenParentDeleted(long[] parentIds, boolean deleted) {
        LinkedList<ParentDeletedTuple> allFolderParentDeletedTuples = new LinkedList<>();
        addAllChildFolderParentDeletedTuples(parentIds, allFolderParentDeletedTuples);

        LinkedList<ParentDeletedTuple> allSizeParentDeletedTuples = new LinkedList<>(this.getSizeParentDeletedTuplesByParentIds(parentIds));
        long[] allFolderIds = getParentDeletedTupleIds(allFolderParentDeletedTuples);
        addAllChildSizeParentDeletedTuples(allFolderIds, allSizeParentDeletedTuples);

        setParentDeletedTuples(allFolderParentDeletedTuples, deleted);
        setParentDeletedTuples(allSizeParentDeletedTuples, deleted);
        updateFolderParentDeletedTuples(allFolderParentDeletedTuples);
        updateSizeParentDeletedTuples(allSizeParentDeletedTuples);
    }

    private void addAllChildFolderParentDeletedTuples(long[] parentIds, @NotNull LinkedList<ParentDeletedTuple> allParentDeletedTuples) {
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

    @Query("SELECT id, deleted ,parentDeleted FROM Folder WHERE parentId IN (:parentIds)")
    protected abstract List<ParentDeletedTuple> getFolderParentDeletedTuplesByParentIds(long[] parentIds);

    private void addAllChildSizeParentDeletedTuples(long[] parentIds, @NotNull LinkedList<ParentDeletedTuple> allParentDeletedTuples) {
        allParentDeletedTuples.addAll(getSizeParentDeletedTuplesByParentIds(parentIds));
    }

    @Query("SELECT id, deleted, parentDeleted FROM Size WHERE parentId IN(:parentIds)")
    protected abstract List<ParentDeletedTuple> getSizeParentDeletedTuplesByParentIds(long[] parentIds);

    private void setParentDeletedTuples(@NotNull List<ParentDeletedTuple> parentDeletedTuples, boolean parentDeleted) {
        parentDeletedTuples.forEach(parentDeletedTuple -> parentDeletedTuple.setParentDeleted(parentDeleted));
    }

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void updateFolderParentDeletedTuples(List<ParentDeletedTuple> parentDeletedTuples);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    protected abstract void updateSizeParentDeletedTuples(List<ParentDeletedTuple> parentDeletedTuples);

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId IN (:parentIds) AND deleted = 0 AND parentDeleted = 0) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId IN(:parentIds) AND deleted =0 AND parentDeleted = 0))")
    protected abstract int[] getContentsSizesByParentIds(long[] parentIds);

    @Query("SELECT((SELECT COUNT(parentId) FROM Folder WHERE parentId = :parentId AND deleted = 0 AND parentDeleted = 0) + " +
            "(SELECT COUNT(parentId) FROM Size WHERE parentId = :parentId AND deleted =0 AND parentDeleted = 0))")
    protected abstract int getContentsSizeByParentId(long parentId);
}
