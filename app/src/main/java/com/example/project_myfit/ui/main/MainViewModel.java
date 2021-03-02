package com.example.project_myfit.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.project_myfit.Repository;
import com.example.project_myfit.ui.main.adapter.CategoryAdapter;
import com.example.project_myfit.ui.main.database.Category;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private int mSort;
    private final List<Category> mSelectedItem;
    private final MutableLiveData<Integer> mSelectedAmount;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mSelectedAmount = new MutableLiveData<>();
        mSelectedItem = new ArrayList<>();
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

    public void categorySelected(Category category, boolean isChecked, int position, int viewPagerPosition, @NotNull List<CategoryAdapter> adapterList) {
        adapterList.get(viewPagerPosition).setSelectedPosition(position);
        if (isChecked) mSelectedItem.add(category);
        else mSelectedItem.remove(category);
        setSelectedAmount();
    }

    public void categoryNameEdit(String categoryName) {
        mSelectedItem.get(0).setCategory(categoryName);
        mRepository.categoryUpdate(mSelectedItem.get(0));
    }

    public void selectedItemDelete() {
        for (Category category : mSelectedItem) category.setIsDeleted(1);
        mRepository.categoryUpdate(mSelectedItem);
    }

    public List<Category> getSelectedItem() {
        return mSelectedItem;
    }

    public Repository getRepository() {
        return mRepository;
    }
}