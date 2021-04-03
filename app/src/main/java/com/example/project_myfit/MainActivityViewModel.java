package com.example.project_myfit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.Repository;
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
    private List<Folder> mFolderHistory;

    public MainActivityViewModel(@NonNull @NotNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        this.mCategory = category;
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

    public void setFolder(Folder folder) {
        this.mFolder = folder;
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

    public Repository getRepository() {
        return mRepository;
    }

    public void setFolderHistory(List<Folder> folderHistory) {
        this.mFolderHistory = folderHistory;
    }

    public List<Folder> getFolderHistory() {
        return mFolderHistory;
    }
}
