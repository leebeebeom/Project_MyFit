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
    private final Repository mRepository;
    private Category mCategory;
    private Folder mFolder;
    private Size mSize;
    private List<Folder> mSelectedFolderList;
    private List<Size> mSelectedSizeList;

    public MainActivityViewModel(@NonNull @NotNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    public void searchViewSizeClick(long sizeId) {
        mSize = mRepository.getSize(sizeId);
        mCategory = mRepository.getCategory(mSize.getFolderId());
        mFolder = mRepository.getFolder(mSize.getFolderId());
        if (mCategory == null)
            findCategory(mFolder);
    }

    public void searchViewFolderClick(long folderId) {
        mFolder = mRepository.getFolder(folderId);
        findCategory(mFolder);
    }

    private void findCategory(@NotNull Folder folder) {
        mCategory = mRepository.getCategory(folder.getFolderId());
        if (mCategory == null) {
            Folder parentFolder = mRepository.getFolder(folder.getFolderId());
            findCategory(parentFolder);
        }
    }

    //getter,setter---------------------------------------------------------------------------------
    //TODO 이 밑으로 다 제거
    public Category getCategory() {
        return mCategory;
    }

    public Size getSize() {
        return mSize;
    }

    public void setSize(Size size) {
        this.mSize = size;
    }

    public Folder getFolder() {
        return mFolder;
    }

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
