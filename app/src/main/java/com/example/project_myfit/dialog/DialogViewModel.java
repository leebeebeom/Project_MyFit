package com.example.project_myfit.dialog;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavBackStackEntry;

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DialogViewModel extends AndroidViewModel {
    private final Repository.CategoryRepository mCategoryRepository;
    private final Repository.FolderRepository mFolderRepository;
    private MutableLiveData<NavBackStackEntry> mBackStackEntryLive;
    private String mParentName;
    private List<Folder> mSelectedFolderList;
    private List<Size> mSelectedSizeList;
    private List<Folder> mFolderHistory;
    private Category mAddedCategory;
    private Folder mAddedFolder;
    private boolean mIsOrderNumberInit;

    public DialogViewModel(@NonNull @NotNull Application application) {
        super(application);
        mCategoryRepository = Repository.getCategoryRepository(application);
        mFolderRepository = Repository.getFolderRepository(application);
    }

    public MutableLiveData<NavBackStackEntry> getBackStackEntryLive() {
        if (mBackStackEntryLive == null)
            return mBackStackEntryLive = new MutableLiveData<>();
        else return mBackStackEntryLive;
    }

    public boolean isSameNameCategory(String categoryName, String parentCategory) {
        boolean isSameName = false;
        List<String> categoryNameList = mCategoryRepository.getCategoryNameList(parentCategory, false);
        for (String name : categoryNameList)
            if (name.equals(categoryName)) {
                isSameName = true;
                break;
            }
        return isSameName;
    }

    public boolean isSameNameFolder(String folderName, long parentId) {
        boolean isSameName = false;
        List<String> folderNameList = mFolderRepository.getFolderNameList(parentId, false, false);
        for (String name : folderNameList)
            if (name.equals(folderName)) {
                isSameName = true;
                break;
            }
        return isSameName;
    }

    public void categoryInsert(String categoryName, String parentCategory) {
        mAddedCategory = new Category(getCurrentTime(), categoryName, parentCategory, mCategoryRepository.getCategoryLargestOrderPlus1());
        mCategoryRepository.categoryInsert(mAddedCategory);
    }

    public void folderInsert(@NotNull String folderName, long parentId, String parentCategory) {
        mAddedFolder = new Folder(getCurrentTime(), folderName.trim(), parentId, mFolderRepository.getFolderLargestOrderPlus1(), parentCategory);
        mFolderRepository.folderInsert(mAddedFolder);
        parentSetDummy(parentId);
    }

    private void parentSetDummy(long parentId) {
        Category parentCategory = mCategoryRepository.getCategory(parentId);
        if (parentCategory != null) {
            parentCategory.setDummy(!parentCategory.getDummy());
            mCategoryRepository.categoryUpdate(parentCategory);
        } else {
            Folder parentFolder = mFolderRepository.getFolder(parentId);
            parentFolder.setDummy(!parentFolder.getDummy());
            mFolderRepository.folderUpdate(parentFolder);
        }
    }

    public void categoryNameEdit(@NotNull Category category, String categoryName, boolean isParentName) {
        if (isParentName) mParentName = categoryName;
        category.setCategoryName(categoryName);
        mCategoryRepository.categoryUpdate(category);
    }

    public void folderNameEdit(@NotNull Folder folder, String folderName, boolean isParentName) {
        if (isParentName) mParentName = folderName;
        folder.setFolderName(folderName);
        mFolderRepository.folderUpdate(folder);
    }

    public void sameNameCategoryEdit(long categoryId, String categoryName, boolean isParentName) {
        if (isParentName) mParentName = categoryName;
        Category category = mCategoryRepository.getCategory(categoryId);
        category.setCategoryName(categoryName);
        mCategoryRepository.categoryUpdate(category);
    }

    public void sameNameFolderEdit(long parentId, String folderName, boolean isParentName) {
        if (isParentName) mParentName = folderName;
        Folder folder = mFolderRepository.getFolder(parentId);
        folder.setFolderName(folderName);
        mFolderRepository.folderUpdate(folder);
    }

    public void sizeDelete(long sizeId) {
        Size size = Repository.getSizeRepository(getApplication()).getSize(sizeId);
        size.setIsDeleted(true);
        Repository.getSizeRepository(getApplication()).sizeUpdate(size);
        parentSetDummy(size.getParentId());
    }

    public Category getCategory(long categoryId) {
        return mCategoryRepository.getCategory(categoryId);
    }

    public Folder getFolder(long folderId) {
        return mFolderRepository.getFolder(folderId);
    }

    public String getParentName() {
        return mParentName;
    }

    public long getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(System.currentTimeMillis())));
    }

    public void forTreeView(List<Folder> selectedFolderList, List<Size> selectedSizeList, List<Folder> folderHistory) {
        mSelectedFolderList = selectedFolderList;
        mSelectedSizeList = selectedSizeList;
        mFolderHistory = folderHistory;
    }

    public List<Folder> getSelectedFolderList() {
        return mSelectedFolderList;
    }

    public List<Size> getSelectedSizeList() {
        return mSelectedSizeList;
    }

    public List<Folder> getFolderHistory() {
        return mFolderHistory;
    }

    public Category getAddedCategory() {
        return mAddedCategory;
    }

    public Folder getAddedFolder() {
        return mAddedFolder;
    }

    public void orderNumberInit(){
        if (!mIsOrderNumberInit){
            List<Category> categoryList = mCategoryRepository.getCategoryList(false);
            for (int i = 0; i < categoryList.size(); i++)
                categoryList.get(i).setOrderNumber(i);
            List<Folder> folderList = mFolderRepository.getFolderList(false, false);
            for (int i = 0; i < folderList.size(); i++)
                folderList.get(i).setOrderNumber(i);
            List<Size> sizeList = Repository.getSizeRepository(getApplication()).getSizeList(false, false);
            for (int i = 0; i < sizeList.size(); i++)
                sizeList.get(i).setOrderNumber(i);
            mCategoryRepository.categoryUpdate(categoryList);
            mFolderRepository.folderUpdate(folderList);
            Repository.getSizeRepository(getApplication()).sizeUpdate(sizeList);
            mIsOrderNumberInit = true;
        }
    }

    public void recentSearchDeleteAll() {
        Repository.getRecentSearchRepository(getApplication()).deleteAllRecentSearch();
    }
}
