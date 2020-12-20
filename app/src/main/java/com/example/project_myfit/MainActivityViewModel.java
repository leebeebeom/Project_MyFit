package com.example.project_myfit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.database.ChildCategory;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

public class MainActivityViewModel extends ViewModel {
    //MainFragment에서 클릭된 아이템
    private ChildCategory childCategory;
    //ListFragment에서 클릭된 아이템
    private Size size;

    //게터 세터
    public ChildCategory getChildCategory() {
        return childCategory;
    }

    public void setChildCategory(ChildCategory childCategory) {
        this.childCategory = childCategory;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }
}
