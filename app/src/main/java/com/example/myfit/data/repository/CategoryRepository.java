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
import com.example.myfit.di.Qualifiers;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

public class CategoryRepository extends BaseRepository {
    private final CategoryDao categoryDao;
    private SharedPreferences mainSortPreference;
    private IntegerSharedPreferenceLiveData mainSortPreferenceLive;
    private MutableLiveData<Long> insertIdLive;
    private MutableLiveData<Boolean> isExistingNameLive;
    private LiveData<List<List<CategoryTuple>>> classifiedTuplesLive, deletedClassifiedTuplesLive, deletedSearchTuplesLive;

    @Inject
    public CategoryRepository(CategoryDao categoryDao,
                              @Qualifiers.MainSortPreference SharedPreferences mainSortPreference,
                              @Qualifiers.MainSortPreferenceLive IntegerSharedPreferenceLiveData mainSortPreferenceLive) {
        this.categoryDao = categoryDao;
        this.mainSortPreference = mainSortPreference;
        this.mainSortPreferenceLive = mainSortPreferenceLive;
    }

    //for appDataBase
    public CategoryRepository(Context context) {
        this.categoryDao = AppDataBase.getsInstance(context).categoryDao();
    }

    //to main
    public LiveData<List<List<CategoryTuple>>> getClassifiedTuplesLive() {
        if (classifiedTuplesLive == null)
            classifiedTuplesLive = Transformations.switchMap(mainSortPreferenceLive, sort -> categoryDao.getClassifiedTuplesLive(Sort.values()[sort]));
        return classifiedTuplesLive;
    }

    //to recycleBin
    public LiveData<List<List<CategoryTuple>>> getDeletedClassifiedTuplesLive() {
        if (deletedClassifiedTuplesLive == null)
            deletedClassifiedTuplesLive = categoryDao.getDeletedClassifiedTuplesLive();
        return deletedClassifiedTuplesLive;
    }

    //to recycleBin search
    public LiveData<List<List<CategoryTuple>>> getDeletedSearchTuplesLive() {
        if (deletedSearchTuplesLive == null)
            deletedSearchTuplesLive = categoryDao.getDeletedSearchTuplesLive();
        return deletedSearchTuplesLive;
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
            insertIdLive.postValue(insertId);
        }).start();
    }

    public MutableLiveData<Long> getInsertIdLive() {
        if (insertIdLive == null)
            insertIdLive = new MutableLiveData<>();
        return insertIdLive;
    }

    //from appDataBase
    public void insert(Category[] categories) {
        new Thread(() -> categoryDao.insert(categories)).start();
    }

    //from restore dialog(disposable)
    public LiveData<Long[]> insertRestoreCategories(@NotNull int[] parentIndex) {
        MutableLiveData<Long[]> insertIdsLive = new MutableLiveData<>();
        new Thread(() -> {
            Long[] insertIds = categoryDao.insertRestoreCategories(parentIndex);
            if (insertIdLive != null) insertIdsLive.postValue(insertIds);
        }).start();
        return insertIdsLive;
    }

    //from editCategoryName dialog
    public void update(long id, String name) {
        new Thread(() -> categoryDao.update(id, name)).start();
    }

    //from adapter drag drop
    public void updateCategories(List<CategoryTuple> categoryTuples) {
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

    //from addCategory Dialog
    public void isExistingName(String name, int parentIndex) {
        new Thread(() -> {
            boolean isExistName = categoryDao.isExistingName(name, parentIndex);
            if (isExistingNameLive != null) isExistingNameLive.postValue(isExistName);
        }).start();
    }

    public MutableLiveData<Boolean> getIsExistingNameLive() {
        if (isExistingNameLive == null)
            isExistingNameLive = new MutableLiveData<>();
        return isExistingNameLive;
    }
}

