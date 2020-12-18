package com.example.project_myfit.ui.main.listfragment.inputfragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

public class InputViewModel extends AndroidViewModel {
    private final SizeDao mDao;

    public InputViewModel(@NonNull Application application) {
        super(application);
        mDao = AppDataBase.getsInstance(application).sizeDao();
    }

    //인서트
    public void SizeInsert(Size size) {
        new Thread(() -> mDao.insert(size)).start();
    }
}