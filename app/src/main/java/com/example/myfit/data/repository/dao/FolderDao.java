package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myfit.data.model.ModelFactory;
import com.example.myfit.data.model.folder.Folder;
import com.example.myfit.data.model.tuple.CategoryFolderTuple;
import com.example.myfit.data.model.tuple.DeletedTuple;
import com.example.myfit.data.model.tuple.ParentIdTuple;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Dao
public abstract class FolderDao extends BaseDao<Folder, CategoryFolderTuple> {

    //to recycleBin
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedClassifiedTuplesLive() {
        LiveData<List<CategoryFolderTuple>> deletedTuplesLive = this.getDeletedTuplesLive();
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(deletedTuplesLive, true);

        return super.getClassifiedTuplesLive(deletedTuplesLive, contentsSizesLive);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Folder WHERE isDeleted = 1 ORDER BY deletedTime DESC")
    protected abstract LiveData<List<CategoryFolderTuple>> getDeletedTuplesLive();

    //to list
    public LiveData<List<CategoryFolderTuple>> getTuplesLiveByParentId(long parentId, int sort) {
        LiveData<List<CategoryFolderTuple>> tuplesLive = this.getTuplesLiveByParentId(parentId);
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(tuplesLive, false);

        return this.getOrderedTuplesLive(tuplesLive, contentsSizesLive, sort);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Folder WHERE parentId = :parentId AND isDeleted = 0 AND isParentDeleted = 0")
    protected abstract LiveData<List<CategoryFolderTuple>> getTuplesLiveByParentId(long parentId);

    @NotNull
    private LiveData<List<CategoryFolderTuple>> getOrderedTuplesLive(LiveData<List<CategoryFolderTuple>> tuplesLive,
                                                                     LiveData<int[]> contentsSizesLive,
                                                                     int sort) {
        return Transformations.map(contentsSizesLive, contentsSizes -> {
            List<CategoryFolderTuple> tuples = tuplesLive.getValue();
            super.setContentsSize(tuples, contentsSizes);
            super.orderTuples(sort, tuples);
            return tuples;
        });
    }

    //to searchView
    public LiveData<List<List<CategoryFolderTuple>>> getSearchTuplesListLive(String keyWord) {
        LiveData<List<CategoryFolderTuple>> searchTuplesLive = this.getSearchTuplesLive(false, keyWord);
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(searchTuplesLive, false);

        return this.getClassifiedTuplesLive(searchTuplesLive, contentsSizesLive);
    }

    //to recycleBin search
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedSearchFolderTuplesListLive(String keyWord) {
        LiveData<List<CategoryFolderTuple>> deletedSearchTuplesLive = this.getSearchTuplesLive(true, keyWord);
        LiveData<int[]> contentsSizesLive = super.getContentsSizesLive(deletedSearchTuplesLive, true);

        return this.getClassifiedTuplesLive(deletedSearchTuplesLive, contentsSizesLive);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Folder " +
            "WHERE isDeleted = :isDeleted AND isParentDeleted = 0 AND name LIKE :keyWord ORDER BY name")
    protected abstract LiveData<List<CategoryFolderTuple>> getSearchTuplesLive(boolean isDeleted, String keyWord);

    //to treeView (disposable)
    @Transaction
    public List<CategoryFolderTuple> getTuplesByParentIndex(byte parentIndex, int sort) {
        List<CategoryFolderTuple> tuples = this.getTuplesByParentIndex(parentIndex);
        long[] ids = super.getItemIds(tuples);
        int[] contentsSizes = getContentsSizesByParentIds(ids);
        super.setContentsSize(tuples, contentsSizes);
        super.orderTuples(sort, tuples);
        return tuples;
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Folder WHERE parentIndex = :parentIndex AND isDeleted = 0 AND isParentDeleted = 0")
    protected abstract List<CategoryFolderTuple> getTuplesByParentIndex(byte parentIndex);

    @Transaction
    //to treeView
    public CategoryFolderTuple getTupleById(long id) {
        CategoryFolderTuple tuple = this.getTupleById2(id);
        int contentsSize = getContentsSizeByParentId(id);
        tuple.setContentsSize(contentsSize);
        return tuple;
    }

    @Query("SELECT id, parentIndex, orderNumber, name, contentsSize FROM Folder WHERE id = :id AND isDeleted = 0 AND isParentDeleted = 0")
    protected abstract CategoryFolderTuple getTupleById2(long id);

    //to list
    @Query("SELECT * FROM Folder WHERE id = :id")
    public abstract LiveData<Folder> getSingleLiveById(long id);

    @Transaction
    public long insert(String name, long parentId, byte parentIndex) {
        int orderNumber = this.getLargestOrderNumber() + 1;
        Folder folder = ModelFactory.makeFolder(name, parentId, parentIndex, orderNumber);
        return insert(folder);
    }

    @Query("SELECT max(orderNumber) FROM Folder")
    protected abstract int getLargestOrderNumber();
    @Transaction
    public void update(long id, String name) {
        CategoryFolderTuple tuple = this.getTupleById2(id);
        tuple.setName(name);
        this.update(tuple);
    }

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void update(CategoryFolderTuple tuple);

    @Transaction
    public void move(long targetId, long[] folderIds) {
        ParentIdTuple[] parentIdTuples = this.getParentIdTuplesByIds(folderIds);
        Arrays.stream(parentIdTuples)
                .forEach(parentIdTuple -> parentIdTuple.setParentId(targetId));
        this.update(parentIdTuples);
    }

    @Query("SELECT id, parentId FROM Folder WHERE id IN (:ids)")
    protected abstract ParentIdTuple[] getParentIdTuplesByIds(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void update(ParentIdTuple[] parentIdTuples);

    @Transaction
    public void deleteOrRestore(long[] folderIds, boolean isDeleted) {
        DeletedTuple[] deletedTuples = this.getDeletedTuplesByIds(folderIds);
        super.setDeletedTuples(deletedTuples, isDeleted);
        this.update(deletedTuples);

        super.setChildrenParentDeleted(folderIds, isDeleted);
    }

    @Query("SELECT id, isDeleted FROM Folder WHERE id IN (:ids)")
    protected abstract DeletedTuple[] getDeletedTuplesByIds(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Folder.class)
    protected abstract void update(DeletedTuple[] deletedTuples);

    //from addFolder dialog
    @Query("SELECT EXISTS(SELECT name, parentId FROM Folder WHERE name =:folderName AND parentId = :parentId AND isDeleted = 0 AND isParentDeleted = 0)")
    public abstract boolean isExistingFolderName(String folderName, long parentId);
}
