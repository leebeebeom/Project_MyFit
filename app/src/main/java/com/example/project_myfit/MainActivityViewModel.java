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
    private List<?> mSelectedItemList;
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

    public List<?> getSelectedItemList() {
        return mSelectedItemList;
    }

    public void setSelectedItemList(List<?> mSelectedItemList) {
        this.mSelectedItemList = mSelectedItemList;
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
