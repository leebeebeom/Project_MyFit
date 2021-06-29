package com.leebeebeom.closetnote.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.leebeebeom.closetnote.data.AppDataBase;
import com.leebeebeom.closetnote.data.model.model.Category;
import com.leebeebeom.closetnote.data.model.model.Folder;
import com.leebeebeom.closetnote.data.model.model.Size;
import com.leebeebeom.closetnote.data.repository.dao.BaseDao;
import com.leebeebeom.closetnote.data.repository.dao.CategoryDao;
import com.leebeebeom.closetnote.data.tuple.tuple.CategoryTuple;
import com.leebeebeom.closetnote.util.constant.SharedPreferenceKey;
import com.leebeebeom.closetnote.util.constant.Sort;
import com.leebeebeom.closetnote.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import lombok.Getter;
import lombok.experimental.Accessors;

import static com.leebeebeom.closetnote.ui.MainActivity.TAG;

@Singleton
@Accessors(prefix = "m")
public class CategoryRepository extends BaseRepository<Category, CategoryTuple> {
    private final CategoryDao mCategoryDao;
    private final SharedPreferences mMainSortPreference, mCategoryLargestSortNumberPreference;
    @Getter
    private final IntegerSharedPreferenceLiveData mMainSortPreferenceLive;
    private final LiveData<List<Category>> mAllCategoriesLive;
    private MutableLiveData<Boolean> mExistingNameLive;
    private LiveData<List<List<CategoryTuple>>> mClassifiedTuplesLive, mDeletedClassifiedTuplesLive, mDeletedSearchTuplesLive;

    @Inject
    public CategoryRepository(@ApplicationContext Context context) {
        this.mCategoryDao = AppDataBase.getsInstance(context).categoryDao();
        this.mMainSortPreference = context.getSharedPreferences(SharedPreferenceKey.SORT_MAIN.getValue(), Context.MODE_PRIVATE);
        this.mMainSortPreferenceLive = new IntegerSharedPreferenceLiveData(
                mMainSortPreference, SharedPreferenceKey.SORT.getValue(), Sort.SORT_CUSTOM.getValue());
        this.mCategoryLargestSortNumberPreference = context.getSharedPreferences(
                SharedPreferenceKey.CATEGORY_LARGEST_SORT_NUMBER.getValue(), Context.MODE_PRIVATE);

        this.mAllCategoriesLive = mCategoryDao.getAllModelsLiveWithContentSize();
    }

    public LiveData<List<List<CategoryTuple>>> getClassifiedTuplesLive() {
        if (mClassifiedTuplesLive == null) {
            mClassifiedTuplesLive = Transformations.map(mAllCategoriesLive, categories -> {
                Stream<Category> undeletedSortedStream = getUndeletedSortedStream(categories);
                return getClassifiedTuplesByParentIndex(getCategoryTuples(undeletedSortedStream));
            });
        }
        return mClassifiedTuplesLive;
    }

    public LiveData<String> getNameLive(long id) {
        MutableLiveData<String> nameLive = new MutableLiveData<>();
        setNameLive(id, nameLive, 0);
        return nameLive;
    }

    private void setNameLive(long id, MutableLiveData<String> nameLive, int count) {
        Optional<String> nameOptional = getNameOptional(getAllModelsLiveValue(), id);
        if (nameOptional.isPresent())
            nameLive.setValue(nameOptional.get());
        else if (count < 5) {
            count++;
            setNameLive(id, nameLive, count);
        } else if (count == 5) {
            nameLive.setValue("");
            Log.d(TAG, "categoryRepository -> setNameLive: 이름 없음");
        }
    }

    public LiveData<List<CategoryTuple>> getTuplesLiveByParentIndex(int parentIndex) {
        return Transformations.map(mAllCategoriesLive, categories -> {
            Stream<Category> undeletedSortedStreamByParentIndex = getUndeletedSortedStreamByParentIndex(categories, parentIndex);
            return getCategoryTuples(undeletedSortedStreamByParentIndex);
        });
    }

    public void insert(String name, int parentIndex) {
        int largestSort = getLargestSortNumber();
        Category category = new Category(parentIndex, largestSort, name.trim());
        insert(category);
    }

    public void update(long id, String name, int count) {
        Optional<Category> categoryOptional = getSingleOptional(getAllModelsLiveValue(), id);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            category.setName(name.trim());
            update(category);
        } else if (count < 5) {
            count++;
            update(id, name, count);
        } else if (count == 5) Log.d(TAG, "categoryRepository -> update: 업데이트 실패, 카테고리 없음");
    }

    public void deleteOrRestore(long[] ids, FolderRepository folderRepository, SizeRepository sizeRepository, int count) {
        List<Category> categories = getModelsByIds(getAllModelsLiveValue(), ids);
        if (categories.size() == ids.length) {
            List<Folder> allFolders = folderRepository.getAllModelsLiveValue();
            List<Size> allSizes = sizeRepository.getAllModelsLiveValue();
            setDeleted(categories);
            new Thread(() -> {
                mCategoryDao.update(categories);
                mCategoryDao.updateChildrenParentDeleted(ids, folderRepository.getAllModelsLiveValue(), sizeRepository.getAllModelsLiveValue());
            }).start();
        } else if (count < 5) {
            count++;
            deleteOrRestore(ids, folderRepository, sizeRepository, count);
        } else if (count == 5) Log.d(TAG, "categoryRepository -> deleteOrRestore: 실패");
    }

    public MutableLiveData<Boolean> getExistingNameLive() {
        mExistingNameLive = new MutableLiveData<>();
        return mExistingNameLive;
    }

    public void isExistingName(String name, int parentIndex) {
        boolean existingName = isExistingName(getAllModelsLiveValue(), parentIndex, name);
        if (mExistingNameLive != null) mExistingNameLive.setValue(existingName);
    }

    private List<CategoryTuple> getCategoryTuples(Stream<Category> stream) {
        return stream.map(CategoryTuple::new).collect(Collectors.toList());
    }

    @Override
    protected SharedPreferences getSortPreference() {
        return mMainSortPreference;
    }

    @Override
    protected SharedPreferences getLargestSortNumberPreference() {
        return mCategoryLargestSortNumberPreference;
    }

    @Override
    protected BaseDao<Category, CategoryTuple> getDao() {
        return mCategoryDao;
    }

    @Override
    protected LiveData<List<Category>> getModelsLive() {
        return mAllCategoriesLive;
    }
}

