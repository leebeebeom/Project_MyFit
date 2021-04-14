package com.example.project_myfit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {
    private final Repository.CategoryRepository mCategoryRepository;
    private final Repository.FolderRepository mFolderRepository;
    private final Repository.SizeRepository mSizeRepository;
    private Category mCategory;
    private Folder mFolder;
    private Size mSize;
    private List<Folder> mSelectedFolderList;
    private List<Size> mSelectedSizeList;

    public MainActivityViewModel(@NonNull @NotNull Application application) {
        super(application);
        mCategoryRepository = Repository.getCategoryRepository(application);
        mFolderRepository = Repository.getFolderRepository(application);
        mSizeRepository = Repository.getSizeRepository(application);
    }

//    public void searchViewSizeClick(long sizeId) {
//        mSize = mSizeRepository.getSize(sizeId);
//        mCategory = mCategoryRepository.getCategory(mSize.getParentId());
//        mFolder = mFolderRepository.getFolder(mSize.getParentId());
//        if (mCategory == null)
//            findCategory(mFolder);
//    }
//
//    public void searchViewFolderClick(long folderId) {
//        mFolder = mFolderRepository.getFolder(folderId);
//        findCategory(mFolder);
//    }
//
//    private void findCategory(@NotNull Folder folder) {
//        mCategory = mCategoryRepository.getCategory(folder.getParentId());
//        if (mCategory == null) {
//            Folder parentFolder = mFolderRepository.getFolder(folder.getParentId());
//            findCategory(parentFolder);
//        }
//    }

    //getter,setter---------------------------------------------------------------------------------
    public List<Folder> getSelectedFolderList() {
        return mSelectedFolderList;
    }

    public void setSelectedFolderList(List<Folder> mSelectedFolders) {
        this.mSelectedFolderList = mSelectedFolders;
    }

    public List<Size> getSelectedSizeList() {
        return mSelectedSizeList;
    }

    public void setSelectedSizeList(List<Size> mSelectedSizes) {
        this.mSelectedSizeList = mSelectedSizes;
    }
    //----------------------------------------------------------------------------------------------
}
