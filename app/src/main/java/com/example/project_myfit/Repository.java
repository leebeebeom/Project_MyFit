package com.example.project_myfit;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.project_myfit.searchActivity.database.RecentSearch;
import com.example.project_myfit.searchActivity.database.RecentSearchDao;
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
    private final RecentSearchDao mRecentSearchDao;

    public Repository(Context context) {
        mCategoryDao = AppDataBase.getsInstance(context).categoryDao();
        mFolderDao = AppDataBase.getsInstance(context).folderDao();
        mSizeDao = AppDataBase.getsInstance(context).sizeDao();
        mRecentSearchDao = AppDataBase.getsInstance(context).recentSearchDao();
    }

    //Category--------------------------------------------------------------------------------------
    public LiveData<List<Category>> getCategoryLive() {
        return mCategoryDao.getCategoryLive(0);
    }

    public List<Category> getCategoryList(String parentCategory) {
        List<Category> categoryList = new ArrayList<>();
        Thread thread = new Thread(() -> categoryList.addAll(mCategoryDao.getCategoryList(parentCategory, 0)));
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

    public Category getCategory(long id) {
        AtomicReference<Category> category = new AtomicReference<>();
        Thread thread = new Thread(() -> category.set(mCategoryDao.getCategory(id)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return category.get();
    }

    public Category treeViewAddCategory(Category category) {
        AtomicReference<Category> addedCategory = new AtomicReference<>();
        Thread thread = new Thread(() -> {
            mCategoryDao.insert(category);
            addedCategory.set(mCategoryDao.getLatestCategory());
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return addedCategory.get();
    }

    public Category getLatestCategory() {
        AtomicReference<Category> category = new AtomicReference<>();
        Thread thread = new Thread(() -> category.set(mCategoryDao.getLatestCategory()));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return category.get();
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
    //----------------------------------------------------------------------------------------------

    //Folder----------------------------------------------------------------------------------------
    public LiveData<List<Folder>> getFolderLive(long folderId) {
        return mFolderDao.getFolderLive(folderId, 0);
    }

    public List<Folder> getAllFolderList() {
        List<Folder> folderList = new ArrayList<>();
        Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getAllFolderList(0)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return folderList;
    }

    public List<Folder> getAllFolderList2(String parentCategory) {
        List<Folder> folderList = new ArrayList<>();
        Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getAllFolderList2(parentCategory, 0)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return folderList;
    }

    public List<String> getFolderNameList() {
        List<String> folderNameList = new ArrayList<>();
        Thread thread = new Thread(() -> folderNameList.addAll(mFolderDao.getFolderNameList(0)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return folderNameList;
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

    public Folder getFolder(long id) {
        AtomicReference<Folder> folder = new AtomicReference<>();
        Thread thread = new Thread(() -> folder.set(mFolderDao.getFolder(id)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return folder.get();
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
        return mSizeDao.getSizeLive(folderId, 0);
    }

    public List<Size> getAllSizeList(String parentCategory) {
        List<Size> sizeList = new ArrayList<>();
        Thread thread = new Thread(() -> sizeList.addAll(mSizeDao.getAllSizeList(parentCategory, 0)));
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

    public List<String> getBrandList() {
        List<String> brandList = new ArrayList<>();
        Thread thread = new Thread(() -> brandList.addAll(mSizeDao.getBrandList(0)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return brandList;
    }

    public List<String> getNameList() {
        List<String> nameList = new ArrayList<>();
        Thread thread = new Thread(() -> nameList.addAll(mSizeDao.getNameList(0)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return nameList;
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

    public LiveData<List<RecentSearch>> getRecentSearchLive() {
        return mRecentSearchDao.getRecentSearchList();
    }

    public void insertRecentSearch(RecentSearch recentSearch) {
        new Thread(() -> mRecentSearchDao.insertRecentSearch(recentSearch)).start();
    }

    public void deleteRecentSearch(RecentSearch recentSearch) {
        new Thread(() -> mRecentSearchDao.deleteRecentSearch(recentSearch)).start();
    }
}
