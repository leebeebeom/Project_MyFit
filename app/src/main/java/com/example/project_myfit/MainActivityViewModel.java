package com.example.project_myfit;

import androidx.lifecycle.ViewModel;

import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {
    private Category category;
    private final List<Folder> folderList = new ArrayList<>();
    private Size size;

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

    public List<Folder> getFolderList() {
        return folderList;
    }

    public void setListFolder(Folder folder) {
        folderList.add(folder);
    }
}
