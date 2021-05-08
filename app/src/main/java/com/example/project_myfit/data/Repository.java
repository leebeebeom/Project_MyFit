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
    private final Context mContext;
    private CategoryRepository mCategoryRepository;
    private FolderRepository mFolderRepository;
    private SizeRepository mSizeRepository;
    private RecentSearchRepository mRecentSearchRepository;

    public Repository(Context context) {
        this.mContext = context;
    }

    public CategoryRepository getCategoryRepository() {
        if (mCategoryRepository == null) mCategoryRepository = new CategoryRepository(mContext);
        return mCategoryRepository;
    }

    public FolderRepository getFolderRepository() {
        if (mFolderRepository == null) mFolderRepository = new FolderRepository(mContext);
        return mFolderRepository;
    }

    public SizeRepository getSizeRepository() {
        if (mSizeRepository == null) mSizeRepository = new SizeRepository(mContext);
        return mSizeRepository;
    }

    public RecentSearchRepository getRecentSearchRepository() {
        if (mRecentSearchRepository == null)
            mRecentSearchRepository = new RecentSearchRepository(mContext);
        return mRecentSearchRepository;
    }

    public static class CategoryRepository {
        private final CategoryDao mCategoryDao;

        private CategoryRepository(Context context) {
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

        public List<Category> getCategoryList(int parentCategoryIndex, boolean isDeleted) {
            //order by orderNumber
            //used in treeView -> sort.categorySort
            List<Category> categoryList = new ArrayList<>();
            Thread thread = new Thread(() -> categoryList.addAll(mCategoryDao.getCategoryList(parentCategoryIndex, isDeleted)));
            start(thread);
            return categoryList;
        }

        public LiveData<List<String>> getCategoryNameLive(boolean isDeleted) {
            //order by categoryName
            //used in recycleBin -> autoComplete
            return mCategoryDao.getCategoryNameLive(isDeleted);
        }

        public List<String> getCategoryNameList(int parentCategoryIndex, boolean isDeleted) {
            //order by id
            //used in dialogViewModel -> isSameNameCategory
            List<String> categoryNameList = new ArrayList<>();
            Thread thread = new Thread(() -> categoryNameList.addAll(mCategoryDao.getCategoryNameList(parentCategoryIndex, isDeleted)));
            start(thread);
            return categoryNameList;
        }

        public Category getCategory(long id, boolean isDeleted) {
            AtomicReference<Category> category = new AtomicReference<>();
            Thread thread = new Thread(() -> category.set(mCategoryDao.getCategory(id, isDeleted)));
            start(thread);
            return category.get();
        }

        public Category getCategory(String categoryName, int parentCategoryIndex, boolean isDeleted) {
            AtomicReference<Category> category = new AtomicReference<>();
            Thread thread = new Thread(() -> category.set(mCategoryDao.getCategory(categoryName, parentCategoryIndex, isDeleted)));
            start(thread);
            return category.get();
        }

        public int getCategoryLargestOrderPlus1() {
            AtomicInteger largestOrder = new AtomicInteger();
            Thread thread = new Thread(() -> largestOrder.set(mCategoryDao.getCategoryLargestOrder()));
            start(thread);
            return largestOrder.get() + 1;
        }

        public void insertCategory(String categoryName, int parentCategoryIndex) {
            new Thread(() -> {
                Category category = new Category(CommonUtil.createId(), categoryName, parentCategoryIndex, mCategoryDao.getCategoryLargestOrder() + 1);
                mCategoryDao.insertCategory(category);
            }).start();
        }

        public void insertCategory(List<Category> categoryList) {
            new Thread(() -> mCategoryDao.insertCategory(categoryList)).start();
        }

        public void updateCategory(@NotNull List<Category> categoryList) {
            new Thread(() -> mCategoryDao.updateCategory(categoryList)).start();
        }

        public void updateCategory(Category category) {
            new Thread(() -> mCategoryDao.updateCategory(category)).start();
        }
    }

    public static class FolderRepository {
        private final FolderDao mFolderDao;

        private FolderRepository(Context context) {
            mFolderDao = AppDataBase.getsInstance(context).folderDao();
        }

        public LiveData<List<Folder>> getFolderLive(boolean isDeleted, boolean isParentDeleted) {
            //order by folderName
            //used in searchFragment
            //TODO folder 에 delete 타임 넣기
            //used in recycleBinSearch
            return mFolderDao.getFolderLive(isDeleted, isParentDeleted);
        }

        public LiveData<List<Folder>> getFolderLive(long parentId, boolean isDeleted, boolean isParentDeleted) {
            //order by orderNumber
            //used in listFragment
            return mFolderDao.getFolderLive(parentId, isDeleted, isParentDeleted);
        }

        public List<Folder> getFolderList(boolean isDeleted, boolean isParentDeleted) {
            //order by orderNumber
            //used in orderNumberInit
            List<Folder> folderList = new ArrayList<>();
            Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getFolderList(isDeleted, isParentDeleted)));
            start(thread);
            return folderList;
        }

        public List<Folder> getFolderList(int parentCategoryIndex, boolean isDeleted, boolean isParentDeleted) {
            //order by orderNumber
            //used in listViewModel -> getFolderPath
            //used in treeViewDialog -> sort.folderSort
            List<Folder> folderList = new ArrayList<>();
            Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getFolderList(parentCategoryIndex, isDeleted, isParentDeleted)));
            start(thread);
            return folderList;
        }

        public List<Folder> getFolderList(long parentId, boolean isDeleted, boolean isParentDeleted) {
            //order by id
            //used in mainFragment -> selectedCategoryDelete
            //used in selectedItemTreat -> deleteFolder
            List<Folder> folderList = new ArrayList<>();
            Thread thread = new Thread(() -> folderList.addAll(mFolderDao.getFolderList(parentId, isDeleted, isParentDeleted)));
            start(thread);
            return folderList;
        }

        public LiveData<List<String>> getFolderNameLive(boolean isDeleted, boolean isParentDeleted) {
            //order by id
            //used in searchView -> sort.string -> autoComplete
            return mFolderDao.getFolderNameLive(isDeleted, isParentDeleted);
        }

        public List<String> getFolderNameList(long parentId, boolean isDeleted, boolean isParentDeleted) {
            //order by id
            //used in DialogViewModel -> isSameNameFolder
            List<String> folderNameList = new ArrayList<>();
            Thread thread = new Thread(() -> folderNameList.addAll(mFolderDao.getFolderNameList(parentId, isDeleted, isParentDeleted)));
            start(thread);
            return folderNameList;
        }

        public List<Long> getFolderParentIdList(boolean isDeleted, boolean isParentDeleted) {
            //order by id
            //used in recycleBinSearch
            List<Long> folderParentIdList = new ArrayList<>();
            Thread thread = new Thread(() -> folderParentIdList.addAll(mFolderDao.getFolderParentIdList(isDeleted, isParentDeleted)));
            start(thread);
            return folderParentIdList;
        }

        public List<Long> getFolderParentIdList(int parentCategoryIndex, boolean isDeleted, boolean isParentDeleted) {
            //order by id
            //used in TreeView -> treeCategoryHolder, treeFolderHolder
            //used in mainFragment -> categoryAdapter
            //used in listFragment -> folderAdapter
            List<Long> folderParentIdList = new ArrayList<>();
            Thread thread = new Thread(() -> folderParentIdList.addAll(mFolderDao.getFolderParentIdList(parentCategoryIndex, isDeleted, isParentDeleted)));
            start(thread);
            return folderParentIdList;
        }

        public LiveData<Folder> getSingleFolderLive(long id) {
            return mFolderDao.getSingleFolderLive(id);
        }

        public Folder getFolder(long id, boolean isDeleted, boolean isParentDeleted) {
            AtomicReference<Folder> folder = new AtomicReference<>();
            Thread thread = new Thread(() -> folder.set(mFolderDao.getFolder(id, isDeleted, isParentDeleted)));
            start(thread);
            return folder.get();
        }

        public int getFolderLargestOrderPlus1() {
            AtomicInteger largestOrder = new AtomicInteger();
            Thread thread = new Thread(() -> largestOrder.set(mFolderDao.getFolderLargestOrder()));
            start(thread);
            return largestOrder.get() + 1;
        }

        public void insertFolder(String folderName, long parentId, int parentCategoryIndex) {
            new Thread(() -> {
                Folder folder = new Folder(CommonUtil.createId(), folderName, parentId, mFolderDao.getFolderLargestOrder() + 1, parentCategoryIndex);
                mFolderDao.insertFolder(folder);
            }).start();
        }

        public void updateFolder(Folder folder) {
            new Thread(() -> mFolderDao.updateFolder(folder)).start();
        }

        public void updateFolder(@NotNull List<Folder> folderList) {
            new Thread(() -> mFolderDao.updateFolder(folderList)).start();
        }
    }

    public static class SizeRepository {
        private final SizeDao mSizeDao;

        private SizeRepository(Context context) {
            mSizeDao = AppDataBase.getsInstance(context).sizeDao();
        }

        public LiveData<List<Size>> getSizeLive(boolean isDeleted, boolean isParentDeleted) {
            //order by name
            //used in searchFragment
            //TODO size에 delete 타임 넣기
            //used in recycleBinSearch
            return mSizeDao.getSizeLive(isDeleted, isParentDeleted);
        }

        public LiveData<List<Size>> getSizeLive(long parentId, boolean isDeleted, boolean isParentDeleted) {
            //order by orderNumber DESC
            //used in listFragment
            return mSizeDao.getSizeLive(parentId, isDeleted, isParentDeleted);
        }

        public List<Size> getSizeList(boolean isDeleted, boolean isParentDeleted) {
            //order by orderNumber
            //used in orderNumberInit
            List<Size> sizeList = new ArrayList<>();
            Thread thread = new Thread(() -> sizeList.addAll(mSizeDao.getSizeList(isDeleted, isParentDeleted)));
            start(thread);
            return sizeList;
        }

        public List<Size> getSizeList(long parentId, boolean isDeleted, boolean isParentDeleted) {
            //order by id
            //used in mainFragment -> selectCategoryDelete
            //used in selectedItemTreat -> deleteSize
            List<Size> sizeList = new ArrayList<>();
            Thread thread = new Thread(() -> sizeList.addAll(mSizeDao.getSizeList(parentId, isDeleted, isParentDeleted)));
            start(thread);
            return sizeList;
        }

        public List<Long> getSizeParentIdList(boolean isDeleted, boolean isParentDeleted) {
            //order by id
            //used in recycleBinSearch
            List<Long> sizeParentIdList = new ArrayList<>();
            Thread thread = new Thread(() -> sizeParentIdList.addAll(mSizeDao.getSizeParentIdList(isDeleted, isParentDeleted)));
            start(thread);
            return sizeParentIdList;
        }

        public List<Long> getSizeParentIdList(int parentCategoryIndex, boolean isDeleted, boolean isParentDeleted) {
            //order by id
            //used in TreeView -> treeCategoryHolder, treeFolderHolder
            //used in mainFragment -> categoryAdapter
            //used in listFragment -> folderAdapter
            List<Long> sizeParentIdList = new ArrayList<>();
            Thread thread = new Thread(() -> sizeParentIdList.addAll(mSizeDao.getSizeParentIdList(parentCategoryIndex, isDeleted, isParentDeleted)));
            start(thread);
            return sizeParentIdList;
        }

        public LiveData<List<String>> getSizeBrandLive(boolean isDelete, boolean isParentDeleted) {
            //order by id
            //used in searchView -> sort.string -> autoComplete
            return mSizeDao.getSizeBrandLive(isDelete, isParentDeleted);
        }

        public LiveData<List<String>> getSizeNameLive(boolean isDeleted, boolean isParentDeleted) {
            //order by id
            //used in searchView -> sort.string -> autoComplete
            return mSizeDao.getSizeNameLive(isDeleted, isParentDeleted);
        }

        public List<String> getSizeBrandList(boolean isDeleted, boolean isParentDeleted) {
            //order by brand
            //used in sizeFragment -> brand -> autoComplete
            List<String> brandList = new ArrayList<>();
            Thread thread = new Thread(() -> brandList.addAll(mSizeDao.getSizeBrandList(isDeleted, isParentDeleted)));
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

        public void insertSize(Size size) {
            new Thread(() -> mSizeDao.insertSize(size)).start();
        }

        public void updateSize(Size size) {
            new Thread(() -> mSizeDao.updateSize(size)).start();
        }

        public void updateSize(List<Size> sizeList) {
            new Thread(() -> mSizeDao.updateSize(sizeList)).start();
        }
    }

    public static class RecentSearchRepository {
        private final RecentSearchDao mRecentSearchDao;

        private RecentSearchRepository(Context context) {
            mRecentSearchDao = AppDataBase.getsInstance(context).recentSearchDao();
        }

        public LiveData<List<RecentSearch>> getRecentSearchLive(int type) {
            //order by id
            //used in searchFragment -> recentSearchAdapter
            //used in recycleBinSearch -> recentSearchAdapter
            return mRecentSearchDao.getRecentSearchLive(type);
        }

        public void insertRecentSearch(String word, int type) {
            new Thread(() -> mRecentSearchDao.insertRecentSearch(new RecentSearch(CommonUtil.createId(), word, getCurrentDate(), type))).start();
        }

        public List<String> getRecentSearchStringList() {
            List<String> recentSearchStringList = new ArrayList<>();
            Thread thread = new Thread(() -> recentSearchStringList.addAll(mRecentSearchDao.getRecentSearchStringList()));
            start(thread);
            return recentSearchStringList;
        }

        public RecentSearch getRecentSearch(String word){
            AtomicReference<RecentSearch> recentSearch = new AtomicReference<>();
            Thread thread = new Thread(() -> recentSearch.set(mRecentSearchDao.getRecentSearch(word)));
            start(thread);
            return recentSearch.get();
        }

        public void deleteRecentSearch(RecentSearch recentSearch) {
            new Thread(() -> mRecentSearchDao.deleteRecentSearch(recentSearch)).start();
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
