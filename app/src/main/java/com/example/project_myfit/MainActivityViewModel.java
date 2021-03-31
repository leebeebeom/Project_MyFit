package com.example.project_myfit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {
    private Category mCategory;
    private Size mSize;
    private Folder mFolder, mListFragmentFolder;
    private final Repository mRepository;
    private List<Folder> mSelectedFolders, mFolderHistory;
    private List<Size> mSelectedSizes;

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

    public List<Folder> getSelectedFolders() {
        return mSelectedFolders;
    }

    public void setSelectedFolders(List<Folder> mSelectedFolders) {
        this.mSelectedFolders = mSelectedFolders;
    }

    public List<Size> getSelectedSizes() {
        return mSelectedSizes;
    }

    public void setSelectedSizes(List<Size> mSelectedSizes) {
        this.mSelectedSizes = mSelectedSizes;
    }

    public Folder getListFragmentFolder() {
        return mListFragmentFolder;
    }

    public void setListFragmentFolder(Folder mListFragmentFolder) {
        this.mListFragmentFolder = mListFragmentFolder;
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
