package com.example.project_myfit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {
    private Category category;
    private Size size;
    private Folder folder;

    private List<Folder> selectedFolder;

    private final Repository mRepository;
    private TreeNode rootTreeNode;

    public MainActivityViewModel(@NonNull @NotNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public void setSelectedFolder(List<Folder> selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public List<Folder> getSelectedFolder() {
        return selectedFolder;
    }

    public List<Folder> getAllFolder() {
        return mRepository.getAllFolder();
    }

    public List<Category> getCategoryList() {
        return mRepository.getCategoryList(category.getParentCategory());
    }

    public void setRootTreeNode(TreeNode rootTreeNode) {
        this.rootTreeNode = rootTreeNode;
    }

    public TreeNode getRootTreeNode() {
        return rootTreeNode;
    }
}
