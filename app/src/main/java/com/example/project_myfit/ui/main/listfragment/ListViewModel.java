package com.example.project_myfit.ui.main.listfragment;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_myfit.Repository;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderCategory;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderFolder;
import com.unnamed.b.atv.model.TreeNode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class ListViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private Folder mThisFolder;
    private long mFolderId;
    private List<Folder> mFolderHistory;
    private List<Folder> mSelectedItemFolder;
    private List<Size> mSelectedItemSize;
    private TreeNode mAddNode;
    private TreeHolderCategory.CategoryTreeHolder mCategoryAddValue;
    private TreeHolderFolder.FolderTreeHolder mFolderAddValue;
    private HashSet<Integer> mSelectedPositionFolder, mSelectedPositionSize;
    private final MutableLiveData<Integer> mSelectedAmount;


    public ListViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mSelectedAmount = new MutableLiveData<>();
    }

    //folder----------------------------------------------------------------------------------------
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

    public void selectedItemMove(long folderId) {
        for (Folder f : mSelectedItemFolder) f.setFolderId(folderId);
        for (Size s : mSelectedItemSize) s.setFolderId(folderId);
        updateFolder(mSelectedItemFolder);
        updateSizeList(mSelectedItemSize);
    }

    public void selectedItemDelete() {
        deleteFolder(mSelectedItemFolder);
        deleteSize(mSelectedItemSize);
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

    public void setThisFolder(Folder thisFolder, long folderId) {
        if (thisFolder != null) {
            mThisFolder = thisFolder;
        }
        mFolderId = folderId;
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

    //선택된 아이템---------------------------------------------------------------------------------
    public void selectedItemListInit() {
        mSelectedItemFolder = new ArrayList<>();
        mSelectedItemSize = new ArrayList<>();
    }

    public List<Size> getSelectedItemSize() {
        return mSelectedItemSize;
    }

    public List<Folder> getSelectedItemFolder() {
        return mSelectedItemFolder;
    }

    public void sizeSelected(Size size, boolean isChecked) {
        if (isChecked) mSelectedItemSize.add(size);
        else mSelectedItemSize.remove(size);
        setSelectedAmount();
    }

    public void folderSelected(Folder folder, boolean isChecked) {
        if (isChecked) mSelectedItemFolder.add(folder);
        else {
            Toast.makeText(getApplication(), mSelectedItemFolder.contains(folder) + "", Toast.LENGTH_SHORT).show();
            mSelectedItemFolder.remove(folder);
        }
        setSelectedAmount();
    }

    public void selectAllClick(boolean isChecked, List<Folder> folderCurrentItem, List<Size> sizeCurrentItem) {
        mSelectedItemSize.clear();
        mSelectedItemFolder.clear();
        if (isChecked) {
            mSelectedItemFolder.addAll(folderCurrentItem);
            mSelectedItemSize.addAll(sizeCurrentItem);
        }
        setSelectedAmount();
    }
    //----------------------------------------------------------------------------------------------


    public void nodeAddClick(TreeNode node, TreeHolderCategory.CategoryTreeHolder value) {
        mAddNode = node;
        mCategoryAddValue = value;
        mFolderAddValue = null;
    }

    public void nodeAddClick(TreeNode node, TreeHolderFolder.FolderTreeHolder value) {
        mAddNode = node;
        mFolderAddValue = value;
        mCategoryAddValue = null;
    }

    public TreeNode getAddNode() {
        return mAddNode;
    }

    public TreeHolderCategory.CategoryTreeHolder getCategoryAddValue() {
        return mCategoryAddValue;
    }

    public TreeHolderFolder.FolderTreeHolder getFolderAddValue() {
        return mFolderAddValue;
    }

    public HashSet<Integer> getSelectedPositionFolder() {
        return mSelectedPositionFolder;
    }

    public void setSelectedPositionFolder(HashSet<Integer> mSelectedPositionFolder) {
        this.mSelectedPositionFolder = mSelectedPositionFolder;
    }

    public HashSet<Integer> getSelectedPositionSize() {
        return mSelectedPositionSize;
    }

    public void setSelectedPositionSize(HashSet<Integer> mSelectedPositionSize) {
        this.mSelectedPositionSize = mSelectedPositionSize;
    }

    public MutableLiveData<Integer> getSelectedAmount() {
        return mSelectedAmount;
    }

    public void setSelectedAmount() {
        mSelectedAmount.setValue(mSelectedItemSize.size() + mSelectedItemFolder.size());
    }
}