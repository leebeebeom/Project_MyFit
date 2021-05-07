package com.example.project_myfit.main.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.util.SelectedItemTreat;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.ETC;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.util.MyFitConstant.SORT_MAIN;
import static com.example.project_myfit.util.MyFitConstant.TOP;

public class MainViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private final MutableLiveData<Integer> mSelectedCategorySizeLive;
    private final SharedPreferences mSortPreferences;
    private List<Category> mSelectedCategoryList;
    private int mCurrentItem;
    private int mSort;
    private SelectedItemTreat mSelectedItemTreat;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mSelectedCategorySizeLive = new MutableLiveData<>();
        mSortPreferences = getApplication().getSharedPreferences(SORT_MAIN, Context.MODE_PRIVATE);
        mSort = mSortPreferences.getInt(SORT_MAIN, SORT_CUSTOM);
    }

    public void deleteSelectedCategory() {
        if (mSelectedItemTreat == null)
            mSelectedItemTreat = new SelectedItemTreat(getApplication());
        mSelectedItemTreat.categoryTreat(mSelectedCategoryList, true);
    }

    public void setSelectedCategorySizeLiveValue() {
        mSelectedCategorySizeLive.setValue(mSelectedCategoryList.size());
    }

    public void updateNewOrderCategoryList(@NotNull List<Category> newOrderNumberCategoryList) {
        List<Category> newSelectedCategoryList = new ArrayList<>();
        for (Category category : newOrderNumberCategoryList)
            if (mSelectedCategoryList.contains(category)) newSelectedCategoryList.add(category);
        mSelectedCategoryList = newSelectedCategoryList;

        mRepository.getCategoryRepository().updateCategory(newOrderNumberCategoryList);
    }

    public void changeSort(int sort) {
        mSort = sort;
        SharedPreferences.Editor editor = mSortPreferences.edit();
        editor.putInt(SORT_MAIN, sort);
        editor.apply();
    }

    //getter,setter---------------------------------------------------------------------------------
    public MutableLiveData<Integer> getSelectedCategorySizeLive() {
        return mSelectedCategorySizeLive;
    }

    public List<Category> getSelectedCategoryList() {
        if (mSelectedCategoryList == null) mSelectedCategoryList = new ArrayList<>();
        return mSelectedCategoryList;
    }


    public List<Long> getFolderParentIdList(String parentCategory) {
        return mRepository.getFolderRepository().getFolderParentIdList(parentCategory, false, false);
    }

    public List<Long> getSizeParentIdList(String parentCategory) {
        return mRepository.getSizeRepository().getSizeParentIdList(parentCategory, false, false);
    }

    public int getCurrentItem() {
        return mCurrentItem;
    }

    public void setCurrentItem(int mCurrentItem) {
        this.mCurrentItem = mCurrentItem;
    }

    public String getParentCategory() {
        switch (mCurrentItem) {
            case 0:
                return TOP;
            case 1:
                return BOTTOM;
            case 2:
                return OUTER;
            case 3:
                return ETC;
        }
        return null;
    }

    public LiveData<List<Category>> getCategoryLive(boolean isDeleted) {
        return mRepository.getCategoryRepository().getCategoryLive(isDeleted);
    }

    public int getSort() {
        return mSort;
    }
}