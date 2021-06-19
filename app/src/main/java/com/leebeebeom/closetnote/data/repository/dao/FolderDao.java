package com.leebeebeom.closetnote.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.leebeebeom.closetnote.data.model.model.Folder;
import com.leebeebeom.closetnote.data.tuple.ContentSizeTuple;
import com.leebeebeom.closetnote.data.tuple.DeletedTuple;
import com.leebeebeom.closetnote.data.tuple.ParentIdTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.FolderTuple;

import java.util.LinkedList;
import java.util.List;

@Dao
public abstract class FolderDao extends BaseCategoryFolderDao<Folder, FolderTuple> {
    @Query("SELECT id, parentIndex, sortNumber, name, deletedTime, parentId FROM Folder WHERE id = :id")
    protected abstract FolderTuple getTupleById2(long id);

    @Query("SELECT SUM((childFolder.parentId IS NOT NULL AND childFolder.deleted = 0) + (size.parentId IS NOT NULL AND size.deleted = 0)) FROM Folder " +
            "LEFT OUTER JOIN Folder AS ChildFolder ON folder.id = childFolder.parentId " +
            "LEFT OUTER JOIN Size ON folder.id = size.parentId " +
            "WHERE folder.id = :id " +
            "GROUP BY folder.id")
    protected abstract int getFolderContentSizes(long id);

    @Query("SELECT max(sortNumber) FROM Folder")
    protected abstract int getLargestSortNumber();

    @Transaction
    //from editFolderName dialog
    public void update(long id, String name) {
        FolderTuple tuple = this.getTupleById2(id);
        tuple.setName(name.trim());
        this.update(tuple);
    }

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void update(FolderTuple tuple);

    //from adapter drag drop
    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    public abstract void updateTuples(List<FolderTuple> tuples);

    @Transaction
    //from move dialog
    public void move(long targetId, long[] ids) {
        List<ParentIdTuple> parentIdTuples = this.getParentIdTuplesByIds(ids);
        parentIdTuples.forEach(parentIdTuple -> parentIdTuple.setParentId(targetId));
        this.updateParentIdTuples(parentIdTuples);
    }

    @Query("SELECT id, parentId FROM Folder WHERE id IN (:ids)")
    public abstract List<ParentIdTuple> getParentIdTuplesByIds(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    public abstract void updateParentIdTuples(List<ParentIdTuple> parentIdTuples);

    @Transaction
    //from delete dialog, restore dialog
    public void deleteOrRestore(long[] ids) {
        List<DeletedTuple> deletedTuples = this.getDeletedTuplesByIds(ids);
//        setDeleted(deletedTuples);
        this.updateDeletedTuples(deletedTuples);
//        super.updateChildrenParentDeleted(ids);
    }

    @Query("SELECT id, deleted, deletedTime FROM Folder WHERE id IN (:ids)")
    protected abstract List<DeletedTuple> getDeletedTuplesByIds(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void updateDeletedTuples(List<DeletedTuple> deletedTuples);

    //from addFolder dialog
    @Query("SELECT EXISTS(SELECT name, parentId FROM Folder WHERE name =:name AND parentId = :parentId AND deleted = 0 AND parentDeleted = 0)")
    public abstract boolean isExistingName(String name, long parentId);

    @Transaction
    public LinkedList<FolderTuple> getFolderPathTuples(long id) {
//        FolderTuple tuple = getTupleById2(id);
//        List<FolderTuple> folderTuples = getTuplesByParentIndex(tuple.getParentIndex());
//        return getFolderPath(tuple, folderTuples);
        return null;
    }


    @Override
    protected LiveData<List<Folder>> getAllModelsLive() {
        return getAllFoldersLive();
    }

    @Override
    protected LiveData<List<ContentSizeTuple>> getFolderContentSizeTuplesLive() {
        return getFolderContentSizeTuplesLiveImpl();
    }

    @Override
    protected LiveData<List<ContentSizeTuple>> getSizeContentSizeTuplesLive() {
        return getSizeContentSizeTuplesLiveImpl();
    }

    @Query("SELECT * FROM Folder")
    protected abstract LiveData<List<Folder>> getAllFoldersLive();

    @Query("SELECT folder.id AS parentId, SUM(folder2.parentId IS NOT NULL AND folder2.deleted = 0) AS size " +
            "FROM Folder, Folder AS Folder2 WHERE folder.id = folder2.parentId " +
            "GROUP BY folder.id")
    protected abstract LiveData<List<ContentSizeTuple>> getFolderContentSizeTuplesLiveImpl();

    @Query("SELECT folder.id AS parentId, SUM(size.parentId IS NOT NULL AND size.deleted = 0) AS size FROM Folder " +
            "LEFT OUTER JOIN Size ON size.parentId = folder.id " +
            "GROUP BY folder.id")
    protected abstract LiveData<List<ContentSizeTuple>> getSizeContentSizeTuplesLiveImpl();
}
