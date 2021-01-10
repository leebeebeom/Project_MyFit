package com.example.project_myfit;

import androidx.lifecycle.ViewModel;

import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;

public class MainActivityViewModel extends ViewModel {
    //From MainFragment Clicked Item
    private Category category;
    //From ListFragment Clicked Item
    private Folder folder;
    //From ListFragment Clicked Item
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

    public Folder getListFolder() {
        return folder;
    }

    public void setListFolder(Folder folder) {
        this.folder = folder;
    }
}
