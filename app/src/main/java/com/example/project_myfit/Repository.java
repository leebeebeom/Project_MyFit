package com.example.project_myfit;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.database.CategoryDao;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.FolderDao;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Repository {
    private final CategoryDao mCategoryDao;
    private final FolderDao mFolderDao;
    private final SizeDao mSizeDao;

    public Repository(Context context) {
        mCategoryDao = AppDataBase.getsInstance(context).categoryDao();
        mFolderDao = AppDataBase.getsInstance(context).folderDao();
        mSizeDao = AppDataBase.getsInstance(context).sizeDao();
    }

    //Category--------------------------------------------------------------------------------------
    public LiveData<List<Category>> getCategoryLive(String parentCategory) {
        return mCategoryDao.getCategoryLive(parentCategory);
    }

    public List<Category> getCategoryList(String parentCategory) {
        List<Category> categoryList = new ArrayList<>();
        Thread thread = new Thread(() -> categoryList.addAll(mCategoryDao.getCategoryList(parentCategory)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return categoryList;
    }

    public int getCategoryLargestOrder() {
        AtomicInteger largestOrder = new AtomicInteger();
        Thread thread = new Thread(() -> largestOrder.set(mCategoryDao.getLargestOrder()));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return largestOrder.get();
    }

    public void categoryInsert(Category category) {
        new Thread(() -> mCategoryDao.insert(category)).start();
    }

    public void categoryUpdate(Category category) {
        new Thread(() -> mCategoryDao.update(category)).start();
    }

    public void categoryUpdate(List<Category> categoryList) {
        new Thread(() -> mCategoryDao.update(categoryList)).start();
    }

    public void categoryDelete(Category category) {
        new Thread(() -> mCategoryDao.delete(category)).start();
    }
    //----------------------------------------------------------------------------------------------

    //Folder----------------------------------------------------------------------------------------
    public LiveData<List<Folder>> getFolderLive(long folderId) {
        return mFolderDao.getFolderLive(folderId, false);
    }

    public List<Folder> getAllFolder() {
        List<Folder> folderList = new ArrayList<>();
        Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getFolderList(false)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return folderList;
    }

    public int getFolderLargestOrder() {
        AtomicInteger largestOrder = new AtomicInteger();
        Thread thread = new Thread(() -> largestOrder.set(mFolderDao.getLargestOrder()));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return largestOrder.get();
    }

    public void folderInsert(Folder folder) {
        new Thread(() -> mFolderDao.insert(folder)).start();
    }

    public void folderUpdate(Folder folder) {
        new Thread(() -> mFolderDao.update(folder)).start();
    }

    public void folderUpdate(List<Folder> folderList) {
        new Thread(() -> mFolderDao.update(folderList)).start();
    }

    public void folderDelete(List<Folder> folderList) {
        new Thread(() -> mFolderDao.delete(folderList)).start();
    }
    //----------------------------------------------------------------------------------------------

    //Size------------------------------------------------------------------------------------------
    public LiveData<List<Size>> getSizeLive(long folderId) {
        return mSizeDao.getSizeLive(folderId, false);
    }

    public List<Size> getSizeList(long folderId) {
        List<Size> sizeList = new ArrayList<>();
        Thread thread = new Thread(() -> sizeList.addAll(mSizeDao.getSizeList(folderId, false)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sizeList;
    }

    public Size getSize(int id) {
        AtomicReference<Size> size = new AtomicReference<>();
        Thread thread = new Thread(() -> size.set(mSizeDao.getSize(id)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return size.get();
    }

    public int getSizeLargestOrder() {
        AtomicInteger largestOrder = new AtomicInteger();
        Thread thread = new Thread(() -> largestOrder.set(mSizeDao.getLargestOrder()));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return largestOrder.get();
    }

    public void sizeInsert(Size size) {
        new Thread(() -> mSizeDao.insert(size)).start();
    }

    public void sizeUpdate(Size size) {
        new Thread(() -> mSizeDao.update(size)).start();
    }

    public void sizeUpdate(List<Size> sizeList) {
        new Thread(() -> mSizeDao.update(sizeList)).start();
    }

    public void sizeDelete(Size size) {
        new Thread(() -> mSizeDao.delete(size)).start();
    }

    public void sizeDelete(List<Size> sizeList) {
        new Thread(() -> mSizeDao.delete(sizeList)).start();
    }
    //----------------------------------------------------------------------------------------------
}
