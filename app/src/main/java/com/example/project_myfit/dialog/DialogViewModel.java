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
import com.example.project_myfit.util.CommonUtil;
import com.example.project_myfit.util.DummyUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DialogViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private MutableLiveData<NavBackStackEntry> mBackStackEntryLive;
    private String mParentName;
    private List<Folder> mSelectedFolderList;
    private List<Size> mSelectedSizeList;
    private List<Folder> mFolderPath;
    private Category mAddedCategory;
    private Folder mAddedFolder;
    private DummyUtil mDummyUtil;
    private boolean mIsOrderNumberInit;

    public DialogViewModel(@NonNull @NotNull Application application) {
        super(application);
        this.mRepository = new Repository(getApplication());
    }

    public MutableLiveData<NavBackStackEntry> getBackStackEntryLive() {
        if (mBackStackEntryLive == null)
            return mBackStackEntryLive = new MutableLiveData<>();
        else return mBackStackEntryLive;
    }

    public boolean isSameNameCategory(String categoryName, String parentCategory) {
        boolean isSameName = false;
        List<String> categoryNameList = mRepository.getCategoryRepository().getCategoryNameList(parentCategory, false);
        for (String name : categoryNameList)
            if (name.equals(categoryName)) {
                isSameName = true;
                break;
            }
        return isSameName;
    }

    public boolean isSameNameFolder(String folderName, long parentId) {
        boolean isSameName = false;
        List<String> folderNameList = mRepository.getFolderRepository().getFolderNameList(parentId, false, false);
        for (String name : folderNameList)
            if (name.equals(folderName)) {
                isSameName = true;
                break;
            }
        return isSameName;
    }

    public void insertCategory(String categoryName, String parentCategory) {
        mAddedCategory = new Category(CommonUtil.createId(), categoryName, parentCategory, mRepository.getCategoryRepository().getCategoryLargestOrderPlus1());
        mRepository.getCategoryRepository().insertCategory(mAddedCategory);
    }

    public void insertFolder(@NotNull String folderName, long parentId, String parentCategory) {
        mAddedFolder = new Folder(CommonUtil.createId(), folderName.trim(), parentId, mRepository.getFolderRepository().getFolderLargestOrderPlus1(), parentCategory);
        mRepository.getFolderRepository().insertFolder(mAddedFolder);
        if (mDummyUtil == null) mDummyUtil = new DummyUtil(getApplication());
        mDummyUtil.setDummy(parentId);
    }

    public void editCategoryName(@NotNull Category category, String categoryName, boolean isParentName) {
        if (isParentName) mParentName = categoryName;
        category.setCategoryName(categoryName);
        mRepository.getCategoryRepository().updateCategory(category);
    }

    public void editFolderName(@NotNull Folder folder, String folderName, boolean isParentName) {
        if (isParentName) mParentName = folderName;
        folder.setFolderName(folderName);
        mRepository.getFolderRepository().updateFolder(folder);
    }

    public void deleteSize(long sizeId) {
        Size size = mRepository.getSizeRepository().getSize(sizeId);
        size.setIsDeleted(true);
        mRepository.getSizeRepository().updateSize(size);
        if (mDummyUtil == null) mDummyUtil = new DummyUtil(getApplication());
        mDummyUtil.setDummy(size.getParentId());
    }

    public Category getCategory(long categoryId) {
        return mRepository.getCategoryRepository().getCategory(categoryId, false);
    }

    public Folder getFolder(long folderId) {
        return mRepository.getFolderRepository().getFolder(folderId, false, false);
    }

    public String getParentName() {
        return mParentName;
    }

    public void setTreeViewResources(List<Folder> selectedFolderList, List<Size> selectedSizeList, List<Folder> folderHistory) {
        mSelectedFolderList = selectedFolderList;
        mSelectedSizeList = selectedSizeList;
        mFolderPath = folderHistory;
    }

    public List<Folder> getSelectedFolderList() {
        return mSelectedFolderList;
    }

    public List<Size> getSelectedSizeList() {
        return mSelectedSizeList;
    }

    public List<Folder> getFolderHistory() {
        return mFolderPath;
    }

    public Category getAddedCategory() {
        return mAddedCategory;
    }

    public Folder getAddedFolder() {
        return mAddedFolder;
    }

    public void orderNumberInit(){
        if (!mIsOrderNumberInit) {
            List<Category> categoryList = mRepository.getCategoryRepository().getCategoryList(false);
            for (int i = 0; i < categoryList.size(); i++)
                categoryList.get(i).setOrderNumber(i);
            List<Folder> folderList = mRepository.getFolderRepository().getFolderList(false, false);
            for (int i = 0; i < folderList.size(); i++)
                folderList.get(i).setOrderNumber(i);
            List<Size> sizeList = mRepository.getSizeRepository().getSizeList(false, false);
            for (int i = 0; i < sizeList.size(); i++)
                sizeList.get(i).setOrderNumber(i);
            mRepository.getCategoryRepository().updateCategory(categoryList);
            mRepository.getFolderRepository().updateFolder(folderList);
            mRepository.getSizeRepository().updateSize(sizeList);
            mIsOrderNumberInit = true;
        }
    }

    public void recentSearchDeleteAll() {
        mRepository.getRecentSearchRepository().deleteAllRecentSearch();
    }
}
