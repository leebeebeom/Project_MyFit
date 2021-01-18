package com.example.project_myfit.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.database.CategoryDao;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final CategoryDao mCategoryDao;
    private final SizeDao mSizeDao;
    private List<Size> mCurrentSizeList;
    private int mLargestOrder;


    public MainViewModel(@NonNull Application application) {
        super(application);
        //Dao
        mCategoryDao = AppDataBase.getsInstance(application).categoryDao();
        mSizeDao = AppDataBase.getsInstance(application).sizeDao();
    }

    //Insert
    public void insert(Category category) {
        new Thread(() -> mCategoryDao.insert(category)).start();
    }

    //Update
    public void update(Category category) {
        new Thread(() -> mCategoryDao.update(category)).start();
    }

    //Delete
    public void delete(Category category) {
        new Thread(() -> mCategoryDao.delete(category)).start();
    }

    //Order Update
    public void updateOrder(List<Category> categoryList) {
        new Thread(() -> mCategoryDao.update(categoryList)).start();
    }

    //Get Size List By Folder Id
    public void getAllSizeByFolder(long folderId) {
        new Thread(() -> mCurrentSizeList = mSizeDao.getSizeList(folderId, false)).start();
    }


    //Get All Category
    public LiveData<List<Category>> getAllChild(String parentCategory) {
        return mCategoryDao.getCategoryLive(parentCategory);
    }

    //Get Largest Order Number
    public void setLargestOrder() {
        new Thread(() -> mLargestOrder = mCategoryDao.getLargestOrder()).start();
    }

    public int getLargestOrder() {
        return mLargestOrder;
    }
}