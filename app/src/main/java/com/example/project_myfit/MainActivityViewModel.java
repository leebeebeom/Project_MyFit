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
    private List<Folder> mSelectedFolders;
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

    public Repository getRepository() {
        return mRepository;
    }
}
