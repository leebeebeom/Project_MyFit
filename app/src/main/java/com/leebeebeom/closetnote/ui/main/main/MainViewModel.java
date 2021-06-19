package com.leebeebeom.closetnote.ui.main.main;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.leebeebeom.closetnote.data.repository.CategoryRepository;
import com.leebeebeom.closetnote.data.tuple.BaseTuple;
import com.leebeebeom.closetnote.data.tuple.ContentSizeTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.CategoryTuple;
import com.leebeebeom.closetnote.util.CommonUtil;
import com.leebeebeom.closetnote.util.SizeLiveSet;
import com.leebeebeom.closetnote.util.constant.Sort;
import com.leebeebeom.closetnote.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import lombok.Getter;
import lombok.experimental.Accessors;

@HiltViewModel
@Accessors(prefix = "m")
public class MainViewModel extends ViewModel {
    private final CategoryRepository mCategoryRepository;
    private final SizeLiveSet<CategoryTuple> mSelectedCategoryTuples;
    @Getter
    private final IntegerSharedPreferenceLiveData mMainSortPreferenceLive;
    private int mCurrentItem;

    @Inject
    public MainViewModel(CategoryRepository categoryRepository,
                         SizeLiveSet<CategoryTuple> selectedCategoryTuples,
                         @ApplicationContext Context context) {
        this.mCategoryRepository = categoryRepository;
        this.mMainSortPreferenceLive = mCategoryRepository.getMainSortPreferenceLive();
        this.mSelectedCategoryTuples = selectedCategoryTuples;
    }

    public SizeLiveSet<CategoryTuple> getSelectedCategoryTuples() {
        return mSelectedCategoryTuples;
    }

    public LiveData<Integer> getSelectedItemsLive() {
        return mSelectedCategoryTuples.getLiveData();
    }

    public int getCurrentItem() {
        return mCurrentItem;
    }

    public void setCurrentItem(int mCurrentItem) {
        this.mCurrentItem = mCurrentItem;
    }

    public LiveData<List<List<CategoryTuple>>> getCategoryLive() {
        return mCategoryRepository.getClassifiedTuplesLive();
    }

    public Sort getSort() {
        return mCategoryRepository.getSort();
    }

    public void dragDrop(List<CategoryTuple> newOrderList) {
        CommonUtil.replaceNewOrderSelectedItems(newOrderList, mSelectedCategoryTuples);
        mCategoryRepository.updateTuples(newOrderList);
    }

    public long[] getSelectedItemIds() {
        return mSelectedCategoryTuples.stream().mapToLong(BaseTuple::getId).toArray();
    }
}