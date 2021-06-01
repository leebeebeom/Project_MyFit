package com.example.myfit.data.repository;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myfit.data.model.model.Size;
import com.example.myfit.data.repository.dao.SizeDao;
import com.example.myfit.data.tuple.ParentIdTuple;
import com.example.myfit.data.tuple.tuple.SizeTuple;
import com.example.myfit.di.Qualifiers;
import com.example.myfit.util.constant.ViewType;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ViewModelScoped;

import static com.example.myfit.di.DataModule.VIEW_TYPE;

@ViewModelScoped
public class SizeRepository extends BaseRepository<SizeTuple> {
    private final SizeDao mSizeDao;
    private final SharedPreferences mListSortPreference, mViewTypePreference;
    private LiveData<List<List<SizeTuple>>> mDeletedClassifiedTuplesLive, mSearchTuplesLive, mDeletedSearchTuplesLive;

    @Inject
    public SizeRepository(SizeDao sizeDao,
                          @Qualifiers.ListSortPreference SharedPreferences listSortPreference,
                          @Qualifiers.ViewTypePreference SharedPreferences viewTypePreference) {
        this.mSizeDao = sizeDao;
        this.mListSortPreference = listSortPreference;
        this.mViewTypePreference = viewTypePreference;
    }

    //to list
    public LiveData<List<SizeTuple>> getTuplesLiveByParentId(long parentId) {
        return mSizeDao.getTuplesLiveByParentId(parentId);
    }

    //to recycleBin
    public LiveData<List<List<SizeTuple>>> getDeletedClassifiedTuplesLive() {
        if (mDeletedClassifiedTuplesLive == null)
            mDeletedClassifiedTuplesLive = mSizeDao.getDeletedClassifiedTuplesLive();
        return mDeletedClassifiedTuplesLive;
    }

    //to search, recycleBin search
    public LiveData<List<List<SizeTuple>>> getSearchTuplesLive(boolean deleted) {
        if (!deleted) {
            if (mSearchTuplesLive == null)
                mSearchTuplesLive = mSizeDao.getSearchTuplesLive(deleted);
            return mSearchTuplesLive;
        } else {
            if (mDeletedSearchTuplesLive == null)
                mDeletedSearchTuplesLive = mSizeDao.getSearchTuplesLive(deleted);
            return mDeletedSearchTuplesLive;
        }
    }

    //to sizeFragment
    public LiveData<Set<String>> getBrands() {
        return mSizeDao.getBrands();
    }

    //to sizeFragment
    public LiveData<Size> getSizeById(long id) {
        return mSizeDao.getSizeById(id);
    }

    //from sizeFragment
    public void insert(Size size) {
        new Thread(() -> mSizeDao.insertSize(size)).start();
    }

    //from listFragment(favorite)
    public void update(SizeTuple sizeTuple) {
        new Thread(() -> mSizeDao.update(sizeTuple)).start();
    }

    //from sizeDelete dialog
    public void delete(long id) {
        new Thread(() -> mSizeDao.deleteOrRestore(id)).start();
    }

    //from move dialog
    public void move(long targetId, long[] ids) {
        new Thread(() -> mSizeDao.move(targetId, ids)).start();
    }

    @Override
    //from selectedItemDelete, restore dialog
    public void deleteOrRestore(long[] ids) {
        new Thread(() -> mSizeDao.deleteOrRestore(ids)).start();
    }

    @Override
    protected SharedPreferences getPreference() {
        return mListSortPreference;
    }

    //to treeView
    public LiveData<ParentIdTuple[]> getParentIdTuplesByIds(long[] ids) {
        MutableLiveData<ParentIdTuple[]> parentIdTuplesLive = new MutableLiveData<>();
        new Thread(() -> {
            ParentIdTuple[] parentIdTuples = mSizeDao.getParentIdTuplesByIds(ids);
            parentIdTuplesLive.postValue(parentIdTuples);
        });
        return parentIdTuplesLive;
    }

    public void changeViewType() {
        if (getViewType() == ViewType.LIST_VIEW)
            mViewTypePreference.edit().putInt(VIEW_TYPE, ViewType.GRID_VIEW.getValue()).apply();
        else mViewTypePreference.edit().putInt(VIEW_TYPE, ViewType.LIST_VIEW.getValue()).apply();
    }

    public ViewType getViewType() {
        return ViewType.values()[getViewTypeInt()];
    }

    private int getViewTypeInt() {
        return mViewTypePreference.getInt(VIEW_TYPE, ViewType.LIST_VIEW.getValue());
    }

    //from listFragment(drag drop)
    public void updateTuples(List<SizeTuple> sizeTuples) {
        new Thread(() -> mSizeDao.update(sizeTuples)).start();
    }
}

