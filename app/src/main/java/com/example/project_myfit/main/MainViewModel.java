package com.example.project_myfit.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private final List<Category> mSelectedCategoryList;
    private final MutableLiveData<Integer> mSelectedAmount;
    private int mCurrentItem;

    public MainViewModel(@NonNull Application application) {
        super(application);
        //checked
        mRepository = new Repository(application);
        mSelectedAmount = new MutableLiveData<>();
        mSelectedCategoryList = new ArrayList<>();
    }

    public void setSelectedAmount() {
        //checked
        mSelectedAmount.setValue(mSelectedCategoryList.size());
    }

    public MutableLiveData<Integer> getSelectedAmount() {
        return mSelectedAmount;
    }

    public void categorySelected(@NotNull Category category, boolean isChecked) {
        //checked
        if (isChecked) mSelectedCategoryList.add(category);
        else mSelectedCategoryList.remove(category);
        setSelectedAmount();
    }

    public List<Category> getSelectedCategoryList() {
        //checked
        return mSelectedCategoryList;
    }

    public Repository getRepository() {
        //checked
        return mRepository;
    }
}