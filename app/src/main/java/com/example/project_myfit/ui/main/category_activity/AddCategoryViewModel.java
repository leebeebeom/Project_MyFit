package com.example.project_myfit.ui.main.category_activity;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.database.ChildCategory;
import com.example.project_myfit.ui.main.database.CategoryDao;

import java.util.ArrayList;
import java.util.List;

public class AddCategoryViewModel extends AndroidViewModel {
    private final AddCategoryAdapter mAdapter;
    private final CategoryDao mDao;
    private List<ChildCategory> childCategoryListNotLive;


    public AddCategoryViewModel(Application application) {
        super(application);
        //Dao
        mDao = AppDataBase.getsInstance(application).clothingDao();
        //카테고리 셋팅
        //상의
        List<ChildCategory> childCategoryList = new ArrayList<>();
        childCategoryList.add(new ChildCategory(ChildCategory.SHORT_SLEEVE, "Top"));
        childCategoryList.add(new ChildCategory(ChildCategory.LONG_SLEEVE,"Top"));
        //하의
        childCategoryList.add(new ChildCategory(ChildCategory.SHORT_PANTS, "Bottom"));
        childCategoryList.add(new ChildCategory(ChildCategory.LONG_PANTS, "Bottom"));
        //치마
        childCategoryList.add(new ChildCategory(ChildCategory.SKIRT, "Bottom"));
        childCategoryList.add(new ChildCategory(ChildCategory.ONE_PIECE, "Bottom"));
        //아우터
        childCategoryList.add(new ChildCategory(ChildCategory.OUTER, "Outer"));
        childCategoryList.add(new ChildCategory(ChildCategory.COAT, "Outer"));
        //어댑터
        mAdapter = new AddCategoryAdapter(childCategoryList);
    }

    //인서트
    public void insert(ChildCategory childCategory) {
        new Thread(() -> mDao.insert(childCategory)).start();
    }

    //getAll
    public void getAllClothingNotLive() {
        new Thread(() -> childCategoryListNotLive = mDao.getAllCategoryNotLive()
        ).start();
    }

    public AddCategoryAdapter getAdapter() {
        return mAdapter;
    }

    public List<ChildCategory> getChildCategoryListNotLive() {
        return childCategoryListNotLive;
    }
}
