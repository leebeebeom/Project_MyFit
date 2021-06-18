package com.example.myfit.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.model.Category;
import com.example.myfit.data.repository.dao.BaseDao;
import com.example.myfit.data.repository.dao.CategoryDao;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.util.constant.SharedPreferenceKey;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

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
public class CategoryRepository extends BaseRepository<Category, CategoryTuple> {
    private final CategoryDao mCategoryDao;
    private final SharedPreferences mMainSortPreference;
    @Getter
    private final IntegerSharedPreferenceLiveData mMainSortPreferenceLive;
    private MutableLiveData<Boolean> mExistingNameLive;
    private final MediatorLiveData<List<List<CategoryTuple>>> mClassifiedTuplesLive = new MediatorLiveData<>();
    private LiveData<List<List<CategoryTuple>>> mDeletedClassifiedTuplesLive, mDeletedSearchTuplesLive;
    private final LiveData<List<Category>> mAllCategoriesLive;

    @Inject
    public CategoryRepository(@ApplicationContext Context context) {
        this.mCategoryDao = AppDataBase.getsInstance(context).categoryDao();
        this.mMainSortPreference = context.getSharedPreferences(SharedPreferenceKey.SORT_MAIN.getValue(), Context.MODE_PRIVATE);
        this.mMainSortPreferenceLive = new IntegerSharedPreferenceLiveData(
                mMainSortPreference, SharedPreferenceKey.SORT.getValue(), Sort.SORT_CUSTOM.getValue());

        this.mAllCategoriesLive = mCategoryDao.getAllModelsLiveWithContentSize();
    }

    public LiveData<List<List<CategoryTuple>>> getClassifiedTuplesLive() {
        if (mClassifiedTuplesLive.getValue() == null) {
            mClassifiedTuplesLive.addSource(mAllCategoriesLive, this::setValue);
            mClassifiedTuplesLive.addSource(mMainSortPreferenceLive, sortInt -> setValue(mAllCategoriesLive.getValue()));
        }
        return mClassifiedTuplesLive;
    }

    private void setValue(List<Category> categories) {
        Stream<Category> undeletedSortedStream = getUndeletedSortedStream(categories);
        List<List<CategoryTuple>> classifiedTuples = getClassifiedTuplesByParentIndex(getCategoryTuples(undeletedSortedStream));
        mClassifiedTuplesLive.setValue(classifiedTuples);
    }

    public LiveData<List<List<CategoryTuple>>> getDeletedClassifiedTuplesLive() {
        if (mDeletedClassifiedTuplesLive == null)
            mDeletedClassifiedTuplesLive = Transformations.map(mAllCategoriesLive, categories -> {
                Stream<Category> deletedSortedStream = getDeletedSortedStream(categories);
                return getClassifiedTuplesByParentIndex(getCategoryTuples(deletedSortedStream));
            });
        return mDeletedClassifiedTuplesLive;
    }

    public LiveData<List<List<CategoryTuple>>> getDeletedSearchTuplesLive() {
        if (mDeletedSearchTuplesLive == null)
            mDeletedSearchTuplesLive = Transformations.map(mAllCategoriesLive, categories -> {
                Stream<Category> deletedSearchStream = getDeletedSearchStream(categories);
                return getClassifiedTuplesByParentIndex(getCategoryTuples(deletedSearchStream));
            });
        return mDeletedSearchTuplesLive;
    }

    public LiveData<Category> getSingleLiveById(long id) {
        return Transformations.map(mAllCategoriesLive, categories -> getSingleOptional(categories, id).orElse(Category.getDummy()));
    }

    public LiveData<List<CategoryTuple>> getTuplesLiveByParentIndex(int parentIndex) {
        return Transformations.map(mAllCategoriesLive, categories -> {
            Stream<Category> undeletedSortedStreamByParentIndex = getUndeletedSortedStreamByParentIndex(categories, parentIndex);
            return getCategoryTuples(undeletedSortedStreamByParentIndex);
        });
    }

    public void insert(String name, int parentIndex) {
            Integer largestSort = getLargestSortNumber(mAllCategoriesLive.getValue());
            Category category = new Category(parentIndex, largestSort + 1, name);
            insert(category);
    }

    public void update(long id, String name) {
        Optional<Category> categoryOptional = getSingleOptional(mAllCategoriesLive.getValue(), id);
        categoryOptional.ifPresent(category -> {
            category.setName(name);
            update(category);
        });
    }

    public void deleteOrRestore(long[] ids) {
        Stream<Category> stream = getStreamByIds(mAllCategoriesLive.getValue(), ids);
        setDeleted(stream);
        new Thread(() -> {
            mCategoryDao.update(stream.collect(Collectors.toList()));
            mCategoryDao.setChildrenParentDeleted(ids);
        }).start();
    }

    public MutableLiveData<Boolean> getExistingNameLive() {
        mExistingNameLive = new MutableLiveData<>();
        return mExistingNameLive;
    }

    public void isExistingName(String name, int parentIndex) {
        boolean existingName = isExistingName(mAllCategoriesLive.getValue(), parentIndex, name);
        mExistingNameLive.setValue(existingName);
    }

    public void updateTuples(List<CategoryTuple> tuples) {
        new Thread(() -> mCategoryDao.updateTuplesImpl(tuples)).start();
    }

    private List<CategoryTuple> getCategoryTuples(Stream<Category> stream) {
        return stream.map(CategoryTuple::new).collect(Collectors.toList());
    }

    @Override
    protected SharedPreferences getSortPreference() {
        return mMainSortPreference;
    }

    @Override
    protected BaseDao<Category, CategoryTuple> getDao() {
        return mCategoryDao;
    }
}

