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
import com.example.myfit.data.model.folder.Folder;
import com.example.myfit.data.model.folder.FolderTuple;
import com.example.myfit.data.model.tuple.DeletedTuple;
import com.example.myfit.data.model.tuple.ParentIdTuple;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Dao
public abstract class FolderDao extends BaseDao<FolderTuple> {

    //to list
    public LiveData<List<FolderTuple>> getTuplesLiveByParentId(long parentId, int sort) {
        LiveData<List<FolderTuple>> tuplesLive = this.getTuplesLiveByParentId(parentId);
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(tuplesLive, false);

        return this.getOrderedTuplesLive(tuplesLive, contentsSizesLive, sort);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize, deletedTime, parentId FROM Folder WHERE parentId = :parentId AND isDeleted = 0 AND isParentDeleted = 0")
    protected abstract LiveData<List<FolderTuple>> getTuplesLiveByParentId(long parentId);

    @NotNull
    private LiveData<List<FolderTuple>> getOrderedTuplesLive(LiveData<List<FolderTuple>> tuplesLive,
                                                               LiveData<int[]> contentsSizesLive,
                                                               int sort) {
        return Transformations.map(contentsSizesLive, contentsSizes -> {
            List<FolderTuple> tuples = tuplesLive.getValue();
            super.setContentsSize(tuples, contentsSizes);
            super.orderTuples(sort, tuples);
            return tuples;
        });
    }

    //to recycleBin
    public LiveData<List<List<FolderTuple>>> getDeletedClassifiedTuplesLive() {
        LiveData<List<FolderTuple>> deletedTuplesLive = this.getDeletedTuplesLive();
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(deletedTuplesLive, true);

        return super.getClassifiedTuplesLive(deletedTuplesLive, contentsSizesLive);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize, deletedTime, parentId FROM Folder WHERE isDeleted = 1 ORDER BY deletedTime DESC")
    protected abstract LiveData<List<FolderTuple>> getDeletedTuplesLive();

    //to searchView
    public LiveData<List<List<FolderTuple>>> getSearchTuplesListLive() {
        LiveData<List<FolderTuple>> searchTuplesLive = this.getSearchTuplesLive(false);
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(searchTuplesLive, false);

        return this.getClassifiedTuplesLive(searchTuplesLive, contentsSizesLive);
    }

    //to recycleBin search
    public LiveData<List<List<FolderTuple>>> getDeletedSearchTuplesListLive() {
        LiveData<List<FolderTuple>> deletedSearchTuplesLive = this.getSearchTuplesLive(true);
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(deletedSearchTuplesLive, true);

        return this.getClassifiedTuplesLive(deletedSearchTuplesLive, contentsSizesLive);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize, deletedTime, parentId FROM Folder " +
            "WHERE isDeleted = :isDeleted AND isParentDeleted = 0 AND name ORDER BY name")
    protected abstract LiveData<List<FolderTuple>> getSearchTuplesLive(boolean isDeleted);

    //to treeView (disposable)
    @Transaction
    public List<FolderTuple> getTuplesByParentIndex(int parentIndex, int sort) {
        List<FolderTuple> tuples = this.getTuplesByParentIndex(parentIndex);
        long[] ids = super.getItemIds(tuples);
        int[] contentsSizes = getContentsSizesByParentIds(ids);
        super.setContentsSize(tuples, contentsSizes);
        super.orderTuples(sort, tuples);
        return tuples;
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize, deletedTime, parentId FROM Folder WHERE parentIndex = :parentIndex AND isDeleted = 0 AND isParentDeleted = 0")
    protected abstract List<FolderTuple> getTuplesByParentIndex(int parentIndex);

    @Transaction
    //to treeView(disposable)
    public FolderTuple getTupleById(long id) {
        FolderTuple tuple = this.getTupleById2(id);
        int contentsSize = getContentsSizeByParentId(id);
        tuple.setContentsSize(contentsSize);
        return tuple;
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize, deletedTime, parentId FROM Folder WHERE id = :id AND isDeleted = 0 AND isParentDeleted = 0")
    protected abstract FolderTuple getTupleById2(long id);

    //to list
    @Query("SELECT * FROM Folder WHERE id = :id")
    public abstract LiveData<Folder> getSingleLiveById(long id);

    @Transaction
    //from addFolder dialog
    public long insert(String name, long parentId, int parentIndex) {
        int orderNumber = this.getLargestOrderNumber() + 1;
        Folder folder = ModelFactory.makeFolder(name, parentId, parentIndex, orderNumber);
        return this.insert(folder);
    }

    @Query("SELECT max(orderNumber) FROM Folder")
    protected abstract int getLargestOrderNumber();

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
    public void deleteOrRestore(long[] ids, boolean isDeleted) {
        DeletedTuple[] deletedTuples = this.getDeletedTuplesByIds(ids);
        super.setDeletedTuples(deletedTuples, isDeleted);
        this.update(deletedTuples);

        super.setChildrenParentDeleted(ids, isDeleted);
    }

    @Query("SELECT id, isDeleted, deletedTime FROM Folder WHERE id IN (:ids)")
    protected abstract DeletedTuple[] getDeletedTuplesByIds(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void update(DeletedTuple[] deletedTuples);

    //from addFolder dialog
    @Query("SELECT EXISTS(SELECT name, parentId FROM Folder WHERE name =:name AND parentId = :parentId AND isDeleted = 0 AND isParentDeleted = 0)")
    public abstract boolean isExistingName(String name, long parentId);
}
