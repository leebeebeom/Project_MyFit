package com.example.myfit.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.tuple.CategoryFolderTuple;
import com.example.myfit.data.repository.dao.CategoryDao;
import com.example.myfit.util.IntegerSharedPreferenceLiveData;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.constant.SortValue;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class CategoryRepository extends BaseRepository implements CategoryDao.CategoryDaoInterFace {
    private final SharedPreferences mainSortPreference;
    private final CategoryDao categoryDao;

    public CategoryRepository(@NotNull Context context) {
        super(context);
        categoryDao = AppDataBase.getsInstance(context).categoryDao();
        mainSortPreference = context.getSharedPreferences(Sort.SORT_MAIN.getText(), SortValue.SORT_CUSTOM.getValue());
    }

    //to main, recycleBin
    @Override
    public LiveData<List<List<CategoryFolderTuple>>> getClassifiedCategoryTuplesLive() {
        IntegerSharedPreferenceLiveData mainSortPreferenceLive =
                new IntegerSharedPreferenceLiveData(mainSortPreference, Sort.SORT_MAIN.getText(), SortValue.SORT_CUSTOM.getValue());

        return Transformations.switchMap(mainSortPreferenceLive, categoryDao::getClassifiedCategoryTuplesLive);
    }

    @Override
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedClassifiedCategoryTuplesLive() {
        return categoryDao.getDeletedClassifiedCategoryTuplesLive();
    }

    //to recycleBin search
    @Override
    public LiveData<List<List<CategoryFolderTuple>>> getSearchCategoryTuplesList(String keyWord) {
        return categoryDao.getSearchCategoryTuplesList(keyWord);
    }

    //to treeView(disposable)
    @Override
    public LiveData<List<CategoryFolderTuple>> getCategoryTuplesByParentIndex(byte parentIndex) {
        MutableLiveData<List<CategoryFolderTuple>> categoryTuplesLive = new MutableLiveData<>();
        //TODO text
        new Thread(() -> {
            try {
                int sort = getMainSort();
                List<CategoryFolderTuple> categoryTuples = categoryDao.getCategoryTuplesByParentIndex(parentIndex, sort);
                categoryTuplesLive.postValue(categoryTuples);
            } catch (Exception e) {
                logE(e);
            }
        }).start();
        return categoryTuplesLive;
    }

    //to treeView
    @Override
    public LiveData<CategoryFolderTuple> getCategoryTupleById(long id) {
        //TODO 테스트
        MutableLiveData<CategoryFolderTuple> categoryTupleLive = new MutableLiveData<>();
        new Thread(() -> {
            try {
                CategoryFolderTuple categoryTuple = categoryDao.getCategoryTupleById(id);
                categoryTupleLive.postValue(categoryTuple);
            } catch (Exception e) {
                logE(e);
            }
        }).start();
        return categoryTupleLive;
    }

    //from addCategory dialog
    @Override
    public LiveData<Long> insertCategory(String categoryName, byte parentIndex) {
        MutableLiveData<Long> insertIdLive = new MutableLiveData<>();
        new Thread(() -> {
            try {
                long insertId = categoryDao.insertCategory(categoryName, parentIndex);
                insertIdLive.postValue(insertId);
            } catch (Exception e) {
                logE(e);
            }
        }).start();
        return insertIdLive;
    }

    //from restore dialog
    @Override
    public LiveData<Long[]> insertRestoreCategories(@NotNull byte[] parentIndex) {
        MutableLiveData<Long[]> insertIdsLive = new MutableLiveData<>();
        new Thread(() -> {
            try {
                Long[] insertIds = categoryDao.insertRestoreCategories(parentIndex);
                insertIdsLive.postValue(insertIds);
            } catch (Exception e) {
                logE(e);
            }
        }).start();
        return insertIdsLive;
    }

    //from editCategoryName dialog
    @Override
    public void updateCategory(long id, String name) {
        new Thread(() -> categoryDao.updateCategory(id, name)).start();
    }

    //from adapter drag drop
    @Override
    public void updateCategories(LinkedList<CategoryFolderTuple> categoryTuples) {
        new Thread(() -> categoryDao.updateTuples(categoryTuples)).start();
    }

    //from addCategory Dialog
    @Override
    public LiveData<Boolean> isExistingCategoryName(String categoryName, byte parentIndex) {
        MutableLiveData<Boolean> existCategoryNameLive = new MutableLiveData<>();
        new Thread(() -> {
            try {
                boolean isExistCategoryName = categoryDao.isExistingCategoryName(categoryName, parentIndex);
                existCategoryNameLive.postValue(isExistCategoryName);
            } catch (Exception e) {
                logE(e);
            }
        }).start();
        return existCategoryNameLive;
    }

    //from deleteSelectedItems, restore dialog
    @Override
    public void deleteOrRestoreCategories(long[] categoryIds, boolean isDeleted) {
        new Thread(() -> categoryDao.deleteOrRestoreCategories(categoryIds, isDeleted)).start();
    }

    public void setMainSortPreferenceValue(int sort) {
        SharedPreferences.Editor editor = mainSortPreference.edit();
        editor.putInt(Sort.SORT_MAIN.getText(), sort);
        editor.apply();
    }

    @NotNull
    private Integer getMainSort() {
        return mainSortPreference.getInt(Sort.SORT_MAIN.getText(), SortValue.SORT_CUSTOM.getValue());
    }
}

