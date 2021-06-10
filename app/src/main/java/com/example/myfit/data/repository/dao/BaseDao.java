package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myfit.data.model.model.Folder;
import com.example.myfit.data.model.model.Size;
import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.data.tuple.DeletedTuple;
import com.example.myfit.data.tuple.ParentDeletedTuple;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
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
    protected long[] getTupleIds(@NotNull List<T> tuples) {
        return tuples.stream()
                .mapToLong(BaseTuple::getId)
                .toArray();
    }

    protected <R extends CategoryTuple> LiveData<List<List<R>>> getClassifiedTuplesLive(LiveData<List<R>> tuplesLive,
                                                                                        LiveData<int[]> contentSizesLive) {
        return Transformations.map(contentSizesLive, contentSizes -> {
            List<R> tuples = tuplesLive.getValue();
            setContentSize(tuples, contentSizes);
            return getClassifiedTuplesByParentIndex(tuples);
        });
    }

    protected <R extends CategoryTuple> void setContentSize(List<R> tuples, int[] contentSizes) {
        if (tuples != null && tuples.size() == contentSizes.length) {
            int count = tuples.size();
            for (int i = 0; i < count; i++) {
                tuples.get(i).setContentSize(contentSizes[i]);
            }
        }
    }

    protected <R extends CategoryTuple> List<List<R>> getClassifiedTuplesByParentIndex(List<R> tuples) {
        if (tuples != null) {
            List<LinkedList<R>> classifiedLinkedList = Arrays.asList(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
            List<List<R>> classifiedList = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

            tuples.forEach(tuple -> classifiedLinkedList.get(tuple.getParentIndex()).add(tuple));
            for (int i = 0; i < 4; i++) classifiedList.get(i).addAll(classifiedLinkedList.get(i));
            return classifiedList;
        } else return null;
    }

    protected <R extends CategoryTuple> void sortTuples(Sort sort, List<R> tuples) {
        if (tuples != null) SortUtil.sortCategoryFolderTuples(sort, tuples);
    }

    protected void setDeletedTuples(DeletedTuple[] deletedTuples) {
        Arrays.stream(deletedTuples)
                .forEach(deletedTuple -> deletedTuple.setDeleted(!deletedTuple.isDeleted()));
        setDeletedTime(deletedTuples);
    }

    @Contract(pure = true)
    private void setDeletedTime(@NotNull DeletedTuple[] deletedTuples) {
        long currentTime = getCurrentTime();
        Arrays.stream(deletedTuples).forEach(deletedTuple -> {
            if (deletedTuple.isDeleted())
                deletedTuple.setDeletedTime(currentTime);
            else deletedTuple.setDeletedTime(0);
        });
    }

    protected long getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(System.currentTimeMillis())));
    }

    protected void setChildrenParentDeleted(long[] parentIds) {
        LinkedList<ParentDeletedTuple> allFolderParentDeletedTuples = new LinkedList<>();
        addAllChildFolderParentDeletedTuples(parentIds, allFolderParentDeletedTuples);

        LinkedList<ParentDeletedTuple> allSizeParentDeletedTuples = new LinkedList<>(this.getSizeParentDeletedTuplesByParentIds(parentIds));
        long[] allFolderIds = getParentDeletedTupleIds(allFolderParentDeletedTuples);
        addAllChildSizeParentDeletedTuples(allFolderIds, allSizeParentDeletedTuples);

        setParentDeletedTuples(allFolderParentDeletedTuples);
        setParentDeletedTuples(allSizeParentDeletedTuples);
        updateFolderParentDeletedTuples(allFolderParentDeletedTuples);
        updateSizeParentDeletedTuples(allSizeParentDeletedTuples);
    }

    private void addAllChildFolderParentDeletedTuples(long[] parentIds, @NotNull LinkedList<ParentDeletedTuple> allParentDeletedTuples) {
        List<ParentDeletedTuple> childFolderParentDeletedTuples = getFolderParentDeletedTuplesByParentIds(parentIds);
        if (!childFolderParentDeletedTuples.isEmpty()) {
            allParentDeletedTuples.addAll(childFolderParentDeletedTuples);
            long[] childFolderIds = getParentDeletedTupleIds(childFolderParentDeletedTuples);
            addAllChildFolderParentDeletedTuples(childFolderIds, allParentDeletedTuples);
        }
    }

    @Query("SELECT id, deleted ,parentDeleted FROM Folder WHERE parentId IN (:parentIds)")
    protected abstract List<ParentDeletedTuple> getFolderParentDeletedTuplesByParentIds(long[] parentIds);

    private long[] getParentDeletedTupleIds(@NotNull List<ParentDeletedTuple> childFolderParentDeletedTuples) {
        return childFolderParentDeletedTuples.stream()
                .filter(parentDeletedTuple -> !parentDeletedTuple.isDeleted())
                .mapToLong(ParentDeletedTuple::getId).toArray();
    }

    private void addAllChildSizeParentDeletedTuples(long[] parentIds, @NotNull LinkedList<ParentDeletedTuple> allParentDeletedTuples) {
        allParentDeletedTuples.addAll(getSizeParentDeletedTuplesByParentIds(parentIds));
    }

    @Query("SELECT id, deleted, parentDeleted FROM Size WHERE parentId IN(:parentIds)")
    protected abstract List<ParentDeletedTuple> getSizeParentDeletedTuplesByParentIds(long[] parentIds);

    private void setParentDeletedTuples(@NotNull List<ParentDeletedTuple> parentDeletedTuples) {
        parentDeletedTuples.forEach(parentDeletedTuple -> parentDeletedTuple.setParentDeleted(!parentDeletedTuple.isDeleted()));
    }

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void updateFolderParentDeletedTuples(List<ParentDeletedTuple> parentDeletedTuples);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    protected abstract void updateSizeParentDeletedTuples(List<ParentDeletedTuple> parentDeletedTuples);
}
