package com.example.myfit.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.size.Size;
import com.example.myfit.data.model.size.SizeTuple;
import com.example.myfit.data.repository.dao.SizeDao;
import com.example.myfit.util.IntegerSharedPreferenceLiveData;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.constant.SortValue;

import java.util.List;

public class SizeRepository extends BaseRepository{
    private final SizeDao sizeDao;
    private final SharedPreferences listSortPreference;

    public SizeRepository(Context context) {
        this.sizeDao = AppDataBase.getsInstance(context).sizeDao();
        this.listSortPreference = context.getSharedPreferences(Sort.SORT_LIST.getText(), Context.MODE_PRIVATE);
    }

    //to list
    public LiveData<List<SizeTuple>> getTuplesLiveByParentId(long parentId) {
        IntegerSharedPreferenceLiveData listSortPreferenceLive =
                new IntegerSharedPreferenceLiveData(listSortPreference, Sort.SORT_LIST.getText(), SortValue.SORT_CUSTOM.getValue());
        return Transformations.switchMap(listSortPreferenceLive, sort -> sizeDao.getTuplesLiveByParentId(parentId, sort));
    }

    //to recycleBin
    public LiveData<List<List<SizeTuple>>> getDeletedClassifiedTuplesLive() {
        return sizeDao.getDeletedClassifiedTuplesLive();
    }

    //to search
    public LiveData<List<List<SizeTuple>>> getSearchTuplesLive() {
        return sizeDao.getSearchTuplesLive();
    }

    //to recycleBin search
    public LiveData<List<List<SizeTuple>>> getDeletedSearchTuplesLive() {
        return sizeDao.getDeletedSearchTuplesLive();
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

    @Override
    protected String getPreferenceKey() {
        return Sort.SORT_LIST.getText();
    }
}

