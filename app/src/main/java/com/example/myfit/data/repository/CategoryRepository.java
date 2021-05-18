package com.example.myfit.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.category.Category;
import com.example.myfit.data.model.tuple.CategoryFolderTuple;
import com.example.myfit.data.repository.dao.CategoryDao;
import com.example.myfit.util.IntegerSharedPreferenceLiveData;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.constant.SortValue;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class CategoryRepository extends BaseRepository{
    private final SharedPreferences mainSortPreference;
    private final CategoryDao categoryDao;

    public CategoryRepository(@NotNull Context context) {
        categoryDao = AppDataBase.getsInstance(context).categoryDao();
        mainSortPreference = context.getSharedPreferences(Sort.SORT_MAIN.getText(), SortValue.SORT_CUSTOM.getValue());
    }

    //to main
    public LiveData<List<List<CategoryFolderTuple>>> getClassifiedTuplesLive() {
        IntegerSharedPreferenceLiveData mainSortPreferenceLive =
                new IntegerSharedPreferenceLiveData(mainSortPreference, Sort.SORT_MAIN.getText(), SortValue.SORT_CUSTOM.getValue());

        return Transformations.switchMap(mainSortPreferenceLive, categoryDao::getClassifiedTuplesLive);
    }

    //to recycleBin
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedClassifiedTuplesLive() {
        return categoryDao.getDeletedClassifiedTuplesLive();
    }

    //to recycleBin search
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedSearchTuplesLive() {
        return categoryDao.getDeletedSearchTuplesLive();
    }

    //to treeView(disposable)
    public LiveData<List<CategoryFolderTuple>> getTuplesByParentIndex(byte parentIndex) {
        MutableLiveData<List<CategoryFolderTuple>> tuplesLive = new MutableLiveData<>();
        new Thread(() -> {
                int sort = getSort();
                List<CategoryFolderTuple> tuples = categoryDao.getTuplesByParentIndex(parentIndex, sort);
                tuplesLive.postValue(tuples);
        }).start();
        return tuplesLive;
    }

    //to treeView(disposable)
    public LiveData<CategoryFolderTuple> getTupleById(long id) {
        MutableLiveData<CategoryFolderTuple> categoryTupleLive = new MutableLiveData<>();
        new Thread(() -> {
                CategoryFolderTuple tuple = categoryDao.getTupleById(id);
                categoryTupleLive.postValue(tuple);
        }).start();
        return categoryTupleLive;
    }

    //from addCategory dialog(disposable)
    public LiveData<Long> insert(String name, byte parentIndex) {
        MutableLiveData<Long> insertIdLive = new MutableLiveData<>();
        new Thread(() -> {
                long insertId = categoryDao.insert(name, parentIndex);
                insertIdLive.postValue(insertId);
        }).start();
        return insertIdLive;
    }

    //from appDateBase
    public void insert(Category[] categories){
        new Thread(() -> categoryDao.insert(categories)).start();
    }

    //from restore dialog(disposable)
    public LiveData<Long[]> insertRestoreCategories(@NotNull byte[] parentIndex) {
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
    public void updateCategories(LinkedList<CategoryFolderTuple> categoryTuples) {
        new Thread(() -> categoryDao.update(categoryTuples)).start();
    }

    @Override
    //from delete dialog, restore dialog
    public void deleteOrRestore(long[] ids, boolean isDeleted) {
        new Thread(() -> categoryDao.deleteOrRestore(ids, isDeleted)).start();
    }

    //from addCategory Dialog
    public LiveData<Boolean> isExistingName(String name, byte parentIndex) {
        MutableLiveData<Boolean> isExistNameLive = new MutableLiveData<>();
        new Thread(() -> {
                boolean isExistName = categoryDao.isExistingName(name, parentIndex);
                isExistNameLive.postValue(isExistName);
        }).start();
        return isExistNameLive;
    }

    @Override
    public void changeSort(int sort) {
        SharedPreferences.Editor editor = mainSortPreference.edit();
        editor.putInt(Sort.SORT_MAIN.getText(), sort);
        editor.apply();
    }

    @NotNull
    private Integer getSort() {
        return mainSortPreference.getInt(Sort.SORT_MAIN.getText(), SortValue.SORT_CUSTOM.getValue());
    }
}

