package com.example.project_myfit.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.project_myfit.data.dao.CategoryDao;
import com.example.project_myfit.data.dao.FolderDao;
import com.example.project_myfit.data.dao.RecentSearchDao;
import com.example.project_myfit.data.dao.SizeDao;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.RecentSearch;
import com.example.project_myfit.data.model.Size;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

//TODO ASyncTask 대안 찾기

public class Repository {
    @NotNull
    @Contract("_ -> new")
    public static CategoryRepository getCategoryRepository(Context context) {
        return new CategoryRepository(context);
    }

    @NotNull
    @Contract("_ -> new")
    public static FolderRepository getFolderRepository(Context context) {
        return new FolderRepository(context);
    }

    @NotNull
    @Contract("_ -> new")
    public static SizeRepository getSizeRepository(Context context) {
        return new SizeRepository(context);
    }

    @NotNull
    @Contract("_ -> new")
    public static RecentSearchRepository getRecentSearchRepository(Context context) {
        return new RecentSearchRepository(context);
    }

    public static class CategoryRepository {
        private final CategoryDao mCategoryDao;

        public CategoryRepository(Context context) {
            mCategoryDao = AppDataBase.getsInstance(context).categoryDao();
        }

        public LiveData<List<Category>> getCategoryLive() {
            //order by orderNumber
            //used in mainFragment -> viewPagerAdapter(Sort.categorySort)->categoryAdapter
            return mCategoryDao.getCategoryLive();
        }

        public LiveData<Category> getCategoryLive(long id) {
            return mCategoryDao.getCategoryLive(id);
        }

        public LiveData<List<Category>> getDeletedCategoryLive() {
            return mCategoryDao.getDeletedCategoryLive();
        }

        public List<Category> getCategoryList() {
            //order by orderNumber
            //used in orderNumberInit
            List<Category> allCategoryList = new ArrayList<>();
            Thread thread = new Thread(() -> allCategoryList.addAll(mCategoryDao.getCategoryList()));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return allCategoryList;
        }

