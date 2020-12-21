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
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    public static final String FIRST_RUN = "first run";
    public static final String TOP = "Top";
    public static final String BOTTOM = "Bottom";
    public static final String OUTER = "Outer";
    public static final String ETC = "ETC";
    private final CategoryDao mCategoryDao;
    private final SizeDao mSizeDao;
    private List<Size> mCurrentSizeList;
    private final SharedPreferences mPreferences;
    private boolean mFirstRun;
    private int mLargestOrder;
    private List<ChildCategory> mCurrentChildList;


    public MainViewModel(@NonNull Application application) {
        super(application);
        //Dao
        mCategoryDao = AppDataBase.getsInstance(application).categoryDao();
        mSizeDao = AppDataBase.getsInstance(application).sizeDao();
        //Preference
        mPreferences = application.getSharedPreferences(FIRST_RUN, Context.MODE_PRIVATE);
        mFirstRun = mPreferences.getBoolean(FIRST_RUN, true);
    }

    /*
    TODO
    최초 실행 후 갱신 완료 후 프리퍼런스 실행
     */
    //First Run Check
    public void setPreferences() {
        if (mFirstRun) {
            SharedPreferences.Editor editor = mPreferences.edit();
            mFirstRun = false;
            editor.putBoolean(FIRST_RUN, mFirstRun);
            editor.apply();
        }
    }

    //Insert
    public void insert(ChildCategory childCategory) {
        new Thread(() -> mCategoryDao.insert(childCategory)).start();
    }

    //Update
    public void update(ChildCategory childCategory) {
        new Thread(() -> mCategoryDao.update(childCategory)).start();
    }

    //Delete
    public void delete(ChildCategory childCategory) {
        new Thread(() -> mCategoryDao.delete(childCategory)).start();
    }

    //Order Update
    public void updateOrder(List<ChildCategory> childCategoryList) {
        new Thread(() -> mCategoryDao.updateOrder(childCategoryList)).start();
    }

    //Get Size List By Folder Id
    public void getAllSizeByFolder(int id) {
        new Thread(() -> mCurrentSizeList = mSizeDao.getAllSizeByFolder(id)).start();
    }

    //Delete Size List By Folder Id
    public void deleteSizeByFolder(int id) {
        new Thread(() -> mSizeDao.deleteSizeByFolder(id)).start();
    }

    //Restore Deleted Size List
    public void restoreDeletedSize() {
        new Thread(() -> mSizeDao.restoreDeletedSize(mCurrentSizeList)).start();
    }

    //Get All Child Category By Order
    public LiveData<List<ChildCategory>> getAllChildByOrder() {
        return mCategoryDao.getAllChildByOrder();
    }

    //Generate Parent Category List
    public List<BaseNode> generateParent(List<ChildCategory> childCategoryList) {
        List<BaseNode> parentCategories = new ArrayList<>();
        List<BaseNode> top = new ArrayList<>();
        List<BaseNode> bottom = new ArrayList<>();
        List<BaseNode> outer = new ArrayList<>();
        List<BaseNode> etc = new ArrayList<>();
        for (ChildCategory childCategory : childCategoryList) {
            if (childCategory.getParentCategory().equals(TOP)) {
                top.add(childCategory);
            }
        }
        for (ChildCategory childCategory : childCategoryList) {
            if (childCategory.getParentCategory().equals(BOTTOM)) {
                bottom.add(childCategory);
            }
        }
        for (ChildCategory childCategory : childCategoryList) {
            if (childCategory.getParentCategory().equals(OUTER)) {
                outer.add(childCategory);
            }
        }
        for (ChildCategory childCategory : childCategoryList) {
            if (childCategory.getParentCategory().equals(ETC)) {
                etc.add(childCategory);
            }
        }
        parentCategories.add(new ParentCategory(TOP, top));
        parentCategories.add(new ParentCategory(BOTTOM, bottom));
        parentCategories.add(new ParentCategory(OUTER, outer));
        parentCategories.add(new ParentCategory(ETC, etc));
        return parentCategories;
    }

    //Get Largest Order Number
    public void setLargestOrder() {
        new Thread(() -> mLargestOrder = mCategoryDao.getLargestOrder()).start();
    }

    public boolean isFirstRun() {
        return mFirstRun;
    }

    public int getLargestOrder() {
        return mLargestOrder;
    }

    public List<ChildCategory> getCurrentChildList() {
        return mCurrentChildList;
    }

    public void setCurrentChildList(List<ChildCategory> childCategoryList) {
        this.mCurrentChildList = childCategoryList;
    }
}