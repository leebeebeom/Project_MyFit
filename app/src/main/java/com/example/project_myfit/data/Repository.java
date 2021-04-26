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

        public LiveData<List<Category>> getAllCategoryLive() {
            //order by orderNumber
            //used in mainFragment -> viewPagerAdapter(Sort.categorySort)->categoryAdapter
            return mCategoryDao.getAllCategoryLive();
        }

        public List<Category> getAllCategoryList() {
            //order by orderNumber
            //used in orderNumberInit
            List<Category> allCategoryList = new ArrayList<>();
            Thread thread = new Thread(() -> allCategoryList.addAll(mCategoryDao.getAllCategoryList()));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return allCategoryList;
        }

        public List<Category> getCategoryListByParentCategory(String parentCategory) {
            //order by orderNumber
            //used in treeViewModel -> sort.categorySort
            List<Category> categoryListByParentCategory = new ArrayList<>();
            Thread thread = new Thread(() -> categoryListByParentCategory.addAll(mCategoryDao.getCategoryListByParentCategory(parentCategory)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return categoryListByParentCategory;
        }

        public List<String> getCategoryNameListByParentCategory(String parentCategory) {
            //order by id
            //used in DialogViewModel -> isSameNameCategory
            List<String> categoryNameList = new ArrayList<>();
            Thread thread = new Thread(() -> categoryNameList.addAll(mCategoryDao.getCategoryNameListByParentCategory(parentCategory)));
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

        public LiveData<Category> getCategoryLive(long id){
            return mCategoryDao.getCategoryLive(id);
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

        public LiveData<List<Folder>> getAllFolderLive() {
            //order by folderName
            //used in searchFragment
            return mFolderDao.getAllFolderLive();
        }

        public LiveData<List<Folder>> getFolderLiveByParentId(long parentId) {
            //order by orderNumber
            //used in listFragment
            return mFolderDao.getFolderLiveByParentId(parentId);
        }

        public List<Folder> getAllFolderList() {
            //order by orderNumber
            //used in orderNumberInit
            List<Folder> folderList = new ArrayList<>();
            Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getAllFolderList()));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return folderList;
        }

        public List<Folder> getFolderListByParentCategory(String parentCategory) {
            //order by orderNumber
            //used in listViewModel -> getFolderHistory(sort.folderSort)
            //used in treeViewDialog -> sort.folderSort
            List<Folder> folderList = new ArrayList<>();
            Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getFolderListByParentCategory(parentCategory)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return folderList;
        }

        public List<Folder> getFolderListByParentId(long parentId) {
            //order by id
            //used in mainFragment -> selectedCategoryDelete
            //used in listFragment -> selectedItemMove
            //used in selectedItemTreat -> deleteFolder
            List<Folder> folderList = new ArrayList<>();
            Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getFolderListByParentId(parentId)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return folderList;
        }

        public LiveData<List<String>> getAllFolderNameLive() {
            return mFolderDao.getAllFolderNameLive();
        }

        public LiveData<List<String>> getFolderNameLiveByParentId(long parentId) {
            return mFolderDao.getFolderNameLiveByParentId(parentId);
        }

        public List<String> getFolderNameListByParentId(long parentId) {
            //order by id
            //used in DialogViewModel -> isSameNameFolder
            List<String> folderNameList = new ArrayList<>();
            Thread thread = new Thread(() -> folderNameList.addAll(mFolderDao.getFolderNameListByParentId(parentId)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return folderNameList;
        }

        public List<Long> getFolderParentIdListByParentCategory(String parentCategory) {
            //order by id
            //used in TreeView -> treeCategoryHolder, treeFolderHolder
            //used in mainFragment -> categoryAdapter
            //used in listFragment -> folderAdapter
            List<Long> folderParentIdList = new ArrayList<>();
            Thread thread = new Thread(() -> folderParentIdList.addAll(mFolderDao.getFolderParentIdListByParentCategory(parentCategory)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return folderParentIdList;
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

        public LiveData<Folder> getFolderLive(long id){
            return mFolderDao.getFolderLive(id);
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

        public LiveData<List<Size>> getAllSizeLive() {
            //order by name
            //used in searchFragment
            return mSizeDao.getAllSizeLive();
        }

        public LiveData<List<Size>> getSizeLiveByParentId(long parentId) {
            //order by orderNumber DESC
            //used in listFragment
            return mSizeDao.getSizeLiveByParentId(parentId);
        }

        public List<Size> getAllSizeList() {
            //order by orderNumber
            //used in orderNumberInit
            List<Size> sizeList = new ArrayList<>();
            Thread thread = new Thread(() -> sizeList.addAll(mSizeDao.getAllSizeList()));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return sizeList;
        }

        public List<Size> getSizeListByParentId(long parentId) {
            //order by id
            //used in mainFragment -> selectCategoryDelete
            //used in selectedItemTreat -> deleteFolder
            List<Size> sizeList = new ArrayList<>();
            Thread thread = new Thread(() -> sizeList.addAll(mSizeDao.getSizeListByParentId(parentId)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return sizeList;
        }

        public List<Long> getSizeParentIdListByParentCategory(String parentCategory) {
            //order by id
            //used in TreeView -> treeCategoryHolder, treeFolderHolder
            //used in mainFragment -> categoryAdapter
            //used in listFragment -> folderAdapter
            List<Long> sizeParentIdList = new ArrayList<>();
            Thread thread = new Thread(() -> sizeParentIdList.addAll(mSizeDao.getSizeParentIdListByParentCategory(parentCategory)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return sizeParentIdList;
        }

        public LiveData<List<String>> getAllSizeBrandLive() {
            return mSizeDao.getAllSizeBrandLive();
        }

        public LiveData<List<String>> getAllSizeNameLive() {
            return mSizeDao.getAllSizeNameLive();
        }

        public LiveData<List<String>> getSizeBrandLiveByParentId(long parentId) {
            return mSizeDao.getSizeBrandLiveByParentId(parentId);
        }

        public LiveData<List<String>> getSizeNameLiveByParentId(long parentId) {
            return mSizeDao.getSizeNameLiveByParentId(parentId);
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

        public LiveData<List<RecentSearch>> getAllRecentSearchLive() {
            //order by id
            //used in searchFragment -> recentSearchAdapter
            return mRecentSearchDao.getAllRecentSearchLive();
        }

        public RecentSearch getRecentSearchByWord(String word) {
            AtomicReference<RecentSearch> recentSearch = new AtomicReference<>();
            Thread thread = new Thread(() -> recentSearch.set(mRecentSearchDao.getRecentSearchByWord(word)));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return recentSearch.get();
        }

        public void insertRecentSearch(String wort) {
            new Thread(() -> mRecentSearchDao.insertRecentSearch(new RecentSearch(wort, getCurrentDate()))).start();
        }

        public void deleteRecentSearch(RecentSearch recentSearch) {
            new Thread(() -> mRecentSearchDao.deleteRecentSearch(recentSearch)).start();
        }

        public void overLapRecentSearchReInsert(String word) {
            new Thread(() -> {
                mRecentSearchDao.deleteRecentSearch(mRecentSearchDao.getRecentSearchByWord(word));
                mRecentSearchDao.insertRecentSearch(new RecentSearch(word, getCurrentDate()));
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
