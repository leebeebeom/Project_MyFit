package com.example.project_myfit.ui.main.listfragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.listfragment.adapter.ListFragmentAdapter;
import com.example.project_myfit.ui.main.listfragment.database.ListFolder;
import com.example.project_myfit.ui.main.listfragment.database.ListFolderDao;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

import java.util.List;

public class ListViewModel extends AndroidViewModel {
    private final SizeDao mSizeDao;
    private final ListFolderDao mListFolderDao;
    private final ListFragmentAdapter mAdapter;


    public ListViewModel(@NonNull Application application) {
        super(application);
        //Dao
        mSizeDao = AppDataBase.getsInstance(application).sizeDao();
        mListFolderDao = AppDataBase.getsInstance(application).listFolderDao();
        //어댑터
        mAdapter = new ListFragmentAdapter();
    }

    //get Folder List
    public LiveData<List<ListFolder>> getFolderList() {
        return mListFolderDao.getAllListFolder();
    }

    //Folder Insert
    public void insertFolder(ListFolder listFolder) {
        new Thread(() -> mListFolderDao.insert(listFolder)).start();

    }

    public LiveData<List<Size>> getSizeList(int folderId) {
        return mSizeDao.getAllSizeByFolderIdLive(folderId);
    }

    public ListFragmentAdapter getAdapter() {
        return mAdapter;
    }
}