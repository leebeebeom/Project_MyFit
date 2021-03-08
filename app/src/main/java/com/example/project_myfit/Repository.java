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

    //category--------------------------------------------------------------------------------------
    public LiveData<List<Category>> getCategoryLive() {
        return mCategoryDao.getAllCategoryLive(0);
    }

    public List<Category> getCategoryListByParent(String parentCategory) {
        List<Category> categoryList = new ArrayList<>();
        Thread thread = new Thread(() -> categoryList.addAll(mCategoryDao.getCategoryListByParent(parentCategory, 0)));
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
        Thread thread = new Thread(() -> largestOrder.set(mCategoryDao.getCategoryLargestOrder()));
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
            mCategoryDao.categoryInsert(category);
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

    public void categoryInsert(Category category) {
        new Thread(() -> mCategoryDao.categoryInsert(category)).start();
    }

    public void categoryUpdate(Category category) {
        new Thread(() -> mCategoryDao.categoryUpdate(category)).start();
    }

    public void categoryUpdate(List<Category> categoryList) {
        new Thread(() -> mCategoryDao.categoryUpdate(categoryList)).start();
    }
    //----------------------------------------------------------------------------------------------

    //folder----------------------------------------------------------------------------------------
    public LiveData<List<Folder>> getFolderLiveByFolder(long folderId) {
        return mFolderDao.getFolderLiveByFolder(folderId, 0);
    }

    public LiveData<List<Folder>> getAllFolderLive() {
        return mFolderDao.getAllFolderLive(0);
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

    public List<Folder> getAllFolderListByParent(String parentCategory) {
        List<Folder> folderList = new ArrayList<>();
        Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getAllFolderListByParent(parentCategory, 0)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return folderList;
    }

    public List<Long> getFolderFolderIdByParent(String parentCategory) {
        List<Long> folderFolderIdList = new ArrayList<>();
        Thread thread = new Thread(() -> folderFolderIdList.addAll(mFolderDao.getFolderFolderIdByParent(parentCategory, 0)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return folderFolderIdList;
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
        Thread thread = new Thread(() -> largestOrder.set(mFolderDao.getFolderLargestOrder()));
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
        new Thread(() -> mFolderDao.folderInsert(folder)).start();
    }

    public void folderUpdate(Folder folder) {
        new Thread(() -> mFolderDao.folderUpdate(folder)).start();
    }

    public void folderUpdate(List<Folder> folderList) {
        new Thread(() -> mFolderDao.folderUpdate(folderList)).start();
    }

    public List<Folder> getAllFolder() {
        List<Folder> folderList = new ArrayList<>();
        Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getAllFolder(0)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return folderList;
    }
    //----------------------------------------------------------------------------------------------

    //size------------------------------------------------------------------------------------------
    public LiveData<List<Size>> getAllSizeLiveByFolder(long folderId) {
        return mSizeDao.getAllSizeLiveByFolder(folderId, 0);
    }

    public List<Long> getSizeFolderIdByParent(String parentCategory) {
        List<Long> sizeFolderIdList = new ArrayList<>();
        Thread thread = new Thread(() -> sizeFolderIdList.addAll(mSizeDao.getSizeFolderIdByParent(parentCategory, 0)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sizeFolderIdList;
    }

    public LiveData<List<Size>> getAllSizeLive() {
        return mSizeDao.getAllSizeLive(0);
    }

    public List<Size> getAllSizeListByParent(String parentCategory) {
        List<Size> sizeList = new ArrayList<>();
        Thread thread = new Thread(() -> sizeList.addAll(mSizeDao.getAllSizeListByParent(parentCategory, 0)));
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

    public List<String> getSizeBrandList() {
        List<String> brandList = new ArrayList<>();
        Thread thread = new Thread(() -> brandList.addAll(mSizeDao.getSizeBrandList(0)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return brandList;
    }

    public List<String> getSizeNameList() {
        List<String> nameList = new ArrayList<>();
        Thread thread = new Thread(() -> nameList.addAll(mSizeDao.getSizeNameList(0)));
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
        Thread thread = new Thread(() -> largestOrder.set(mSizeDao.getSizeLargestOrder()));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return largestOrder.get();
    }

    public void sizeInsert(Size size) {
        new Thread(() -> mSizeDao.sizeInsert(size)).start();
    }

    public void sizeUpdate(Size size) {
        new Thread(() -> mSizeDao.sizeUpdate(size)).start();
    }

    public void sizeUpdate(List<Size> sizeList) {
        new Thread(() -> mSizeDao.sizeUpdate(sizeList)).start();
    }

    public List<Size> getAllSize() {
        List<Size> sizeList = new ArrayList<>();
        Thread thread = new Thread(() -> sizeList.addAll(mSizeDao.getAllSize(0)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sizeList;
    }
    //----------------------------------------------------------------------------------------------

    //recent search---------------------------------------------------------------------------------
    public LiveData<List<RecentSearch>> getRecentSearchLive() {
        return mRecentSearchDao.getRecentSearchList();
    }

    public void insertRecentSearch(RecentSearch recentSearch) {
        new Thread(() -> mRecentSearchDao.insertRecentSearch(recentSearch)).start();
    }

    public void deleteRecentSearch(RecentSearch recentSearch) {
        new Thread(() -> mRecentSearchDao.deleteRecentSearch(recentSearch)).start();
    }
    //----------------------------------------------------------------------------------------------
}
