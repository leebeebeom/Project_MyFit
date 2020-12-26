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


    public MainViewModel(@NonNull Application application) {
        super(application);
        //Dao
        mCategoryDao = AppDataBase.getsInstance(application).categoryDao();
        mSizeDao = AppDataBase.getsInstance(application).sizeDao();
        //Preference
        mPreferences = application.getSharedPreferences(FIRST_RUN, Context.MODE_PRIVATE);
        mFirstRun = mPreferences.getBoolean(FIRST_RUN, true);
    }

    //First Run Check
    public void setPreferences() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(FIRST_RUN, false);
        editor.apply();
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
        new Thread(() -> mCurrentSizeList = mSizeDao.getAllSizeByFolderNotLive(id)).start();
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
    public LiveData<List<ChildCategory>> getAllChild() {
        return mCategoryDao.getAllChild();
    }

    //Generate Parent Category List
    public List<BaseNode> generateParent(List<ChildCategory> childCategoryList) {
        List<BaseNode> parentCategoryList = new ArrayList<>();
        List<BaseNode> parentTop = new ArrayList<>();
        List<BaseNode> parentBottom = new ArrayList<>();
        List<BaseNode> parentOuter = new ArrayList<>();
        List<BaseNode> parentETC = new ArrayList<>();
        for (ChildCategory childCategory : childCategoryList) {
            if (childCategory.getParentCategory().equals(TOP)) {
                parentTop.add(childCategory);
            }
        }
        for (ChildCategory childCategory : childCategoryList) {
            if (childCategory.getParentCategory().equals(BOTTOM)) {
                parentBottom.add(childCategory);
            }
        }
        for (ChildCategory childCategory : childCategoryList) {
            if (childCategory.getParentCategory().equals(OUTER)) {
                parentOuter.add(childCategory);
            }
        }
        for (ChildCategory childCategory : childCategoryList) {
            if (childCategory.getParentCategory().equals(ETC)) {
                parentETC.add(childCategory);
            }
        }
        parentCategoryList.add(new ParentCategory(TOP, parentTop));
        parentCategoryList.add(new ParentCategory(BOTTOM, parentBottom));
        parentCategoryList.add(new ParentCategory(OUTER, parentOuter));
        parentCategoryList.add(new ParentCategory(ETC, parentETC));
        return parentCategoryList;
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

    public void setFirstRun(boolean mFirstRun) {
        this.mFirstRun = mFirstRun;
    }
}