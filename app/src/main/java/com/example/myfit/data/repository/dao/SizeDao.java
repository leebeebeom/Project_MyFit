package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myfit.data.model.size.Size;
import com.example.myfit.data.model.size.SizeTuple;
import com.example.myfit.data.model.tuple.DeletedTuple;
import com.example.myfit.data.model.tuple.ParentIdTuple;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Dao
public abstract class SizeDao extends BaseDao<SizeTuple> {

    //to list
    public LiveData<List<SizeTuple>> getTuplesLiveByParentId(long parentId) {
        LiveData<List<SizeTuple>> tuplesLive = getTuplesLiveByParentId2(parentId);
        return Transformations.map(tuplesLive, tuples -> tuples);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, brand, imageUri, isFavorite, deletedTime FROM Size WHERE parentId = :parentId AND deleted = 0 AND parentDeleted = 0")
    protected abstract LiveData<List<SizeTuple>> getTuplesLiveByParentId2(long parentId);

    //to recycleBin
    public LiveData<List<List<SizeTuple>>> getDeletedClassifiedTuplesLive() {
        LiveData<List<SizeTuple>> tuplesLive = this.getDeletedTuplesLive();
        return Transformations.map(tuplesLive, this::getClassifiedSizeTuplesByParentIndex);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, brand, imageUri, isFavorite, deletedTime FROM Size " +
            "WHERE deleted = 1 AND parentDeleted = 0 ORDER BY deletedTime DESC")
    protected abstract LiveData<List<SizeTuple>> getDeletedTuplesLive();

    @NotNull
    private List<List<SizeTuple>> getClassifiedSizeTuplesByParentIndex(List<SizeTuple> tuples) {
        List<LinkedList<SizeTuple>> classifiedLinkedList = Arrays.asList(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
        List<List<SizeTuple>> classifiedList = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        try {
            tuples.forEach(tuple -> classifiedLinkedList.get(tuple.getParentIndex()).add(tuple));
            for (int i = 0; i < 4; i++) classifiedList.get(i).addAll(classifiedLinkedList.get(i));
        } catch (NullPointerException e) {
            logE(e);
        }

        return classifiedList;
    }

    //to search
    public LiveData<List<List<SizeTuple>>> getSearchTuplesLive() {
        LiveData<List<SizeTuple>> tuplesLive = getSearchTuplesLive2(false);
        return Transformations.map(tuplesLive, this::getClassifiedSizeTuplesByParentIndex);
    }

    //to recycleBin search
    public LiveData<List<List<SizeTuple>>> getDeletedSearchTuplesLive() {
        LiveData<List<SizeTuple>> deletedTuplesLive = getSearchTuplesLive2(true);
        return Transformations.map(deletedTuplesLive, this::getClassifiedSizeTuplesByParentIndex);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, brand, imageUri, isFavorite, deletedTime FROM Size " +
            "WHERE deleted = :deleted AND parentDeleted = 0 AND name || brand ORDER BY name")
    protected abstract LiveData<List<SizeTuple>> getSearchTuplesLive2(boolean deleted);

    //to sizeFragment(disposable)
    public List<String> getBrands() {
        List<String> brands = this.getBrands2();
        brands.sort(String::compareTo);
        return brands;
    }

    @Query("SELECT brand FROM Size WHERE deleted = 0 AND parentDeleted = 0")
    protected abstract List<String> getBrands2();

    //to sizeFragment
    @Query("SELECT * FROM Size WHERE id = :id")
    public abstract Size getSizeById(long id);

    //from sizeFragment
    public void insertSize(@NotNull Size size) {
        int orderNumber = getLargestOrder() + 1;
        size.setOrderNumber(orderNumber);
        this.insert(size);
    }

    @Query("SELECT max(orderNumber) FROM Size")
    protected abstract int getLargestOrder();

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    public abstract void update(SizeTuple sizeTuple);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    public abstract void update(List<SizeTuple> sizeTuples);

    @Transaction
    public void delete(long id) {
        DeletedTuple deletedTuple = getDeletedTuple(id);
        deletedTuple.setDeleted(true);
        deletedTuple.setDeletedTime(super.getCurrentTime());
        updateDeletedTuple(deletedTuple);
    }

    @Query("SELECT id, deleted, deletedTime FROM Size WHERE id = :id")
    protected abstract DeletedTuple getDeletedTuple(long id);

    @Insert
    protected abstract long insert(Size size);

    @Transaction
    //from move dialog
    public void move(long targetId, long[] ids) {
        ParentIdTuple[] parentIdTuples = this.getParentIdTuplesByIds(ids);
        Arrays.stream(parentIdTuples).
                forEach(parentIdTuple -> parentIdTuple.setParentId(targetId));
        this.update(parentIdTuples);
    }

    @Query("SELECT id, parentId FROM Size WHERE id IN (:ids)")
    public abstract ParentIdTuple[] getParentIdTuplesByIds(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    protected abstract void update(ParentIdTuple[] parentIdTuples);

    @Transaction
    //from selectedItemDelete, restore dialog
    public void deleteOrRestore(long[] ids, boolean deleted) {
        DeletedTuple[] sizeDeletedTuples = getDeletedTuples(ids);
        super.setDeletedTuples(sizeDeletedTuples, deleted);
        this.updateDeletedTuples(sizeDeletedTuples);
    }

    @Query("SELECT id, deleted, deletedTime FROM Size WHERE id IN (:ids)")
    protected abstract DeletedTuple[] getDeletedTuples(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    protected abstract void updateDeletedTuples(DeletedTuple[] deletedTuples);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    protected abstract void updateDeletedTuple(DeletedTuple deletedTuple);
}
