package com.example.project_myfit.ui.main.listfragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.listfragment.database.ListFolder;
import com.example.project_myfit.ui.main.listfragment.database.ListFolderDao;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

import java.util.List;

public class ListViewModel extends AndroidViewModel {
    private final SizeDao mSizeDao;
    private final ListFolderDao mListFolderDao;
    private int mLargestOrder;


    public ListViewModel(@NonNull Application application) {
        super(application);
        //Dao
        mSizeDao = AppDataBase.getsInstance(application).sizeDao();
        mListFolderDao = AppDataBase.getsInstance(application).listFolderDao();
        //어댑터
    }

    //get Folder List
    public LiveData<List<ListFolder>> getAllFolderList(int folderId) {
        return mListFolderDao.getAllListFolder(folderId);
    }

    //Folder Insert
    public void insertFolder(ListFolder listFolder) {
        new Thread(() -> mListFolderDao.insert(listFolder)).start();
    }

    //Folder Update
    public void updateFolder(ListFolder listFolder) {
        new Thread(() -> mListFolderDao.update(listFolder)).start();
    }

    //Folder Delete
    public void deleteFolder(ListFolder listFolder) {
        new Thread(() -> mListFolderDao.Delete(listFolder)).start();
    }

    public LiveData<List<Size>> getSizeList(int folderId) {
        return mSizeDao.getAllSize(folderId);
    }

    public void setLargestOrder(int folderId) {
        new Thread(() -> mLargestOrder = mListFolderDao.getLargestOrder(folderId)).start();
    }

    public int getLargestOrder() {
        return mLargestOrder;
    }
}