package com.example.myfit.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.model.Category;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.data.repository.dao.CategoryDao;
import com.example.myfit.di.Qualifiers;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ViewModelScoped;

@ViewModelScoped
public class CategoryRepository extends BaseRepository<CategoryTuple> {
    private final CategoryDao mCategoryDao;
    private SharedPreferences mMainSortPreference;
    private IntegerSharedPreferenceLiveData mMainSortPreferenceLive;
    private MutableLiveData<Long> mInsertIdLive;
    private MutableLiveData<Boolean> mExistingNameLive;
    private LiveData<List<List<CategoryTuple>>> mClassifiedTuplesLive, mDeletedClassifiedTuplesLive, mDeletedSearchTuplesLive;

    @Inject
    public CategoryRepository(CategoryDao categoryDao,
                              @Qualifiers.MainSortPreference SharedPreferences mainSortPreference){
        this.mCategoryDao = categoryDao;
        this.mMainSortPreference = mainSortPreference;
    }

    //for appDataBase
    public CategoryRepository(Context context) {
        this.mCategoryDao = AppDataBase.getsInstance(context).categoryDao();
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

    //to treeView(disposable)
    public LiveData<CategoryTuple> getTupleById(long id) {
        MutableLiveData<CategoryTuple> categoryTupleLive = new MutableLiveData<>();
        new Thread(() -> {
            CategoryTuple tuple = mCategoryDao.getTupleById(id);
            categoryTupleLive.postValue(tuple);
        }).start();
        return categoryTupleLive;
    }

    //to treeView
    public MutableLiveData<Long> getInsertIdLive() {
        mInsertIdLive = new MutableLiveData<>();
        return mInsertIdLive;
    }

    //from addCategory dialog(disposable)
    public void insert(String name, int parentIndex) {
        new Thread(() -> {
            long insertId = mCategoryDao.insert(name, parentIndex);
            if (mInsertIdLive != null) mInsertIdLive.postValue(insertId);
        }).start();
    }

    //from appDataBase
    public void insert(Category[] categories) {
        new Thread(() -> mCategoryDao.insert(categories)).start();
    }

    //from restore dialog(disposable)
    public LiveData<Long[]> insertRestoreCategories(@NotNull int[] parentIndex) {
        MutableLiveData<Long[]> insertIdsLive = new MutableLiveData<>();
        new Thread(() -> {
            Long[] insertIds = mCategoryDao.insertRestoreCategories(parentIndex);
            if (mInsertIdLive != null) insertIdsLive.postValue(insertIds);
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
        new Thread(() -> mCategoryDao.update(categoryTuples)).start();
    }

    @Override
    //from delete dialog, restore dialog
    public void deleteOrRestore(long[] ids, boolean isDeleted) {
        new Thread(() -> mCategoryDao.deleteOrRestore(ids, isDeleted)).start();
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
            boolean isExistName = mCategoryDao.isExistingName(name, parentIndex);
            if (mExistingNameLive != null)
                mExistingNameLive.postValue(isExistName);
        }).start();
    }
}

