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
import com.example.myfit.util.SortUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Dao
public abstract class SizeDao extends BaseDao<SizeTuple> {

    //to recycleBin
    public LiveData<List<List<SizeTuple>>> getDeletedClassifiedTuplesLive() {
        LiveData<List<SizeTuple>> tuplesLive = this.getDeletedTuplesLive();

        return getClassifiedTuplesLive(tuplesLive);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, brand, imageUri, isFavorite, deletedTime FROM Size " +
            "WHERE isDeleted = 1 AND isParentDeleted = 0 ORDER BY deletedTime DESC")
    protected abstract LiveData<List<SizeTuple>> getDeletedTuplesLive();

    @NotNull
    private LiveData<List<List<SizeTuple>>> getClassifiedTuplesLive(LiveData<List<SizeTuple>> tuplesLive) {
        return Transformations.map(tuplesLive, this::getClassifiedSizeTuplesByParentIndex);
    }

    private void orderSizeTuples(int sort, List<SizeTuple> tuples) {
        try {
            SortUtil.orderSizeTuples(sort, tuples);
        } catch (NullPointerException e) {
            logE(e);
        }
    }

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

    //to list
    public LiveData<List<SizeTuple>> getTuplesLiveByParentId(long parentId, int sort) {
        LiveData<List<SizeTuple>> tuplesLive = getTuplesLiveByParentId(parentId);
        return Transformations.map(tuplesLive, tuples -> {
            this.orderSizeTuples(sort, tuples);
            return tuples;
        });
    }

    @Query("SELECT id, parentIndex, orderNumber, name, brand, imageUri, isFavorite, deletedTime FROM Size WHERE parentId = :parentId AND isDeleted = 0 AND isParentDeleted = 0")
    protected abstract LiveData<List<SizeTuple>> getTuplesLiveByParentId(long parentId);

    //to search
    public LiveData<List<List<SizeTuple>>> getSearchTuplesLive(String keyWord) {
        LiveData<List<SizeTuple>> tuplesLive = getSearchTuplesLive2(false, keyWord);

        return getClassifiedTuplesLive(tuplesLive);
    }

    //to recycleBin search
    public LiveData<List<List<SizeTuple>>> getDeletedSearchTuplesLive(String keyWord) {
        LiveData<List<SizeTuple>> deletedTuplesLive = getSearchTuplesLive2(true, keyWord);
        return getClassifiedTuplesLive(deletedTuplesLive);
    }

    @Query("SELECT id, parentIndex, orderNumber, name, brand, imageUri, isFavorite, deletedTime FROM Size " +
            "WHERE isDeleted = :isDeleted AND isParentDeleted = 0 AND name || brand LIKE :keyWord ORDER BY name")
    protected abstract LiveData<List<SizeTuple>> getSearchTuplesLive2(boolean isDeleted, String keyWord);

    //to sizeFragment(disposable)
    public List<String> getBrands() {
        List<String> brands = this.getBrands2();
        brands.sort(String::compareTo);
        return brands;
    }

    @Query("SELECT brand FROM Size WHERE isDeleted = 0 AND isParentDeleted = 0")
    protected abstract List<String> getBrands2();

    //to sizeFragment
    @Query("SELECT * FROM Size WHERE id = :id")
    public abstract Size getSizeById(long id);

    public void insertSize(@NotNull Size size) {
        int orderNumber = getLargestOrder() + 1;
        size.setOrderNumber(orderNumber);
        this.insert(size);
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(Size size);

    @Insert
    protected abstract long insert(Size size);

    @Transaction
    //from move dialog
    public void move(long targetId, long[] ids) {
        ParentIdTuple[] parentIdTuples = this.getParentIdTuples(ids);
        Arrays.stream(parentIdTuples).
                forEach(parentIdTuple -> parentIdTuple.setParentId(targetId));
        this.update(parentIdTuples);
    }

    @Query("SELECT id, parentId FROM Size WHERE id IN (:ids)")
    protected abstract ParentIdTuple[] getParentIdTuples(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    protected abstract void update(ParentIdTuple[] parentIdTuples);

    @Transaction
    //from selectedItemDelete, restore dialog
    public void deleteOrRestore(long[] ids, boolean isDeleted) {
        DeletedTuple[] sizeDeletedTuples = getDeletedTuples(ids);
        super.setDeletedTuples(sizeDeletedTuples, isDeleted);
        this.updateDeletedTuples(sizeDeletedTuples);
    }

    @Query("SELECT id, isDeleted, deletedTime FROM Size WHERE id IN (:ids)")
    protected abstract DeletedTuple[] getDeletedTuples(long[] ids);

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = Size.class)
    protected abstract void updateDeletedTuples(DeletedTuple[] deletedTuples);

    @Query("SELECT max(orderNumber) FROM Size")
    protected abstract int getLargestOrder();
}
