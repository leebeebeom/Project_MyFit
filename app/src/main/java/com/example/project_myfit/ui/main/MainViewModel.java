package com.example.project_myfit.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_myfit.Repository;
import com.example.project_myfit.ui.main.adapter.ViewPagerAdapter;
import com.example.project_myfit.ui.main.database.Category;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.BOTTOM;
import static com.example.project_myfit.MyFitConstant.ETC;
import static com.example.project_myfit.MyFitConstant.OUTER;
import static com.example.project_myfit.MyFitConstant.TOP;

public class MainViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private int mSort;
    private List<Category> mSelectedItem;
    private HashSet<Integer> mSelectedPosition;
    private final MutableLiveData<Integer> mSelectedAmount;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mSelectedAmount = new MutableLiveData<>();
    }

    public LiveData<List<Category>> getCategoryLive() {
        return mRepository.getCategoryLive();
    }

    public void addCategory(String parentCategory, String categoryName) {
        int largestOrder = mRepository.getCategoryLargestOrder() + 1;
        mRepository.categoryInsert(new Category(categoryName, parentCategory, largestOrder));
    }

    public void updateCategoryList(List<Category> categoryList) {
        mRepository.categoryUpdate(categoryList);
    }

    public void setSelectedAmount() {
        mSelectedAmount.setValue(mSelectedItem.size());
    }

    public MutableLiveData<Integer> getSelectedAmount() {
        return mSelectedAmount;
    }

    public int getSort() {
        return mSort;
    }

    public void setSort(int mSort) {
        this.mSort = mSort;
    }

    public void setSelectedPosition(ViewPagerAdapter mViewPagerAdapter, @NotNull String parentCategory) {
        switch (parentCategory) {
            case TOP:
                mSelectedPosition = mViewPagerAdapter.getTopAdapter().getSelectedPosition();
                break;
            case BOTTOM:
                mSelectedPosition = mViewPagerAdapter.getBottomAdapter().getSelectedPosition();
                break;
            case OUTER:
                mSelectedPosition = mViewPagerAdapter.getOuterAdapter().getSelectedPosition();
                break;
            case ETC:
                mSelectedPosition = mViewPagerAdapter.getETCAdapter().getSelectedPosition();
                break;
        }
    }

    public HashSet<Integer> getSelectedPosition() {
        return mSelectedPosition;
    }

    public void selectedItemListInit() {
        mSelectedItem = new ArrayList<>();
    }

    public void categorySelected(Category category, boolean isChecked, int position, int viewPagerPosition, ViewPagerAdapter mViewPagerAdapter) {
        if (viewPagerPosition == 0) mViewPagerAdapter.getTopAdapter().setSelectedPosition(position);
        else if (viewPagerPosition == 1)
            mViewPagerAdapter.getBottomAdapter().setSelectedPosition(position);
        else if (viewPagerPosition == 2)
            mViewPagerAdapter.getOuterAdapter().setSelectedPosition(position);
        else if (viewPagerPosition == 3)
            mViewPagerAdapter.getETCAdapter().setSelectedPosition(position);

        if (isChecked) mSelectedItem.add(category);
        else mSelectedItem.remove(category);
        setSelectedAmount();
    }

    public void categoryNameEdit(String categoryName) {
        mSelectedItem.get(0).setCategory(categoryName);
        mRepository.categoryUpdate(mSelectedItem.get(0));
    }

    public void selectedItemDelete() {
        for (Category category : mSelectedItem) category.setDeleted(true);
        mRepository.categoryUpdate(mSelectedItem);
    }

    public List<Category> getSelectedItem() {
        return mSelectedItem;
    }
}