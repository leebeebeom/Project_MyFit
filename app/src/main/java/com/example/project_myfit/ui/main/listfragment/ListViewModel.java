package com.example.project_myfit.ui.main.listfragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.Repository;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private MainActivityViewModel mActivityModel;
    private Folder mThisFolder;
    private long mFolderId;
    private String mActionBarTitle;
    private List<Folder> mFolderHistory;

    public ListViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    //dao-------------------------------------------------------------------------------------
    public LiveData<List<Folder>> getFolderLive() {
        return mRepository.getFolderLive(mFolderId);
    }

    public List<Folder> getAllFolder() {
        return mRepository.getAllFolder();
    }

    public int getFolderLargestOrder() {
        return mRepository.getFolderLargestOrder();
    }

    public void insertFolder(Folder folder) {
        mRepository.folderInsert(folder);
    }

    public void updateFolder(Folder folder) {
        mRepository.folderUpdate(folder);
    }

    public void updateFolder(List<Folder> folderList) {
        mRepository.folderUpdate(folderList);
    }

    public void deleteFolder(List<Folder> folderList) {
        for (Folder f : folderList) f.setDeleted(true);
        mRepository.folderDelete(folderList);
    }

    //size------------------------------------------------------------------------------------------
    public LiveData<List<Size>> getSizeLive() {
        return mRepository.getSizeLive(mFolderId);
    }

    public void deleteSize(List<Size> sizeList) {
        for (Size s : sizeList) s.setDeleted(true);
        mRepository.sizeDelete(sizeList);
    }

    public void updateSizeList(List<Size> sizeList) {
        mRepository.sizeUpdate(sizeList);
    }

    //category--------------------------------------------------------------------------------------
    public List<Category> getCategoryList() {
        return mRepository.getCategoryList(mActivityModel.getCategory().getParentCategory());
    }
    //----------------------------------------------------------------------------------------------

    public void setThisFolder(MainActivityViewModel activityModel) {
        if (mActivityModel == null) mActivityModel = activityModel;
        if (mThisFolder == null)
            mThisFolder = mActivityModel.getFolder() != null ? mActivityModel.getFolder() : null;
        mFolderId = mThisFolder == null ? mActivityModel.getCategory().getId() :
                mThisFolder.getId();
    }

    public String getActionBarTitle() {
        if (mActionBarTitle == null)
            mActionBarTitle = mThisFolder == null ? mActivityModel.getCategory().getCategory() : mThisFolder.getFolderName();
        return mActionBarTitle;
    }

    public Folder getThisFolder() {
        return mThisFolder;
    }

    public long getFolderId() {
        return mFolderId;
    }

    public long getCurrentTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(date));
    }

    public List<Folder> getFolderHistory() {
        if (mFolderHistory != null) return mFolderHistory;
        List<Folder> allFolderList = getAllFolder();
        List<Folder> folderHistory = new ArrayList<>();
        folderHistory.add(mThisFolder);
        mFolderHistory = getFolderHistory2(allFolderList, folderHistory, mThisFolder);
        Collections.reverse(mFolderHistory);
        return mFolderHistory;
    }

    private List<Folder> getFolderHistory2(List<Folder> allFolderList, List<Folder> folderHistory, Folder thisFolder) {
        for (Folder parentFolder : allFolderList) {
            if (parentFolder.getId() == thisFolder.getFolderId()) {
                folderHistory.add(parentFolder);
                getFolderHistory2(allFolderList, folderHistory, parentFolder);
                break;
            }
        }
        return folderHistory;
    }
}