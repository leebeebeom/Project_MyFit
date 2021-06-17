package com.example.myfit.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.model.Category;
import com.example.myfit.data.repository.dao.CategoryDao;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.util.SortUtil;
import com.example.myfit.util.constant.SharedPreferenceKey;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import lombok.Getter;
import lombok.experimental.Accessors;

@Singleton
@Accessors(prefix = "m")
public class CategoryRepository extends BaseRepository {
    private final CategoryDao mCategoryDao;
    private final SharedPreferences mMainSortPreference;
    @Getter
    private final IntegerSharedPreferenceLiveData mMainSortPreferenceLive;
    private MutableLiveData<Boolean> mExistingNameLive;
    private final MediatorLiveData<List<List<CategoryTuple>>> mClassifiedTuplesLive = new MediatorLiveData<>();
    private LiveData<List<List<CategoryTuple>>> mDeletedClassifiedTuplesLive, mDeletedSearchTuplesLive;
    private LiveData<List<CategoryTuple>> mAddedTuplesLive;
    private final LiveData<List<Category>> mAllCategoriesLive;

    @Inject
    public CategoryRepository(@ApplicationContext Context context) {
        this.mCategoryDao = AppDataBase.getsInstance(context).categoryDao();
        this.mMainSortPreference = context.getSharedPreferences(SharedPreferenceKey.SORT_MAIN.getValue(), Context.MODE_PRIVATE);
        this.mMainSortPreferenceLive = new IntegerSharedPreferenceLiveData(
                mMainSortPreference, SharedPreferenceKey.SORT.getValue(), Sort.SORT_CUSTOM.getValue());

        this.mAllCategoriesLive = mCategoryDao.getAllModelsLiveWithContentSize();
    }

    //from appDataBase
    public void insert(List<Category> categories) {
        new Thread(() -> mCategoryDao.insert(categories)).start();
    }

    //to main
    public LiveData<List<List<CategoryTuple>>> getClassifiedTuplesLive() {
        if (mClassifiedTuplesLive.getValue() == null) {
            mClassifiedTuplesLive.addSource(mAllCategoriesLive, this::setValue);
            mClassifiedTuplesLive.addSource(mMainSortPreferenceLive, sortInt -> {
                if (mAllCategoriesLive.getValue() != null)
                    setValue(mAllCategoriesLive.getValue());
            });
        }
        return mClassifiedTuplesLive;
    }

    private void setValue(List<Category> categories) {
        Stream<Category> unDeletedCategoryStream = super.getUnDeletedStream(categories);
        Stream<Category> sortedStream = SortUtil.sortCategoryStream(super.getSort(), unDeletedCategoryStream);
        List<List<CategoryTuple>> classifiedTuples = super.getClassifiedTuplesByParentIndex(getCategoryTuples(sortedStream));
        mClassifiedTuplesLive.setValue(classifiedTuples);
    }

    private List<CategoryTuple> getCategoryTuples(Stream<Category> stream) {
        return stream.map(CategoryTuple::new).collect(Collectors.toList());
    }

    //to recycleBin
    public LiveData<List<List<CategoryTuple>>> getDeletedClassifiedTuplesLive() {
        if (mDeletedClassifiedTuplesLive == null)
            mDeletedClassifiedTuplesLive = Transformations.map(mAllCategoriesLive, categories -> {
                Stream<Category> deletedCategoryStream = super.getDeletedCategoryStream(categories)
                        .sorted((o1, o2) -> Long.compare(o1.getDeletedTime(), o2.getDeletedTime()));
                return super.getClassifiedTuplesByParentIndex(getCategoryTuples(deletedCategoryStream));
            });
        return mDeletedClassifiedTuplesLive;
    }

    //to recycleBin search
    public LiveData<List<List<CategoryTuple>>> getDeletedSearchTuplesLive() {
        if (mDeletedSearchTuplesLive == null)
            mDeletedSearchTuplesLive = Transformations.map(mAllCategoriesLive, categories -> {
                Stream<Category> deletedCategoryStream = super.getDeletedCategoryStream(categories)
                        .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()));
                return super.getClassifiedTuplesByParentIndex(getCategoryTuples(deletedCategoryStream));
            });
        return mDeletedSearchTuplesLive;
    }

    //to listFragment
    public LiveData<Category> getSingleLiveById(long id) {
        return Transformations.map(mAllCategoriesLive, categories -> {
            Optional<Category> categoryOptional = categories.stream().filter(category -> category.getId() == id).findAny();
            return categoryOptional.orElse(null);
        });
    }

    //to treeView
    public LiveData<List<CategoryTuple>> getTuplesLiveByParentIndex(int parentIndex) {
        return Transformations.map(mAllCategoriesLive, categories -> {
            Stream<Category> unDeletedCategoryStream = super.getUnDeletedStream(categories)
                    .filter(category -> category.getParentIndex() == parentIndex);
            Stream<Category> sortedStream = SortUtil.sortCategoryStream(super.getSort(), unDeletedCategoryStream);
            return getCategoryTuples(sortedStream);
        });
    }

    //from addCategory dialog
    public void insert(String name, int parentIndex) {
        new Thread(() -> mCategoryDao.insert(name, parentIndex)).start();
    }

    //to recycle bin
    public LiveData<List<CategoryTuple>> getAddedTuplesLive() {
        mAddedTuplesLive = new MutableLiveData<>();
        return mAddedTuplesLive;
    }

    //from restore dialog(disposable)
    public void insertRestoreCategories(@NotNull List<Integer> parentIndex) {
        new Thread(() -> {
            List<Long> insertIds = mCategoryDao.insertRestoreCategories(parentIndex);
            mAddedTuplesLive = Transformations.map(mAllCategoriesLive, categories -> {
                Stream<Category> addedCategoriesStream =
                        categories.stream().filter(category -> insertIds.stream().anyMatch(id -> id == category.getId()));
                return getCategoryTuples(addedCategoriesStream);
            });
        }).start();
    }

    //from editCategoryName dialog
    public void update(long id, String name) {
        if (mAllCategoriesLive.getValue() != null) {
            Optional<Category> categoryOptional = mAllCategoriesLive.getValue().stream().filter(category -> category.getId() == id).findFirst();
            if (categoryOptional.isPresent()) {
                Category category = categoryOptional.get();
                category.setName(name);
                new Thread(() -> mCategoryDao.update(category)).start();
            }
        }
    }

    //from adapter drag drop
    public void updateTuples(List<CategoryTuple> categoryTuples) {
        new Thread(() -> mCategoryDao.updateTuples(categoryTuples)).start();
    }

    //from delete dialog, restore dialog
    public void deleteOrRestore(long[] ids) {
        if (mAllCategoriesLive.getValue() != null) {
            List<Category> categories = mAllCategoriesLive.getValue().stream()
                    .filter(category -> Arrays.stream(ids).anyMatch(id -> category.getId() == id))
                    .collect(Collectors.toList());
            super.setDeleted(categories);
            new Thread(() -> {
                mCategoryDao.update(categories);
                mCategoryDao.setChildrenParentDeleted(ids);
            }).start();
        }
    }

    @Override
    protected SharedPreferences getSortPreference() {
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

