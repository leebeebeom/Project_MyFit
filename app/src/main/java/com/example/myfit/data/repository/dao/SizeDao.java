package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myfit.data.model.model.Size;
import com.example.myfit.data.tuple.DeletedTuple;
import com.example.myfit.data.tuple.ParentIdTuple;
import com.example.myfit.data.tuple.tuple.SizeTuple;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Dao
public abstract class SizeDao extends BaseDao<SizeTuple> {

    //to list
    @Query("SELECT id, parentIndex, sortNumber, name, brand, imageUri, favorite, deletedTime " +
            "FROM Size WHERE parentId = :parentId AND deleted = 0 AND parentDeleted = 0")
    public abstract LiveData<List<SizeTuple>> getTuplesLiveByParentId(long parentId);

    //to recycleBin
    public LiveData<List<List<SizeTuple>>> getDeletedClassifiedTuplesLive() {
        LiveData<List<SizeTuple>> tuplesLive = this.getDeletedTuplesLive();
        return Transformations.map(tuplesLive, this::getClassifiedSizeTuples);
    }

    @Query("SELECT id, parentIndex, sortNumber, name, brand, imageUri, favorite, deletedTime FROM Size " +
            "WHERE deleted = 1 AND parentDeleted = 0 ORDER BY deletedTime DESC")
    protected abstract LiveData<List<SizeTuple>> getDeletedTuplesLive();

    private List<List<SizeTuple>> getClassifiedSizeTuples(List<SizeTuple> tuples) {
        if (tuples != null) {
            List<LinkedList<SizeTuple>> classifiedLinkedList = Arrays.asList(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
            List<List<SizeTuple>> classifiedList = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

            tuples.forEach(tuple -> classifiedLinkedList.get(tuple.getParentIndex()).add(tuple));
            for (int i = 0; i < 4; i++) classifiedList.get(i).addAll(classifiedLinkedList.get(i));
            return classifiedList;
        } else return null;
    }

    //to search, recycleBin search
    public LiveData<List<List<SizeTuple>>> getSearchTuplesLive(boolean deleted) {
        LiveData<List<SizeTuple>> tuplesLive = getSearchTuplesLive2(deleted);
        return Transformations.map(tuplesLive, this::getClassifiedSizeTuples);
    }

    @Query("SELECT id, parentIndex, sortNumber, name, brand, imageUri, favorite, deletedTime FROM Size " +
            "WHERE deleted = :deleted AND parentDeleted = 0 AND name || brand ORDER BY name")
    protected abstract LiveData<List<SizeTuple>> getSearchTuplesLive2(boolean deleted);

    //to sizeFragment
    public LiveData<Set<String>> getBrands() {
        LiveData<List<String>> brands = this.getBrands2();
        return Transformations.map(brands, input -> {
            input.forEach(s -> s = s.trim());
            return new LinkedHashSet<>(input);
        });
    }

    @Query("SELECT brand FROM Size WHERE deleted = 0 AND parentDeleted = 0 ORDER BY brand")
    protected abstract LiveData<List<String>> getBrands2();

    //to sizeFragment
    @Query("SELECT * FROM Size WHERE id = :id")
    public abstract LiveData<Size> getSizeById(long id);

    //from sizeFragment
    public void insertSize(@NotNull Size size) {
        int sortNumber = getLargestSort() + 1;
        size.setSortNumber(sortNumber);
        this.insert(size);
    }

    @Query("SELECT max(sortNumber) FROM Size")
    protected abstract int getLargestSort();

    //from listFragment(favorite)
    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    public abstract void update(SizeTuple sizeTuple);

    //from listFragment(drag drop)
    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    public abstract void updateTuples(List<SizeTuple> sizeTuples);

    //from sizeFragment
    @Transaction
    public void deleteOrRestore(long id) {
        DeletedTuple deletedTuple = getDeletedTuple(id);
        deletedTuple.setDeleted(!deletedTuple.isDeleted());
        deletedTuple.setDeletedTime(super.getCurrentTime());
        updateDeletedTuple(deletedTuple);
    }

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    protected abstract void updateDeletedTuple(DeletedTuple deletedTuple);

    @Query("SELECT id, deleted, deletedTime FROM Size WHERE id = :id")
    protected abstract DeletedTuple getDeletedTuple(long id);

    @Insert
    protected abstract long insert(Size size);

    @Transaction
    //from move dialog
    public void move(long targetId, long[] ids) {
        List<ParentIdTuple> parentIdTuples = this.getParentIdTuplesByIds(ids);
        parentIdTuples.forEach(parentIdTuple -> parentIdTuple.setParentId(targetId));
        this.updateParentIdTuple(parentIdTuples);
    }

    //to treeView(selectedSizes)
    @Query("SELECT id, parentId FROM Size WHERE id IN (:ids)")
    public abstract List<ParentIdTuple> getParentIdTuplesByIds(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    protected abstract void updateParentIdTuple(List<ParentIdTuple> parentIdTuples);

    @Transaction
    //from selectedItemDelete, restore dialog
    public void deleteOrRestore(long[] ids) {
        List<DeletedTuple> sizeDeletedTuples = getDeletedTuples(ids);
        super.setDeletedTuples(sizeDeletedTuples);
        this.updateDeletedTuples(sizeDeletedTuples);
    }

    @Query("SELECT id, deleted, deletedTime FROM Size WHERE id IN (:ids)")
    protected abstract List<DeletedTuple> getDeletedTuples(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    protected abstract void updateDeletedTuples(List<DeletedTuple> deletedTuples);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(Size size);
}
