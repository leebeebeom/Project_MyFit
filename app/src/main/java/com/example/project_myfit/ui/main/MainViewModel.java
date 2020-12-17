package com.example.project_myfit.ui.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.database.CategoryDao;
import com.example.project_myfit.ui.main.database.ChildCategory;
import com.example.project_myfit.ui.main.database.ParentCategory;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    public static final String FIRST_RUN = "first run";
    public static final String SORT = "sort";

    public static final int ID_ASC = 0;
    public static final int ID_DESC = 1;
    public static final int NAME_ASC = 2;
    public static final int NAME_DESC = 3;

    private final CategoryDao mCategoryDao;
    private final SharedPreferences mPreferences_first_run;
    private final SharedPreferences mPreferences_sort;
    private int mRunCount;
    private int mSort;
    private CategoryAdapter mAdapter;
    private RecyclerViewExpandableItemManager mManager;
    private RecyclerView.Adapter mAdapter2;


    public MainViewModel(@NonNull Application application) {
        super(application);
        //Dao
        mCategoryDao = AppDataBase.getsInstance(application).clothingDao();
        //first_run 프리퍼런스
        mPreferences_first_run = application.getSharedPreferences(FIRST_RUN, Context.MODE_PRIVATE);
        mRunCount = mPreferences_first_run.getInt(FIRST_RUN, 0);
        //최초실행일시 초기 db 셋팅
        if (mRunCount == 0) {
            setFirstRunDb();
        }
        //sort 프리퍼런스
        mPreferences_sort = application.getSharedPreferences(SORT, Context.MODE_PRIVATE);
        //실행시 sort 값 저장
        mSort = mPreferences_sort.getInt(SORT, 0);
        //어드밴스드 리사이클러 뷰 초기화
        mManager = new RecyclerViewExpandableItemManager(null);
    }


    //MainFragment에서 최초실행일시 RunCount+1, Preference에 저장
    public void setRunCount() {
        this.mRunCount = 1;
        SharedPreferences.Editor editor = mPreferences_first_run.edit();
        editor.putInt(FIRST_RUN, mRunCount);
        editor.apply();
    }

    //정렬 순서별 라이브 데이터 반환
    public LiveData<List<ChildCategory>> getSort() {
        LiveData<List<ChildCategory>> clothingList;
        if (mSort == ID_ASC) {
            clothingList = mCategoryDao.getAllCategory();
        } else if (mSort == ID_DESC) {
            clothingList = mCategoryDao.getAllCategoryByIdInverse();
        } else if (mSort == NAME_ASC) {
            clothingList = mCategoryDao.getAllCategoryByName();
        } else {
            clothingList = mCategoryDao.getAllCategoryByNameInverse();
        }
        return clothingList;
    }

    //최초 실행시 db 셋팅
    public void setFirstRunDb() {
        List<ChildCategory> childCategoryList = new ArrayList<>();
        childCategoryList.add(new ChildCategory(ChildCategory.SHORT_SLEEVE, "Top"));
        childCategoryList.add(new ChildCategory(ChildCategory.LONG_SLEEVE, "Top"));
        childCategoryList.add(new ChildCategory(ChildCategory.LONG_PANTS, "Bottom"));
        childCategoryList.add(new ChildCategory(ChildCategory.OUTER, "Outer"));
        new Thread(() -> {
            for (ChildCategory childCategory : childCategoryList) {
                mCategoryDao.insert(childCategory);
            }
        }).start();
    }

    //ImageView set_image 속성 정의
    @BindingAdapter("set_image")
    public static void setImage(ImageView imageView, Integer integer) {
        Glide.with(imageView.getRootView())
                .load("")
                .error(integer)
                .into(imageView);

    }


    public CategoryAdapter getAdapter() {
        return mAdapter;
    }

    public SharedPreferences getPreferences_sort() {
        return mPreferences_sort;
    }

    public int getRunCount() {
        return mRunCount;
    }

    public void setSortInt(int mSort) {
        this.mSort = mSort;
    }

    public CategoryDao getCategoryDao() {
        return mCategoryDao;
    }

    //인서트
    public void insert(ChildCategory childCategory) {
        new Thread(() -> mCategoryDao.insert(childCategory)).start();
    }

    public LiveData<List<ChildCategory>> getAll() {
        return mCategoryDao.getAllCategory();
    }

    public RecyclerViewExpandableItemManager getManager() {
        return mManager;
    }


    public RecyclerView.Adapter getmAdapter2() {
        return mAdapter2;
    }

    public List<ParentCategory> getData(List<ChildCategory> childCategoryList) {
        List<ParentCategory> parentCategories = new ArrayList<>();
        List<ChildCategory> top = new ArrayList<>();
        List<ChildCategory> bottom = new ArrayList<>();
        List<ChildCategory> outer = new ArrayList<>();
        List<ChildCategory> etc = new ArrayList<>();
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
}