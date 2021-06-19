package com.leebeebeom.closetnote.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.leebeebeom.closetnote.data.AppDataBase;
import com.leebeebeom.closetnote.data.model.model.Size;
import com.leebeebeom.closetnote.data.repository.dao.BaseDao;
import com.leebeebeom.closetnote.data.repository.dao.SizeDao;
import com.leebeebeom.closetnote.data.tuple.ParentIdTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.SizeTuple;
import com.leebeebeom.closetnote.util.constant.SharedPreferenceKey;
import com.leebeebeom.closetnote.util.constant.ViewType;
import com.leebeebeom.closetnote.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import lombok.Getter;
import lombok.experimental.Accessors;

@Singleton
@Accessors(prefix = "m")
public class SizeRepository extends BaseRepository<Size, SizeTuple> {
    private final SizeDao mSizeDao;
    private final SharedPreferences mListSortPreference, mViewTypePreference;
    @Getter
    private final IntegerSharedPreferenceLiveData mViewTypePreferenceLive;
    private LiveData<List<List<SizeTuple>>> mDeletedClassifiedTuplesLive, mSearchTuplesLive, mDeletedSearchTuplesLive;

    @Inject
    public SizeRepository(@ApplicationContext Context context) {
        this.mSizeDao = AppDataBase.getsInstance(context).sizeDao();
        this.mListSortPreference = context.getSharedPreferences(SharedPreferenceKey.SORT_LIST.getValue(), Context.MODE_PRIVATE);
        this.mViewTypePreference = context.getSharedPreferences(SharedPreferenceKey.VIEW_TYPE.getValue(), Context.MODE_PRIVATE);
        this.mViewTypePreferenceLive = new IntegerSharedPreferenceLiveData(mViewTypePreference, SharedPreferenceKey.VIEW_TYPE.getValue(), ViewType.LIST_VIEW.getValue());
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
    public LiveData<List<List<SizeTuple>>> getSearchTuplesLive() {
        if (mSearchTuplesLive == null)
            mSearchTuplesLive = mSizeDao.getSearchTuplesLive(false);
        return mSearchTuplesLive;
    }

    public LiveData<List<List<SizeTuple>>> getDeletedSearchTuplesLive() {
        if (mDeletedSearchTuplesLive == null)
            mDeletedSearchTuplesLive = mSizeDao.getSearchTuplesLive(true);
        return mDeletedSearchTuplesLive;
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

    //from sizeFragment
    public void update(Size size) {
        new Thread(() -> mSizeDao.update(size)).start();
    }

    @Override
    protected BaseDao<Size, SizeTuple> getDao() {
        return mSizeDao;
    }

    @Override
    protected LiveData<List<Size>> getModelsLive() {
        return null;
    }

    //from sizeDelete dialog
    public void delete(long id) {
        new Thread(() -> mSizeDao.delete(id)).start();
    }

    //from move dialog
    public void move(long targetId, long[] ids) {
        new Thread(() -> mSizeDao.move(targetId, ids)).start();
    }

    //from selectedItemDelete, restore dialog
    public void deleteOrRestore(long[] ids) {
        new Thread(() -> mSizeDao.deleteOrRestore(ids)).start();
    }

    @Override
    protected SharedPreferences getSortPreference() {
        return mListSortPreference;
    }

    @Override
    protected SharedPreferences getLargestSortNumberPreference() {
        return null;
    }

    //to treeView
    public LiveData<List<ParentIdTuple>> getParentIdTuplesByIds(long[] ids) {
        MutableLiveData<List<ParentIdTuple>> parentIdTuplesLive = new MutableLiveData<>();
        new Thread(() -> {
            List<ParentIdTuple> parentIdTuples = mSizeDao.getParentIdTuplesByIds(ids);
            parentIdTuplesLive.postValue(parentIdTuples);
        }).start();
        return parentIdTuplesLive;
    }

    public void changeViewType() {
        if (getViewType() == ViewType.LIST_VIEW)
            mViewTypePreference.edit().putInt(SharedPreferenceKey.VIEW_TYPE.getValue(), ViewType.GRID_VIEW.getValue()).apply();
        else
            mViewTypePreference.edit().putInt(SharedPreferenceKey.VIEW_TYPE.getValue(), ViewType.LIST_VIEW.getValue()).apply();
    }

    public ViewType getViewType() {
        return ViewType.values()[getViewTypeInt()];
    }

    private int getViewTypeInt() {
        return mViewTypePreference.getInt(SharedPreferenceKey.VIEW_TYPE.getValue(), ViewType.LIST_VIEW.getValue());
    }

    //from listFragment(drag drop)
    public void updateTuples(List<SizeTuple> sizeTuples) {
        new Thread(() -> mSizeDao.updateTuples(sizeTuples)).start();
    }
}

