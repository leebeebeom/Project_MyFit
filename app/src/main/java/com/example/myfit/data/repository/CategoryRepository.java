package com.example.myfit.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.model.Category;
import com.example.myfit.data.repository.dao.CategoryDao;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.util.constant.SharedPreferenceKey;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ViewModelScoped;
import lombok.Getter;
import lombok.experimental.Accessors;

@Singleton
@Accessors(prefix = "m")
public class CategoryRepository extends BaseRepository<CategoryTuple> {
    private final CategoryDao mCategoryDao;
    private final SharedPreferences mMainSortPreference;
    @Getter
    private final IntegerSharedPreferenceLiveData mMainSortPreferenceLive;
    private MutableLiveData<Boolean> mExistingNameLive;
    private LiveData<List<List<CategoryTuple>>> mClassifiedTuplesLive, mDeletedClassifiedTuplesLive, mDeletedSearchTuplesLive;
    private MutableLiveData<CategoryTuple> mAddedTupleLive;
    private MutableLiveData<List<CategoryTuple>> mAddedTuplesLive;

    @Inject
    public CategoryRepository(@ApplicationContext Context context) {
        this.mCategoryDao = AppDataBase.getsInstance(context).categoryDao();
        this.mMainSortPreference = context.getSharedPreferences(SharedPreferenceKey.SORT_MAIN.getValue(), Context.MODE_PRIVATE);
        this.mMainSortPreferenceLive = new IntegerSharedPreferenceLiveData(mMainSortPreference, SharedPreferenceKey.SORT.getValue(), Sort.SORT_CUSTOM.getValue());
    }

    //from appDataBase
    public void insert(List<Category> categories) {
        new Thread(() -> mCategoryDao.insert(categories)).start();
    }

    //to main
    public LiveData<List<List<CategoryTuple>>> getClassifiedTuplesLive() {
        if (mClassifiedTuplesLive == null)
            mClassifiedTuplesLive = mCategoryDao.getClassifiedTuplesLive();
        return mClassifiedTuplesLive;
    }

    //to recycleBin
    public LiveData<List<List<CategoryTuple>>> getDeletedClassifiedTuplesLive() {
        if (mDeletedClassifiedTuplesLive == null)
            mDeletedClassifiedTuplesLive = mCategoryDao.getDeletedClassifiedTuplesLive();
        return mDeletedClassifiedTuplesLive;
    }

    //to recycleBin search
    public LiveData<List<List<CategoryTuple>>> getDeletedSearchTuplesLive() {
        if (mDeletedSearchTuplesLive == null)
            mDeletedSearchTuplesLive = mCategoryDao.getDeletedSearchTuplesLive();
        return mDeletedSearchTuplesLive;
    }

    //to listFragment
    public LiveData<CategoryTuple> getTupleLiveById(long id) {
        return mCategoryDao.getTupleLiveById(id);
    }

    //to treeView(disposable)
    public LiveData<List<CategoryTuple>> getTuplesByParentIndex(int parentIndex) {
        MutableLiveData<List<CategoryTuple>> tuplesLive = new MutableLiveData<>();
        new Thread(() -> {
            Sort sort = getSort();
            List<CategoryTuple> tuples = mCategoryDao.getTuplesByParentIndex(parentIndex, sort);
            tuplesLive.postValue(tuples);
        }).start();
        return tuplesLive;
    }

    //to treeView
    public MutableLiveData<CategoryTuple> getAddedTupleLive() {
        mAddedTupleLive = new MutableLiveData<>();
        return mAddedTupleLive;
    }

    //from addCategory dialog
    public void insert(String name, int parentIndex) {
        new Thread(() -> {
            long insertId = mCategoryDao.insert(name, parentIndex);
            if (mAddedTupleLive != null) {
                CategoryTuple addedTuple = mCategoryDao.getTupleById(insertId);
                mAddedTupleLive.postValue(addedTuple);
            }
        }).start();
    }

    public MutableLiveData<List<CategoryTuple>> getAddedTuplesLive() {
        mAddedTuplesLive = new MutableLiveData<>();
        return mAddedTuplesLive;
    }

    //from restore dialog(disposable)
    public LiveData<List<Long>> insertRestoreCategories(@NotNull List<Integer> parentIndex) {
        MutableLiveData<List<Long>> insertIdsLive = new MutableLiveData<>();
        new Thread(() -> {
            List<Long> insertIds = mCategoryDao.insertRestoreCategories(parentIndex);
            List<CategoryTuple> addedTuples = mCategoryDao.getTuplesByIds(insertIds);
            mAddedTuplesLive.postValue(addedTuples);
        }).start();
        return insertIdsLive;
    }

    //from editCategoryName dialog
    public void update(long id, String name) {
        new Thread(() -> mCategoryDao.update(id, name)).start();
    }

    @Override
    //from adapter drag drop
    public void updateTuples(List<CategoryTuple> categoryTuples) {
        new Thread(() -> mCategoryDao.updateTuples(categoryTuples)).start();
    }

    @Override
    //from delete dialog, restore dialog
    public void deleteOrRestore(long[] ids) {
        new Thread(() -> mCategoryDao.deleteOrRestore(ids)).start();
    }

    @Override
    protected SharedPreferences getPreference() {
        return mMainSortPreference;
    }

    //to addCategory Dialog
    public MutableLiveData<Boolean> getExistingNameLive() {
        mExistingNameLive = new MutableLiveData<>();
        return mExistingNameLive;
    }

    //from addCategory Dialog
    public void isExistingName(String name, int parentIndex) {
        new Thread(() -> {
            boolean isExistName = mCategoryDao.isExistingName(name.trim(), parentIndex);
            if (mExistingNameLive != null)
                mExistingNameLive.postValue(isExistName);
        }).start();
    }
}

