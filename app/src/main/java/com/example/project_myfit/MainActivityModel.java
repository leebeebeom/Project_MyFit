package com.example.project_myfit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;

import org.jetbrains.annotations.NotNull;

public class MainActivityModel extends AndroidViewModel {
    private final Repository.CategoryRepository mCategoryRepository;
    private final Repository.FolderRepository mFolderRepository;
    private String mParentCategory;

    public MainActivityModel(@NonNull @NotNull Application application) {
        super(application);
        mCategoryRepository = Repository.getCategoryRepository(getApplication());
        mFolderRepository = Repository.getFolderRepository(getApplication());
    }

    public long getCategoryId(long parentId) {
        Category category = mCategoryRepository.getCategory(parentId);
        if (category != null) {
            mParentCategory = category.getParentCategory();
            return category.getId();
        } else return findCategoryId(parentId);
    }

    private long findCategoryId(long parentId) {
        Folder folder = mFolderRepository.getFolder(parentId);
        Category category = mCategoryRepository.getCategory(folder.getParentId());
        if (category != null) {
            mParentCategory = category.getParentCategory();
            return category.getId();
        } else return findCategoryId(folder.getParentId());
    }

    public long getParentFolderId(long parentId) {
        Folder folder = mFolderRepository.getFolder(parentId);
        if (folder != null) return folder.getId();
        else return 0;
    }

    public String getParentCategory() {
        return mParentCategory;
    }

    public String getSizeParentCategory(long parentId) {
        Category category = mCategoryRepository.getCategory(parentId);
        if (category != null) return category.getParentCategory();
        else return mFolderRepository.getFolder(parentId).getParentCategory();
    }
}
