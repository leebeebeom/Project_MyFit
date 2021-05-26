package com.example.myfit.data.repository;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.model.size.Size;
import com.example.myfit.data.model.size.SizeTuple;
import com.example.myfit.data.model.tuple.ParentIdTuple;
import com.example.myfit.data.repository.dao.SizeDao;
import com.example.myfit.di.Qualifiers;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import java.util.List;

import javax.inject.Inject;

public class SizeRepository extends BaseRepository {
    private final SizeDao sizeDao;
    private final SharedPreferences listSortPreference;
    private final IntegerSharedPreferenceLiveData listSortPreferenceLive;
    private LiveData<List<SizeTuple>> tuplesLiveByParentId;
    private long parentId;
    private LiveData<List<List<SizeTuple>>> deletedClassifiedTuplesLive;
    private LiveData<List<List<SizeTuple>>> searchTuplesLive;
    private LiveData<List<List<SizeTuple>>> deletedSearchTuplesLive;

    @Inject
    public SizeRepository(SizeDao sizeDao,
                          @Qualifiers.ListSortPreferenceLive SharedPreferences listSortPreference,
                          @Qualifiers.ListSortPreferenceLive IntegerSharedPreferenceLiveData listSortPreferenceLive) {
        this.sizeDao = sizeDao;
        this.listSortPreference = listSortPreference;
        this.listSortPreferenceLive = listSortPreferenceLive;
    }

    //to list
    public LiveData<List<SizeTuple>> getTuplesLiveByParentId(long parentId) {
        if (this.parentId != parentId) {
            this.parentId = parentId;
            tuplesLiveByParentId = Transformations.switchMap(listSortPreferenceLive, sort -> sizeDao.getTuplesLiveByParentId(parentId, Sort.values()[sort]));
        }
        return tuplesLiveByParentId;
    }

    //to recycleBin
    public LiveData<List<List<SizeTuple>>> getDeletedClassifiedTuplesLive() {
        if (deletedClassifiedTuplesLive == null)
            deletedClassifiedTuplesLive = sizeDao.getDeletedClassifiedTuplesLive();
        return deletedClassifiedTuplesLive;
    }

    //to search
    public LiveData<List<List<SizeTuple>>> getSearchTuplesLive() {
        if (searchTuplesLive == null)
            searchTuplesLive = sizeDao.getSearchTuplesLive();
        return searchTuplesLive;
    }

    //to recycleBin search
    public LiveData<List<List<SizeTuple>>> getDeletedSearchTuplesLive() {
        if (deletedSearchTuplesLive == null)
            deletedSearchTuplesLive = sizeDao.getDeletedSearchTuplesLive();
        return deletedSearchTuplesLive;
    }

    //to sizeFragment(disposable)
    public LiveData<List<String>> getBrands() {
        MutableLiveData<List<String>> brandsLive = new MutableLiveData<>();
        new Thread(() -> {
                List<String> brands = sizeDao.getBrands();
                brandsLive.postValue(brands);
        }).start();
        return brandsLive;
    }

    //to sizeFragment
    public LiveData<Size> getSizeById(long id) {
        MutableLiveData<Size> sizeLive = new MutableLiveData<>();
        new Thread(() -> {
                Size size = sizeDao.getSizeById(id);
                sizeLive.postValue(size);
        }).start();
        return sizeLive;
    }

    //from sizeFragment
    public void insert(Size size) {
        new Thread(() -> sizeDao.insertSize(size)).start();
    }

    //from sizeFragment
    public void update(Size size) {
        new Thread(() -> sizeDao.update(size)).start();
    }

    //from sizeDelete dialog
    public void delete(long id){
        new Thread(() -> sizeDao.delete(id)).start();
    }

    //from move dialog
    public void move(long targetId, long[] ids) {
        new Thread(() -> sizeDao.move(targetId, ids)).start();
    }

    @Override
    //from selectedItemDelete, restore dialog
    public void deleteOrRestore(long[] ids, boolean isDeleted) {
        new Thread(() -> sizeDao.deleteOrRestore(ids, isDeleted)).start();
    }

    @Override
    protected SharedPreferences getPreference() {
        return listSortPreference;
    }

    //to treeView
    public LiveData<ParentIdTuple[]> getParentIdTuplesByIds(long[] ids) {
        MutableLiveData<ParentIdTuple[]> parentIdTuplesLive = new MutableLiveData<>();
        new Thread(() -> {
            ParentIdTuple[] parentIdTuples = sizeDao.getParentIdTuplesByIds(ids);
            parentIdTuplesLive.postValue(parentIdTuples);
        });
        return parentIdTuplesLive;
    }
}

