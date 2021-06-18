package com.example.myfit.data.repository.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myfit.data.model.BaseModel;
import com.example.myfit.data.model.model.Folder;
import com.example.myfit.data.model.model.Size;
import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.data.tuple.ParentDeletedTuple;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

@Dao
public abstract class BaseDao<T extends BaseModel, U extends BaseTuple> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(T t);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract List<Long> insert(List<T> ts);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(T t);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(List<T> ts);

    public abstract void updateTuples(List<U> tuples);

    public void setChildrenParentDeleted(long[] parentIds) {
        LinkedList<ParentDeletedTuple> allFolderParentDeletedTuples = getAllFolderParentDeletedTuples(parentIds);
        LinkedList<ParentDeletedTuple> allSizeParentDeletedTuples = getAllSizeParentDeletedTuples(parentIds, allFolderParentDeletedTuples);

        setParentDeletedTuples(allFolderParentDeletedTuples);
        setParentDeletedTuples(allSizeParentDeletedTuples);
        updateFolderParentDeletedTuples(allFolderParentDeletedTuples);
        updateSizeParentDeletedTuples(allSizeParentDeletedTuples);
    }

    @NotNull
    private LinkedList<ParentDeletedTuple> getAllFolderParentDeletedTuples(long[] parentIds) {
        LinkedList<ParentDeletedTuple> allFolderParentDeletedTuples = new LinkedList<>();
        addAllChildFolderParentDeletedTuples(parentIds, allFolderParentDeletedTuples);
        return allFolderParentDeletedTuples;
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
                .mapToLong(ParentDeletedTuple::getId)
                .toArray();
    }

    @NotNull
    private LinkedList<ParentDeletedTuple> getAllSizeParentDeletedTuples(long[] parentIds, LinkedList<ParentDeletedTuple> allFolderParentDeletedTuples) {
        LinkedList<ParentDeletedTuple> allSizeParentDeletedTuples = new LinkedList<>(this.getSizeParentDeletedTuplesByParentIds(parentIds));
        long[] allFolderIds = getParentDeletedTupleIds(allFolderParentDeletedTuples);
        addAllChildSizeParentDeletedTuples(allFolderIds, allSizeParentDeletedTuples);
        return allSizeParentDeletedTuples;
    }

    @Query("SELECT id, deleted, parentDeleted FROM Size WHERE parentId IN(:parentIds)")
    protected abstract List<ParentDeletedTuple> getSizeParentDeletedTuplesByParentIds(long[] parentIds);

    private void addAllChildSizeParentDeletedTuples(long[] parentIds, @NotNull LinkedList<ParentDeletedTuple> allParentDeletedTuples) {
        allParentDeletedTuples.addAll(getSizeParentDeletedTuplesByParentIds(parentIds));
    }

    private void setParentDeletedTuples(@NotNull List<ParentDeletedTuple> parentDeletedTuples) {
        parentDeletedTuples.forEach(parentDeletedTuple -> parentDeletedTuple.setParentDeleted(!parentDeletedTuple.isDeleted()));
    }

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void updateFolderParentDeletedTuples(List<ParentDeletedTuple> parentDeletedTuples);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    protected abstract void updateSizeParentDeletedTuples(List<ParentDeletedTuple> parentDeletedTuples);
}
