package com.example.myfit.data.repository.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myfit.data.model.BaseModel;
import com.example.myfit.data.model.model.Folder;
import com.example.myfit.data.model.model.Size;
import com.example.myfit.data.tuple.BaseTuple;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transaction
    public void updateChildrenParentDeleted(long[] parentIds, List<Folder> allFolders, List<Size> allSizes) {
        List<Folder> allChildFolders = getAllChildFolders(parentIds, allFolders);
        List<Size> allChildSizes = getAllChildSizes(parentIds, allChildFolders, allSizes);

        allChildFolders.forEach(folder -> folder.setParentDeleted(!folder.isParentDeleted()));
        updateFolders(allChildFolders);
        allChildSizes.forEach(size -> size.setParentDeleted(!size.isParentDeleted()));
        updateSizes(allChildSizes);
    }

    @NotNull
    private List<Folder> getAllChildFolders(long[] parentIds, List<Folder> allFolders) {
        List<Folder> allChildFolders = new LinkedList<>();
        addAllChildFolders(parentIds, allChildFolders, allFolders);
        return allChildFolders;
    }

    private void addAllChildFolders(long[] parentIds, @NotNull List<Folder> allChildFolders, List<Folder> allFolders) {
        List<Folder> childFolders = getFoldersByParentIds(parentIds, allFolders);
        if (!childFolders.isEmpty()) {
            allChildFolders.addAll(childFolders);
            long[] childFolderIds = getModelIds(childFolders);
            addAllChildFolders(childFolderIds, allChildFolders, allFolders);
        }
    }

    private List<Folder> getFoldersByParentIds(long[] parentIds, List<Folder> allFolders) {
        return allFolders.stream()
                .filter(folder -> Arrays.stream(parentIds).anyMatch(parentId -> parentId == folder.getParentId()))
                .collect(Collectors.toList());
    }

    private <V extends BaseModel> long[] getModelIds(@NotNull List<V> models) {
        return models.stream()
                .filter(model -> !model.isDeleted())
                .mapToLong(BaseModel::getId)
                .toArray();
    }

    @NotNull
    private List<Size> getAllChildSizes(long[] parentIds, List<Folder> allChildFolders, List<Size> allSizes) {
        List<Size> allChildSizes = new LinkedList<>(getSizesByParentIds(parentIds, allSizes));
        long[] allFolderIds = getModelIds(allChildFolders);
        allChildSizes.addAll(getSizesByParentIds(allFolderIds, allSizes));
        return allChildSizes;
    }

    private List<Size> getSizesByParentIds(long[] parentIds, List<Size> allSizes) {
        return allSizes.stream()
                .filter(size -> Arrays.stream(parentIds).anyMatch(parentId -> parentId == size.getParentId()))
                .collect(Collectors.toList());
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    protected abstract void updateFolders(List<Folder> folders);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    protected abstract void updateSizes(List<Size> sizes);
}
