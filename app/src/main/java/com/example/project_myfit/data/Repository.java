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
import com.example.project_myfit.util.CommonUtil;

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

        public LiveData<List<Category>> getCategoryLive(boolean isDeleted) {
            //order by orderNumber
            //used in main -> viewPagerAdapter(Sort.categorySort)-> categoryAdapter
            //TODO category 에 delete 타임 넣기
            //used in RecycleBinSearch -> viewPagerAdapter -> recycleBinCategoryAdapter
            return mCategoryDao.getCategoryLive(isDeleted);
        }

        public LiveData<Category> getCategoryLive(long id) {
            //used in list -> actionBarTitle
            return mCategoryDao.getCategoryLive(id);
        }

        public List<Category> getCategoryList(boolean isDeleted) {
            //order by orderNumber
            //used in orderNumberInit
            List<Category> allCategoryList = new ArrayList<>();
            Thread thread = new Thread(() -> allCategoryList.addAll(mCategoryDao.getCategoryList(isDeleted)));
            start(thread);
            return allCategoryList;
        }

        public List<Category> getCategoryList(String parentCategory, boolean isDeleted) {
            //order by orderNumber
            //used in treeView -> sort.categorySort
            List<Category> categoryList = new ArrayList<>();
            Thread thread = new Thread(() -> categoryList.addAll(mCategoryDao.getCategoryList(parentCategory, isDeleted)));
            start(thread);
            return categoryList;
        }

        public LiveData<List<String>> getCategoryNameLive(boolean isDeleted) {
            //order by categoryName
            //used in recycleBin -> autoComplete
            return mCategoryDao.getCategoryNameLive(isDeleted);
        }

        public List<String> getCategoryNameList(String parentCategory, boolean isDeleted) {
            //order by id
            //used in dialogViewModel -> isSameNameCategory
            List<String> categoryNameList = new ArrayList<>();
            Thread thread = new Thread(() -> categoryNameList.addAll(mCategoryDao.getCategoryNameList(parentCategory, isDeleted)));
            start(thread);
            return categoryNameList;
        }

        public Category getCategory(long id, boolean isDeleted) {
            AtomicReference<Category> category = new AtomicReference<>();
            Thread thread = new Thread(() -> category.set(mCategoryDao.getCategory(id, isDeleted)));
            start(thread);
            return category.get();
        }

        public int getCategoryLargestOrderPlus1() {
            AtomicInteger largestOrder = new AtomicInteger();
            Thread thread = new Thread(() -> largestOrder.set(mCategoryDao.getCategoryLargestOrder()));
            start(thread);
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

        public LiveData<List<Folder>> getFolderLive(boolean isDeleted, boolean parentIsDeleted) {
            //order by folderName
            //used in searchFragment
            //TODO folder 에 delete 타임 넣기
            //used in recycleBinSearch
            return mFolderDao.getFolderLive(isDeleted, parentIsDeleted);
        }

        public LiveData<List<Folder>> getFolderLive(long parentId, boolean isDeleted, boolean parentIsDeleted) {
            //order by orderNumber
            //used in listFragment
            return mFolderDao.getFolderLive(parentId, isDeleted, parentIsDeleted);
        }

        public List<Folder> getFolderList(boolean isDeleted, boolean parentIsDeleted) {
            //order by orderNumber
            //used in orderNumberInit
            List<Folder> folderList = new ArrayList<>();
            Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getFolderList(isDeleted, parentIsDeleted)));
            start(thread);
            return folderList;
        }

        public List<Folder> getFolderList(String parentCategory, boolean isDeleted, boolean parentIsDeleted) {
            //order by orderNumber
            //used in listViewModel -> getFolderHistory
            //used in treeViewDialog -> sort.folderSort
            List<Folder> folderList = new ArrayList<>();
            Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getFolderList(parentCategory, isDeleted, parentIsDeleted)));
            start(thread);
            return folderList;
        }

        public List<Folder> getFolderList(long parentId, boolean isDeleted, boolean parentIsDeleted) {
            //order by id
            //used in mainFragment -> selectedCategoryDelete
            //used in selectedItemTreat -> deleteFolder
            List<Folder> folderList = new ArrayList<>();
            Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getFolderList(parentId, isDeleted, parentIsDeleted)));
            start(thread);
            return folderList;
        }

        public LiveData<List<String>> getFolderNameLive(boolean isDeleted, boolean parentIsDeleted) {
            //order by id
            //used in searchView -> sort.string -> autoComplete
            return mFolderDao.getFolderNameLive(isDeleted, parentIsDeleted);
        }

        public List<String> getFolderNameList(long parentId, boolean isDeleted, boolean parentIsDeleted) {
            //order by id
            //used in DialogViewModel -> isSameNameFolder
            List<String> folderNameList = new ArrayList<>();
            Thread thread = new Thread(() -> folderNameList.addAll(mFolderDao.getFolderNameList(parentId, isDeleted, parentIsDeleted)));
            start(thread);
            return folderNameList;
        }

        public List<Long> getFolderParentIdList(boolean isDeleted, boolean parentIsDeleted) {
            //order by id
            //used in recycleBinSearch
            List<Long> folderParentIdList = new ArrayList<>();
            Thread thread = new Thread(() -> folderParentIdList.addAll(mFolderDao.getFolderParentIdList(isDeleted, parentIsDeleted)));
            start(thread);
            return folderParentIdList;
        }

        public List<Long> getFolderParentIdList(String parentCategory, boolean isDeleted, boolean parentIsDeleted) {
            //order by id
            //used in TreeView -> treeCategoryHolder, treeFolderHolder
            //used in mainFragment -> categoryAdapter
            //used in listFragment -> folderAdapter
            List<Long> folderParentIdList = new ArrayList<>();
            Thread thread = new Thread(() -> folderParentIdList.addAll(mFolderDao.getFolderParentIdList(parentCategory, isDeleted, parentIsDeleted)));
            start(thread);
            return folderParentIdList;
        }

        public LiveData<Folder> getSingleFolderLive(long id) {
            return mFolderDao.getSingleFolderLive(id);
        }

        public Folder getFolder(long id, boolean isDeleted, boolean parentIsDeleted) {
            AtomicReference<Folder> folder = new AtomicReference<>();
            Thread thread = new Thread(() -> folder.set(mFolderDao.getFolder(id, isDeleted, parentIsDeleted)));
            start(thread);
            return folder.get();
        }

        public int getFolderLargestOrderPlus1() {
            AtomicInteger largestOrder = new AtomicInteger();
            Thread thread = new Thread(() -> largestOrder.set(mFolderDao.getFolderLargestOrder()));
            start(thread);
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

        public LiveData<List<Size>> getSizeLive(boolean isDeleted, boolean parentIsDeleted) {
            //order by name
            //used in searchFragment
            //TODO size에 delete 타임 넣기
            //used in recycleBinSearch
            return mSizeDao.getSizeLive(isDeleted, parentIsDeleted);
        }

        public LiveData<List<Size>> getSizeLive(long parentId, boolean isDeleted, boolean parentIsDeleted) {
            //order by orderNumber DESC
            //used in listFragment
            return mSizeDao.getSizeLive(parentId, isDeleted, parentIsDeleted);
        }

        public List<Size> getSizeList(boolean isDeleted, boolean parentIsDeleted) {
            //order by orderNumber
            //used in orderNumberInit
            List<Size> sizeList = new ArrayList<>();
            Thread thread = new Thread(() -> sizeList.addAll(mSizeDao.getSizeList(isDeleted, parentIsDeleted)));
            start(thread);
            return sizeList;
        }

        public List<Size> getSizeList(long parentId, boolean isDeleted, boolean parentIsDeleted) {
            //order by id
            //used in mainFragment -> selectCategoryDelete
            //used in selectedItemTreat -> deleteSize
            List<Size> sizeList = new ArrayList<>();
            Thread thread = new Thread(() -> sizeList.addAll(mSizeDao.getSizeList(parentId, isDeleted, parentIsDeleted)));
            start(thread);
            return sizeList;
        }

        public List<Long> getSizeParentIdList(boolean isDeleted, boolean parentIsDeleted) {
            //order by id
            //used in recycleBinSearch
            List<Long> sizeParentIdList = new ArrayList<>();
            Thread thread = new Thread(() -> sizeParentIdList.addAll(mSizeDao.getSizeParentIdList(isDeleted, parentIsDeleted)));
            start(thread);
            return sizeParentIdList;
        }

        public List<Long> getSizeParentIdList(String parentCategory, boolean isDeleted, boolean parentIsDeleted) {
            //order by id
            //used in TreeView -> treeCategoryHolder, treeFolderHolder
            //used in mainFragment -> categoryAdapter
            //used in listFragment -> folderAdapter
            List<Long> sizeParentIdList = new ArrayList<>();
            Thread thread = new Thread(() -> sizeParentIdList.addAll(mSizeDao.getSizeParentIdList(parentCategory, isDeleted, parentIsDeleted)));
            start(thread);
            return sizeParentIdList;
        }

        public LiveData<List<String>> getSizeBrandLive(boolean isDelete, boolean parentIsDeleted) {
            //order by id
            //used in searchView -> sort.string -> autoComplete
            return mSizeDao.getSizeBrandLive(isDelete, parentIsDeleted);
        }

        public LiveData<List<String>> getSizeNameLive(boolean isDeleted, boolean parentIsDeleted) {
            //order by id
            //used in searchView -> sort.string -> autoComplete
            return mSizeDao.getSizeNameLive(isDeleted, parentIsDeleted);
        }

        public List<String> getSizeBrandList(boolean isDeleted, boolean parentIsDeleted) {
            //order by brand
            //used in sizeFragment -> brand -> autoComplete
            List<String> brandList = new ArrayList<>();
            Thread thread = new Thread(() -> brandList.addAll(mSizeDao.getSizeBrandList(isDeleted, parentIsDeleted)));
            start(thread);
            return brandList;
        }

        public Size getSize(long id) {
            AtomicReference<Size> size = new AtomicReference<>();
            Thread thread = new Thread(() -> size.set(mSizeDao.getSize(id)));
            start(thread);
            return size.get();
        }

        public int getSizeLargestOrderPlus1() {
            AtomicInteger largestOrder = new AtomicInteger();
            Thread thread = new Thread(() -> largestOrder.set(mSizeDao.getSizeLargestOrder()));
            start(thread);
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
            //used in recycleBinSearch -> recentSearchAdapter
            return mRecentSearchDao.getRecentSearchLive(type);
        }

        public void recentSearchInsert(String wort, int type) {
            new Thread(() -> mRecentSearchDao.recentSearchInsert(new RecentSearch(CommonUtil.createId(), wort, getCurrentDate(), type))).start();
        }

        public void recentSearchDelete(RecentSearch recentSearch) {
            new Thread(() -> mRecentSearchDao.recentSearchDelete(recentSearch)).start();
        }

        public void overLapRecentSearchReInsert(String word, int type) {
            new Thread(() -> {
                mRecentSearchDao.recentSearchDelete(mRecentSearchDao.getRecentSearch(word));
                mRecentSearchDao.recentSearchInsert(new RecentSearch(CommonUtil.createId(), word, getCurrentDate(), type));
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

    private static void start(@NotNull Thread thread) {
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
