package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myfit.data.model.ModelFactory;
import com.example.myfit.data.model.model.Folder;
import com.example.myfit.data.tuple.DeletedTuple;
import com.example.myfit.data.tuple.ParentIdTuple;
import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.util.constant.Sort;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Dao
public abstract class FolderDao extends BaseDao<FolderTuple> {

    //to list
    public LiveData<List<FolderTuple>> getTuplesLiveByParentId(long parentId) {
        LiveData<List<FolderTuple>> tuplesLive = this.getTuplesLiveByParentId2(parentId);
        LiveData<int[]> contentSizesLive = getFolderContentSizesLive(tuplesLive);

        return this.getTuplesWithContentSizeLive(tuplesLive, contentSizesLive);
    }

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime, parentId " +
            "FROM Folder WHERE parentId = :parentId AND deleted = 0 AND parentDeleted = 0")
    protected abstract LiveData<List<FolderTuple>> getTuplesLiveByParentId2(long parentId);

    @NotNull
    protected LiveData<int[]> getFolderContentSizesLive(LiveData<List<FolderTuple>> tuplesLive) {
        return Transformations.switchMap(tuplesLive, tuples -> {
            long[] itemIds = getTupleIds(tuples);
            return getFolderContentSizesLive(itemIds);
        });
    }

    @Query("SELECT SUM((childFolder.parentId IS NOT NULL AND childFolder.deleted = 0) + (size.parentId IS NOT NULL AND size.deleted = 0)) FROM Folder " +
            "LEFT OUTER JOIN Folder AS ChildFolder ON folder.id = childFolder.parentId " +
            "LEFT OUTER JOIN Size ON folder.id = size.parentId " +
            "WHERE folder.id IN (:ids) " +
            "GROUP BY folder.id")
    protected abstract LiveData<int[]> getFolderContentSizesLive(long[] ids);

    @NotNull
    private LiveData<List<FolderTuple>> getTuplesWithContentSizeLive(LiveData<List<FolderTuple>> tuplesLive,
                                                                     LiveData<int[]> contentSizesLive) {
        return Transformations.map(contentSizesLive, contentSizes -> {
            List<FolderTuple> tuples = tuplesLive.getValue();
            super.setContentSize(tuples, contentSizes);
            return tuples;
        });
    }

    //to recycleBin
    public LiveData<List<List<FolderTuple>>> getDeletedClassifiedTuplesLive() {
        LiveData<List<FolderTuple>> deletedTuplesLive = this.getDeletedTuplesLive();
        LiveData<int[]> contentSizesLive = getFolderContentSizesLive(deletedTuplesLive);

        return super.getClassifiedTuplesLive(deletedTuplesLive, contentSizesLive);
    }

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime, parentId FROM Folder WHERE deleted = 1 ORDER BY deletedTime DESC")
    protected abstract LiveData<List<FolderTuple>> getDeletedTuplesLive();

    //to searchView, recycleBin search
    public LiveData<List<List<FolderTuple>>> getSearchTuplesListLive(boolean deleted) {
        LiveData<List<FolderTuple>> searchTuplesLive = this.getSearchTuplesLive(deleted);
        LiveData<int[]> contentSizesLive = getFolderContentSizesLive(searchTuplesLive);

        return super.getClassifiedTuplesLive(searchTuplesLive, contentSizesLive);
    }

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime, parentId " +
            "FROM Folder WHERE deleted = :deleted AND parentDeleted = 0 ORDER BY name")
    protected abstract LiveData<List<FolderTuple>> getSearchTuplesLive(boolean deleted);

    //to treeView (disposable)
    @Transaction
    public List<FolderTuple> getTuplesByParentIndex(int parentIndex, Sort sort) {
        List<FolderTuple> tuples = this.getTuplesByParentIndex(parentIndex);
        long[] ids = super.getTupleIds(tuples);
        int[] contentSizes = getFolderContentSizes(ids);
        super.setContentSize(tuples, contentSizes);
        super.sortTuples(sort, tuples);
        return tuples;
    }

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime, parentId FROM Folder WHERE parentIndex = :parentIndex AND deleted = 0 AND parentDeleted = 0")
    protected abstract List<FolderTuple> getTuplesByParentIndex(int parentIndex);

    @Query("SELECT SUM((childFolder.parentId IS NOT NULL AND childFolder.deleted = 0) + (size.parentId IS NOT NULL AND size.deleted = 0)) FROM Folder " +
            "LEFT OUTER JOIN Folder AS ChildFolder ON folder.id = childFolder.parentId " +
            "LEFT OUTER JOIN Size ON folder.id = size.parentId " +
            "WHERE folder.id IN (:ids) " +
            "GROUP BY folder.id")
    protected abstract int[] getFolderContentSizes(long[] ids);

    @Transaction
    //to treeView(disposable)
    public FolderTuple getTupleById(long id) {
        FolderTuple tuple = this.getTupleById2(id);
        int contentSize = getFolderContentSizes(id);
        tuple.setContentSize(contentSize);
        return tuple;
    }

    @Query("SELECT SUM((childFolder.parentId IS NOT NULL AND childFolder.deleted = 0) + (size.parentId IS NOT NULL AND size.deleted = 0)) FROM Folder " +
            "LEFT OUTER JOIN Folder AS ChildFolder ON folder.id = childFolder.parentId " +
            "LEFT OUTER JOIN Size ON folder.id = size.parentId " +
            "WHERE folder.id = :id " +
            "GROUP BY folder.id")
    protected abstract int getFolderContentSizes(long id);

    @Query("SELECT id, parentIndex, sortNumber, name, contentSize, deletedTime, parentId FROM Folder WHERE id = :id")
    protected abstract FolderTuple getTupleById2(long id);

    //to list
    @Query("SELECT * FROM Folder WHERE id = :id")
    public abstract LiveData<Folder> getSingleLiveById(long id);

    @Transaction
    //from addFolder dialog
    public long insert(String name, long parentId, int parentIndex) {
        int sortNumber = this.getLargestSortNumber() + 1;
        Folder folder = ModelFactory.makeFolder(name, parentId, parentIndex, sortNumber);
        return this.insert(folder);
    }

    @Query("SELECT max(sortNumber) FROM Folder")
    protected abstract int getLargestSortNumber();

    @Insert
    protected abstract long insert(Folder folder);

    @Transaction
    //from editFolderName dialog
    public void update(long id, String name) {
        FolderTuple tuple = this.getTupleById2(id);
        tuple.setName(name);
        this.update(tuple);
    }

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void update(FolderTuple tuple);

    //from adapter drag drop
    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    public abstract void update(List<FolderTuple> tuples);

    @Transaction
    //from move dialog
    public void move(long targetId, long[] ids) {
        ParentIdTuple[] parentIdTuples = this.getParentIdTuplesByIds(ids);
        Arrays.stream(parentIdTuples)
                .forEach(parentIdTuple -> parentIdTuple.setParentId(targetId));
        this.update(parentIdTuples);
    }

    @Query("SELECT id, parentId FROM Folder WHERE id IN (:ids)")
    public abstract ParentIdTuple[] getParentIdTuplesByIds(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void update(ParentIdTuple[] parentIdTuples);

    @Transaction
    //from delete dialog, restore dialog
    public void deleteOrRestore(long[] ids) {
        DeletedTuple[] deletedTuples = this.getDeletedTuplesByIds(ids);
        super.setDeletedTuples(deletedTuples);
        this.update(deletedTuples);

        super.setChildrenParentDeleted(ids);
    }

    @Query("SELECT id, deleted, deletedTime FROM Folder WHERE id IN (:ids)")
    protected abstract DeletedTuple[] getDeletedTuplesByIds(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void update(DeletedTuple[] deletedTuples);

    //from addFolder dialog
    @Query("SELECT EXISTS(SELECT name, parentId FROM Folder WHERE name =:name AND parentId = :parentId AND deleted = 0 AND parentDeleted = 0)")
    public abstract boolean isExistingName(String name, long parentId);

    @Transaction
    public LinkedList<FolderTuple> getFolderPathTuples(long id) {
        FolderTuple tuple = getTupleById2(id);
        List<FolderTuple> folderTuples = getTuplesByParentIndex(tuple.getParentIndex());
        LinkedList<FolderTuple> folderPathTuples = new LinkedList<>();
        folderPathTuples.add(tuple);
        completeFolderPath(folderTuples, folderPathTuples, tuple);
        return folderPathTuples;
    }

    private void completeFolderPath(List<FolderTuple> folderTuples, LinkedList<FolderTuple> folderPathTuples, FolderTuple tuple) {
        Optional<FolderTuple> parentTuple = folderTuples.stream()
                .filter(folderTuple -> tuple.getParentId() == folderTuple.getId())
                .findFirst();

        if (parentTuple.isPresent()) {
            folderPathTuples.add(0, parentTuple.get());
            completeFolderPath(folderTuples, folderPathTuples, parentTuple.get());
        }
    }
}
