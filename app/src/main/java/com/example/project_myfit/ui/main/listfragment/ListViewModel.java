package com.example.project_myfit.ui.main.listfragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

import java.util.List;

public class ListViewModel extends AndroidViewModel {
    private final SizeDao mDao;
    private final ListFragmentAdapter mAdapter;


    public ListViewModel(@NonNull Application application) {
        super(application);
        //Dao
        mDao = AppDataBase.getsInstance(application).sizeDao();
        //어댑터
        mAdapter = new ListFragmentAdapter();
    }

    public ListFragmentAdapter setAdapterItem(List<Size> sizes) {
        mAdapter.setItems(sizes);
        return mAdapter;
    }

    public LiveData<List<Size>> getSizeList(int folderId) {
        return mDao.getAllSizeByFolderId(folderId);
    }

    public ListFragmentAdapter getAdapter() {
        return mAdapter;
    }
}