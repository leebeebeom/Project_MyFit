package com.example.project_myfit.ui.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.database.CategoryDao;
import com.example.project_myfit.ui.main.database.ChildCategory;
import com.example.project_myfit.ui.main.database.ParentCategory;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;
import com.example.project_myfit.ui.main.nodedapter.MainFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    public static final String FIRST_RUN = "first run";
    private final CategoryDao mCategoryDao;
    private final SizeDao mSizeDao;
    private List<Size> mCurrentSizeList;
    private final MainFragmentAdapter mAdapter;
    private final SharedPreferences mPreferences;
    private boolean mFirstRun;


    public MainViewModel(@NonNull Application application) {
        super(application);
        //Dao
        mCategoryDao = AppDataBase.getsInstance(application).categoryDao();
        mSizeDao = AppDataBase.getsInstance(application).sizeDao();
        //어댑터
        mAdapter = new MainFragmentAdapter();
        //프리퍼런스
        mPreferences = application.getSharedPreferences(FIRST_RUN, Context.MODE_PRIVATE);
        mFirstRun = mPreferences.getBoolean(FIRST_RUN, true);

    }

    //최초 실행 체크
    public void setPreferences() {
        SharedPreferences.Editor editor = mPreferences.edit();
        mFirstRun = false;
        editor.putBoolean(FIRST_RUN, mFirstRun);
        editor.apply();
    }

    //인서트
    public void insert(ChildCategory childCategory) {
        new Thread(() -> mCategoryDao.insert(childCategory)).start();
    }

    //업데이트
    public void update(ChildCategory childCategory) {
        new Thread(() -> mCategoryDao.update(childCategory)).start();
    }

    //딜리트
    public void delete(ChildCategory childCategory) {
        new Thread(() -> mCategoryDao.delete(childCategory)).start();
    }

    public void getCurrentSizeList(int id) {
        new Thread(() -> mCurrentSizeList = mSizeDao.getAllSizeByFolderId(id)).start();
    }

    public void deleteSizeListByFolderId(int id) {
        new Thread(() -> mSizeDao.deleteByFolderId(id)).start();
    }

    public void restoreCurrentSizeList() {
        new Thread(() -> mSizeDao.insertList(mCurrentSizeList)).start();
    }


    //로드
    public LiveData<List<ChildCategory>> getAll() {
        return mCategoryDao.getAllChildCategory();
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

    public boolean isFirstRun() {
        return mFirstRun;
    }

}