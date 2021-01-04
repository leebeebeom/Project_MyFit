package com.example.project_myfit.ui.main.listfragment;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.project_myfit.ui.main.database.AppDataBase;
import com.example.project_myfit.ui.main.listfragment.database.ListFolder;
import com.example.project_myfit.ui.main.listfragment.database.ListFolderDao;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.database.SizeDao;

import java.io.File;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ListViewModel extends AndroidViewModel {
    private final SizeDao mSizeDao;
    private final ListFolderDao mListFolderDao;
    private int mFolderLargestOrder;
    private int mSizeLargestOrder;


    public ListViewModel(@NonNull Application application) {
        super(application);
        //Dao
        mSizeDao = AppDataBase.getsInstance(application).sizeDao();
        mListFolderDao = AppDataBase.getsInstance(application).listFolderDao();
    }

    //get Folder List
    public LiveData<List<ListFolder>> getFolderList(int folderId) {
        return mListFolderDao.getListFolder(folderId);
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

    //Folder Order Update
    public void updateFolderOrder(List<ListFolder> folderList) {
        new Thread(() -> mListFolderDao.updateOrder(folderList)).start();
    }

    //Size insert
    public void insertSize(Size size) {
        new Thread(() -> mSizeDao.insert(size)).start();
    }

    //Size Order Update
    public void updateSizeOrder(List<Size> sizeList) {
        new Thread(() -> mSizeDao.updateOrder(sizeList)).start();
    }

    //Size Delete
    public void deleteSize(Size size) {
        if (size.getImageUri() != null) {
            File file = new File(Uri.parse(size.getImageUri()).getPath());
            if (file.delete()) {
                Log.d(TAG, "deleteSize: 파일 삭제됨");
            } else {
                Log.d(TAG, "deleteSize: 파일 삭제 실패");
            }
        }

        new Thread(() -> mSizeDao.delete(size)).start();

    }

    public LiveData<List<Size>> getSizeList(int folderId) {
        return mSizeDao.getAllSize(folderId);
    }

    public void setFolderLargestOrder(int folderId) {
        new Thread(() -> mFolderLargestOrder = mListFolderDao.getLargestOrder(folderId)).start();
    }

    public void setSizeLargestOrder() {
        new Thread(() -> mSizeLargestOrder = mSizeDao.getLargestOrder()).start();
    }

    public int getLargestOrder() {
        return mFolderLargestOrder;
    }
}