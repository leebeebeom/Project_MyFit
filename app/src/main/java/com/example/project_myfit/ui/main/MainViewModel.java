package com.example.project_myfit.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_myfit.Repository;
import com.example.project_myfit.ui.main.adapter.CategoryAdapter;
import com.example.project_myfit.ui.main.database.Category;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private final CategoryAdapter mAdapter;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mAdapter = new CategoryAdapter(this);
    }

    public void updateCategoryList(List<Category> categoryList) {
        mRepository.categoryUpdate(categoryList);
    }

    public CategoryAdapter getAdapter() {
        return mAdapter;
    }
}