package com.example.myfit.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.category.Category;
import com.example.myfit.data.model.category.CategoryTuple;
import com.example.myfit.data.repository.dao.CategoryDao;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class CategoryRepository extends BaseRepository {
    private static final String SORT_MAIN = "sort main";

    private final SharedPreferences mainSortPreference;
    private final CategoryDao categoryDao;
    private MutableLiveData<Long> insertIdLive;

    public CategoryRepository(@NotNull Context context) {
        categoryDao = AppDataBase.getsInstance(context).categoryDao();
        mainSortPreference = context.getSharedPreferences(SORT_MAIN, Context.MODE_PRIVATE);
    }

    //to main
    public LiveData<List<List<CategoryTuple>>> getClassifiedTuplesLive() {
        IntegerSharedPreferenceLiveData mainSortPreferenceLive =
                new IntegerSharedPreferenceLiveData(mainSortPreference, SORT_MAIN, Sort.SORT_CUSTOM.getValue());

        return Transformations.switchMap(mainSortPreferenceLive, sort -> categoryDao.getClassifiedTuplesLive(Sort.values()[sort]));
    }

    //to recycleBin
    public LiveData<List<List<CategoryTuple>>> getDeletedClassifiedTuplesLive() {
        return categoryDao.getDeletedClassifiedTuplesLive();
    }

    //to recycleBin search
    public LiveData<List<List<CategoryTuple>>> getDeletedSearchTuplesLive() {
        return categoryDao.getDeletedSearchTuplesLive();
    }

    //to treeView(disposable)
    public LiveData<List<CategoryTuple>> getTuplesByParentIndex(int parentIndex) {
        MutableLiveData<List<CategoryTuple>> tuplesLive = new MutableLiveData<>();
        new Thread(() -> {
            Sort sort = getSort();
            List<CategoryTuple> tuples = categoryDao.getTuplesByParentIndex(parentIndex, sort);
            tuplesLive.postValue(tuples);
        }).start();
        return tuplesLive;
    }

    //to treeView(disposable)
    public LiveData<CategoryTuple> getTupleById(long id) {
        MutableLiveData<CategoryTuple> categoryTupleLive = new MutableLiveData<>();
        new Thread(() -> {
                CategoryTuple tuple = categoryDao.getTupleById(id);
                categoryTupleLive.postValue(tuple);
        }).start();
        return categoryTupleLive;
    }

    //from addCategory dialog(disposable)
    public void insert(String name, int parentIndex) {
        new Thread(() -> {
            long insertId = categoryDao.insert(name, parentIndex);
            if (insertIdLive != null) insertIdLive.postValue(insertId);
        }).start();
    }

    public MutableLiveData<Long> getInsertIdLive() {
        if (insertIdLive == null)
            insertIdLive = new MutableLiveData<>();
        return insertIdLive;
    }

    //from appDateBase
    public void insert(Category[] categories) {
        new Thread(() -> categoryDao.insert(categories)).start();
    }

    //from restore dialog(disposable)
    public LiveData<Long[]> insertRestoreCategories(@NotNull int[] parentIndex) {
        MutableLiveData<Long[]> insertIdsLive = new MutableLiveData<>();
        new Thread(() -> {
                Long[] insertIds = categoryDao.insertRestoreCategories(parentIndex);
                insertIdsLive.postValue(insertIds);
        }).start();
        return insertIdsLive;
    }

    //from editCategoryName dialog
    public void update(long id, String name) {
        new Thread(() -> categoryDao.update(id, name)).start();
    }

    //from adapter drag drop
    public void updateCategories(LinkedList<CategoryTuple> categoryTuples) {
        new Thread(() -> categoryDao.update(categoryTuples)).start();
    }

    @Override
    //from delete dialog, restore dialog
    public void deleteOrRestore(long[] ids, boolean isDeleted) {
        new Thread(() -> categoryDao.deleteOrRestore(ids, isDeleted)).start();
    }

    @Override
    protected SharedPreferences getPreference() {
        return mainSortPreference;
    }

    @Override
    protected String getPreferenceKey() {
        return SORT_MAIN;
    }

    //from addCategory Dialog
    public LiveData<Boolean> isExistingName(String name, int parentIndex) {
        MutableLiveData<Boolean> isExistNameLive = new MutableLiveData<>();
        new Thread(() -> {
            boolean isExistName = categoryDao.isExistingName(name, parentIndex);
            isExistNameLive.postValue(isExistName);
        }).start();
        return isExistNameLive;
    }
}

