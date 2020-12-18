package com.example.project_myfit.ui.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.database.CategoryDao;
import com.example.project_myfit.ui.main.database.ChildCategory;
import com.example.project_myfit.ui.main.database.ParentCategory;
import com.example.project_myfit.ui.main.nodedapter.MainFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final CategoryDao mCategoryDao;
    private final MainFragmentAdapter mAdapter;


    public MainViewModel(@NonNull Application application) {
        super(application);
        //Dao
        mCategoryDao = AppDataBase.getsInstance(application).categoryDao();
        //어댑터
        mAdapter = new MainFragmentAdapter();
    }

    //인서트
    public void insert(ChildCategory childCategory) {
        new Thread(() -> mCategoryDao.insert(childCategory)).start();
    }

    //로드
    public LiveData<List<ChildCategory>> getAll() {
        return mCategoryDao.getAllCategory();
    }

    //데이터 생성
    public List<BaseNode> getData(List<ChildCategory> childCategoryList) {
        List<BaseNode> parentCategories = new ArrayList<>();
        List<BaseNode> top = new ArrayList<>();
        List<BaseNode> bottom = new ArrayList<>();
        List<BaseNode> outer = new ArrayList<>();
        List<BaseNode> etc = new ArrayList<>();
        for (ChildCategory childCategory : childCategoryList) {
            if (childCategory.getParentCategory().equals("Top")) {
                top.add(childCategory);
            }
        }
        for (ChildCategory childCategory : childCategoryList) {
            if (childCategory.getParentCategory().equals("Bottom")) {
                bottom.add(childCategory);
            }
        }
        for (ChildCategory childCategory : childCategoryList) {
            if (childCategory.getParentCategory().equals("Outer")) {
                outer.add(childCategory);
            }
        }
        for (ChildCategory childCategory : childCategoryList) {
            if (childCategory.getParentCategory().equals("ETC")) {
                etc.add(childCategory);
            }
        }
        parentCategories.add(new ParentCategory("Top", top));
        parentCategories.add(new ParentCategory("Bottom", bottom));
        parentCategories.add(new ParentCategory("Outer", outer));
        parentCategories.add(new ParentCategory("ETC", etc));
        return parentCategories;
    }

    public MainFragmentAdapter getMainFragmentAdapter() {
        return mAdapter;
    }
}