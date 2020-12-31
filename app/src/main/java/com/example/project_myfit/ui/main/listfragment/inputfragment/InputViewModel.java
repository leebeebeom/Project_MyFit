package com.example.project_myfit.ui.main.listfragment.inputfragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

public class InputViewModel extends AndroidViewModel {
    private final SizeDao mSizeDao;
    private int mLargestOrder;

    public InputViewModel(@NonNull Application application) {
        super(application);
        mSizeDao = AppDataBase.getsInstance(application).sizeDao();
    }

    //Insert
    public void insert(Size size) {
        new Thread(() -> mSizeDao.insert(size)).start();
    }

    //Set LargestOrder
    public void setLargestOrder() {
        new Thread(() -> mLargestOrder = mSizeDao.getLargestOrder()).start();
    }
    //Get LargestOrder
    public int getLargestOrder() {
        return mLargestOrder;
    }
}