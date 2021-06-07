package com.example.myfit.ui.main.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.di.Qualifiers;
import com.example.myfit.util.SizeLiveSet;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.experimental.Accessors;

@HiltViewModel
@Accessors(prefix = "m")
public class MainViewModel extends ViewModel {
    private final CategoryRepository mCategoryRepository;
    private final SizeLiveSet<CategoryTuple> mSelectedItems;
    @Getter
    private final IntegerSharedPreferenceLiveData mMainSortPreferenceLive;
    private int mCurrentItem;

    @Inject
    public MainViewModel(CategoryRepository categoryRepository,
                         SizeLiveSet<CategoryTuple> selectedItems,
                         @Qualifiers.MainSortPreferenceLive IntegerSharedPreferenceLiveData mainSortPreferenceLive) {
        this.mCategoryRepository = categoryRepository;
        this.mSelectedItems = selectedItems;
        this.mMainSortPreferenceLive = mainSortPreferenceLive;
    }

    public Set<CategoryTuple> getSelectedItems() {
        return mSelectedItems;
    }

    public LiveData<Integer> getSelectedItemsLive() {
        return mSelectedItems.getLiveData();
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
        List<CategoryTuple> newOrderSelectedItems =
                newOrderList.stream()
                        .filter(mSelectedItems::contains)
                        .collect(Collectors.toList());
        mSelectedItems.clear();
        mSelectedItems.addAll(newOrderSelectedItems);

        mCategoryRepository.updateTuples(newOrderList);
    }

    public long[] getSelectedItemIds() {
        return mSelectedItems.stream().mapToLong(BaseTuple::getId).toArray();
    }
}