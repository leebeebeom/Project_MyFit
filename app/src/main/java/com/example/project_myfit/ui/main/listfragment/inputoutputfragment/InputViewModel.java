package com.example.project_myfit.ui.main.listfragment.inputoutputfragment;

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
        //Dao
        mSizeDao = AppDataBase.getsInstance(application).sizeDao();
        //get
        largestOrderInit();
    }

    //Insert
    public void insert(Size size) {
        new Thread(() -> mSizeDao.insert(size)).start();
    }
    //Update
    public void update(Size size){
        new Thread(() -> mSizeDao.update(size)).start();
    }

    //Set LargestOrder
    public void largestOrderInit() {
        new Thread(() -> mLargestOrder = mSizeDao.getLargestOrder()).start();
    }


    //Get LargestOrder
    public int getLargestOrder() {
        return mLargestOrder;
    }
}