        public List<Category> getCategoryList(String parentCategory) {
            //order by orderNumber
            //used in treeViewModel -> sort.categorySort
            List<Category> categoryListByParentCategory = new ArrayList<>();
            Thread thread = new Thread(() -> categoryListByParentCategory.addAll(mCategoryDao.getCategoryList(parentCategory)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return categoryListByParentCategory;
        }

        public LiveData<List<String>> getDeletedCategoryNameLive() {
            return mCategoryDao.getDeletedCategoryNameLive();
        }

        public List<String> getCategoryNameList(String parentCategory) {
            //order by id
            //used in DialogViewModel -> isSameNameCategory
            List<String> categoryNameList = new ArrayList<>();
            Thread thread = new Thread(() -> categoryNameList.addAll(mCategoryDao.getCategoryNameList(parentCategory)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return categoryNameList;
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

        public int getCategoryLargestOrderPlus1() {
            AtomicInteger largestOrder = new AtomicInteger();
            Thread thread = new Thread(() -> largestOrder.set(mCategoryDao.getCategoryLargestOrder()));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return largestOrder.get() + 1;
        }

        public void categoryInsert(Category category) {
            new Thread(() -> mCategoryDao.categoryInsert(category)).start();
        }

        public void categoryInsert(List<Category> categoryList) {
            new Thread(() -> mCategoryDao.categoryInsert(categoryList)).start();
        }

        public void categoryUpdate(@NotNull List<Category> categoryList) {
            new Thread(() -> mCategoryDao.categoryUpdate(categoryList)).start();
        }

        public void categoryUpdate(Category category) {
            new Thread(() -> mCategoryDao.categoryUpdate(category)).start();
        }
    }

    public static class FolderRepository {
        private final FolderDao mFolderDao;

        public FolderRepository(Context context) {
            mFolderDao = AppDataBase.getsInstance(context).folderDao();
        }

        public LiveData<List<Folder>> getFolderLive() {
            //order by folderName
            //used in searchFragment
            return mFolderDao.getFolderLive();
        }

        public LiveData<List<Folder>> getFolderLive(long parentId) {
            //order by orderNumber
            //used in listFragment
            return mFolderDao.getFolderLive(parentId);
        }

        public LiveData<List<Folder>> getDeletedFolderLive() {
            return mFolderDao.getDeletedFolderLive();
        }

        public List<Folder> getFolderList() {
            //order by orderNumber
            //used in orderNumberInit
            List<Folder> folderList = new ArrayList<>();
            Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getFolderList()));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return folderList;
        }

        public List<Folder> getFolderList(String parentCategory) {
            //order by orderNumber
            //used in listViewModel -> getFolderHistory(sort.folderSort)
            //used in treeViewDialog -> sort.folderSort
            List<Folder> folderList = new ArrayList<>();
            Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getFolderList(parentCategory)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return folderList;
        }

        public List<Folder> getFolderList(long parentId) {
            //order by id
            //used in mainFragment -> selectedCategoryDelete
            //used in listFragment -> selectedItemMove
            //used in selectedItemTreat -> deleteFolder
            List<Folder> folderList = new ArrayList<>();
            Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getFolderList(parentId)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return folderList;
        }

        public LiveData<List<String>> getFolderNameLive() {
            return mFolderDao.getFolderNameLive();
        }

        public List<String> getFolderNameList(long parentId) {
            //order by id
            //used in DialogViewModel -> isSameNameFolder
            List<String> folderNameList = new ArrayList<>();
            Thread thread = new Thread(() -> folderNameList.addAll(mFolderDao.getFolderNameList(parentId)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return folderNameList;
        }

        public List<Long> getFolderParentIdList(String parentCategory) {
            //order by id
            //used in TreeView -> treeCategoryHolder, treeFolderHolder
            //used in mainFragment -> categoryAdapter
            //used in listFragment -> folderAdapter
            List<Long> folderParentIdList = new ArrayList<>();
            Thread thread = new Thread(() -> folderParentIdList.addAll(mFolderDao.getFolderParentIdList(parentCategory)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return folderParentIdList;
        }

        public List<Long> getParentDeletedFolderParentIdList() {
            List<Long> folderParentIdList = new ArrayList<>();
            Thread thread = new Thread(() -> folderParentIdList.addAll(mFolderDao.getParentDeletedFolderParentIdList()));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return folderParentIdList;
        }

        public LiveData<Folder> getSingleFolderLive(long id) {
            return mFolderDao.getSingleFolderLive(id);
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

        public int getFolderLargestOrderPlus1() {
            AtomicInteger largestOrder = new AtomicInteger();
            Thread thread = new Thread(() -> largestOrder.set(mFolderDao.getFolderLargestOrder()));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return largestOrder.get() + 1;
        }

        public void folderInsert(Folder folder) {
            new Thread(() -> mFolderDao.folderInsert(folder)).start();
        }

        public void folderUpdate(Folder folder) {
            new Thread(() -> mFolderDao.folderUpdate(folder)).start();
        }

        public void folderUpdate(@NotNull List<Folder> folderList) {
            new Thread(() -> mFolderDao.folderUpdate(folderList)).start();
        }
    }

    public static class SizeRepository {
        private final SizeDao mSizeDao;

        public SizeRepository(Context context) {
            mSizeDao = AppDataBase.getsInstance(context).sizeDao();
        }

        public LiveData<List<Size>> getSizeLive() {
            //order by name
            //used in searchFragment
            return mSizeDao.getSizeLive();
        }

        public LiveData<List<Size>> getDeletedSizeLive() {
            return mSizeDao.getDeletedSizeLive();
        }

        public LiveData<List<Size>> getSizeLive(long parentId) {
            //order by orderNumber DESC
            //used in listFragment
            return mSizeDao.getSizeLive(parentId);
        }

        public List<Size> getSizeList() {
            //order by orderNumber
            //used in orderNumberInit
            List<Size> sizeList = new ArrayList<>();
            Thread thread = new Thread(() -> sizeList.addAll(mSizeDao.getSizeList()));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return sizeList;
        }

        public List<Size> getSizeList(long parentId) {
            //order by id
            //used in mainFragment -> selectCategoryDelete
            //used in selectedItemTreat -> deleteFolder
            List<Size> sizeList = new ArrayList<>();
            Thread thread = new Thread(() -> sizeList.addAll(mSizeDao.getSizeList(parentId)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return sizeList;
        }

        public List<Long> getSizeParentIdList(String parentCategory) {
            //order by id
            //used in TreeView -> treeCategoryHolder, treeFolderHolder
            //used in mainFragment -> categoryAdapter
            //used in listFragment -> folderAdapter
            List<Long> sizeParentIdList = new ArrayList<>();
            Thread thread = new Thread(() -> sizeParentIdList.addAll(mSizeDao.getSizeParentIdList(parentCategory)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return sizeParentIdList;
        }

        public List<Long> getParentDeletedSizeParentIdList() {
            List<Long> sizeParentIdList = new ArrayList<>();
            Thread thread = new Thread(() -> sizeParentIdList.addAll(mSizeDao.getParentDeletedSizeParentIdList()));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return sizeParentIdList;
        }

        public LiveData<List<String>> getSizeBrandLive() {
            return mSizeDao.getSizeBrandLive();
        }

        public LiveData<List<String>> getSizeNameLive() {
            return mSizeDao.getSizeNameLive();
        }

        public List<String> getSizeBrandList() {
            //order by brand
            //used in inputOutputFragment -> autoCompleteTextView
            List<String> brandList = new ArrayList<>();
            Thread thread = new Thread(() -> brandList.addAll(mSizeDao.getSizeBrandList()));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return brandList;
        }

        public Size getSize(long id) {
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

        public int getSizeLargestOrderPlus1() {
            AtomicInteger largestOrder = new AtomicInteger();
            Thread thread = new Thread(() -> largestOrder.set(mSizeDao.getSizeLargestOrder()));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return largestOrder.get() + 1;
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
    }

    public static class RecentSearchRepository {
        private final RecentSearchDao mRecentSearchDao;

        public RecentSearchRepository(Context context) {
            mRecentSearchDao = AppDataBase.getsInstance(context).recentSearchDao();
        }

        public LiveData<List<RecentSearch>> getRecentSearchLive(int type) {
            //order by id
            //used in searchFragment -> recentSearchAdapter
            return mRecentSearchDao.getRecentSearchLive(type);
        }

        public void recentSearchInsert(String wort, int type) {
            new Thread(() -> mRecentSearchDao.recentSearchInsert(new RecentSearch(wort, getCurrentDate(), type))).start();
        }

        public void recentSearchDelete(RecentSearch recentSearch) {
            new Thread(() -> mRecentSearchDao.recentSearchDelete(recentSearch)).start();
        }

        public void overLapRecentSearchReInsert(String word, int type) {
            new Thread(() -> {
                mRecentSearchDao.recentSearchDelete(mRecentSearchDao.getRecentSearch(word));
                mRecentSearchDao.recentSearchInsert(new RecentSearch(word, getCurrentDate(), type));
            }).start();
        }

        public void deleteAllRecentSearch() {
            new Thread(mRecentSearchDao::deleteAllRecentSearch).start();
        }

        @NotNull
        private String getCurrentDate() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일", Locale.getDefault());
            return dateFormat.format(new Date(System.currentTimeMillis()));
        }
    }
}
