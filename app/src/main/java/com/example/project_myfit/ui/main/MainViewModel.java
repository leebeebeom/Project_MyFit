package com.example.project_myfit.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_myfit.Repository;
import com.example.project_myfit.ui.main.adapter.ViewPagerAdapter;
import com.example.project_myfit.ui.main.database.Category;

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
    private List<Category> mSelectedItemTop, mSelectedItemBottom, mSelectedItemOuter, mSelectedItemETC;
    private HashSet<Integer> mTopSelectedPosition, mBottomSelectedPosition, mOuterSelectedPosition, mETCSelectedPosition;
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

    public void selectedItemClear() {
        mSelectedItemTop.clear();
        mSelectedItemBottom.clear();
        mSelectedItemOuter.clear();
        mSelectedItemETC.clear();
    }

    public void setSelectedAmount() {
        mSelectedAmount.setValue(mSelectedItemTop.size() + mSelectedItemBottom.size() + mSelectedItemOuter.size() + mSelectedItemETC.size());
    }

    public MutableLiveData<Integer> getSelectedAmount() {
        return mSelectedAmount;
    }

    public List<Category> getSelectedItemTop() {
        return mSelectedItemTop;
    }

    public List<Category> getSelectedItemBottom() {
        return mSelectedItemBottom;
    }

    public List<Category> getSelectedItemOuter() {
        return mSelectedItemOuter;
    }

    public List<Category> getSelectedItemETC() {
        return mSelectedItemETC;
    }

    public int getSort() {
        return mSort;
    }

    public void setSort(int mSort) {
        this.mSort = mSort;
    }

    public void setSelectedPosition(ViewPagerAdapter mViewPagerAdapter, String parentCategory) {
        switch (parentCategory) {
            case TOP:
                mTopSelectedPosition = mViewPagerAdapter.getTopAdapter().getSelectedPosition();
                break;
            case BOTTOM:
                mBottomSelectedPosition = mViewPagerAdapter.getBottomAdapter().getSelectedPosition();
                break;
            case OUTER:
                mOuterSelectedPosition = mViewPagerAdapter.getOuterAdapter().getSelectedPosition();
                break;
            case ETC:
                mETCSelectedPosition = mViewPagerAdapter.getETCAdapter().getSelectedPosition();
                break;
        }
    }

    public List<HashSet<Integer>> getSelectedPositionList() {
        List<HashSet<Integer>> selectedPositionList = new ArrayList<>();
        selectedPositionList.add(mTopSelectedPosition);
        selectedPositionList.add(mBottomSelectedPosition);
        selectedPositionList.add(mOuterSelectedPosition);
        selectedPositionList.add(mETCSelectedPosition);
        return selectedPositionList;
    }

    public void selectedItemListInit() {
        mSelectedItemTop = new ArrayList<>();
        mSelectedItemBottom = new ArrayList<>();
        mSelectedItemOuter = new ArrayList<>();
        mSelectedItemETC = new ArrayList<>();
    }

    public void categorySelected(Category category, boolean isChecked, int position, int viewPagerPosition, ViewPagerAdapter mViewPagerAdapter) {
        if (viewPagerPosition == 0) {
            if (isChecked) mSelectedItemTop.add(category);
            else mSelectedItemTop.remove(category);
            mViewPagerAdapter.getTopAdapter().setSelectedPosition(position);
        } else if (viewPagerPosition == 1) {
            if (isChecked) mSelectedItemBottom.add(category);
            else mSelectedItemBottom.remove(category);
            mViewPagerAdapter.getBottomAdapter().setSelectedPosition(position);
        } else if (viewPagerPosition == 2) {
            if (isChecked) mSelectedItemOuter.add(category);
            else mSelectedItemOuter.remove(category);
            mViewPagerAdapter.getOuterAdapter().setSelectedPosition(position);
        } else if (viewPagerPosition == 3) {
            if (isChecked) mSelectedItemETC.add(category);
            else mSelectedItemETC.remove(category);
            mViewPagerAdapter.getETCAdapter().setSelectedPosition(position);
        }
        setSelectedAmount();
    }

    public void categoryNameEdit(String categoryName, String parentCategory) {
        switch (parentCategory) {
            case TOP:
                mSelectedItemTop.get(0).setCategory(categoryName);
                mRepository.categoryUpdate(mSelectedItemTop.get(0));
                break;
            case BOTTOM:
                mSelectedItemBottom.get(0).setCategory(categoryName);
                mRepository.categoryUpdate(mSelectedItemBottom.get(0));
                break;
            case OUTER:
                mSelectedItemOuter.get(0).setCategory(categoryName);
                mRepository.categoryUpdate(mSelectedItemOuter.get(0));
                break;
            case ETC:
                mSelectedItemETC.get(0).setCategory(categoryName);
                mRepository.categoryUpdate(mSelectedItemETC.get(0));
                break;

        }
    }

    public void selectedItemDelete(String parentCategory) {
        switch (parentCategory) {
            case TOP:
                for (Category category : mSelectedItemTop) category.setDeleted(true);
                mRepository.categoryUpdate(mSelectedItemTop);
                break;
            case BOTTOM:
                for (Category category : mSelectedItemBottom) category.setDeleted(true);
                mRepository.categoryUpdate(mSelectedItemBottom);
                break;
            case OUTER:
                for (Category category : mSelectedItemOuter) category.setDeleted(true);
                mRepository.categoryUpdate(mSelectedItemOuter);
                break;
            case ETC:
                for (Category category : mSelectedItemETC) category.setDeleted(true);
                mRepository.categoryUpdate(mSelectedItemETC);
                break;

        }
    }
}