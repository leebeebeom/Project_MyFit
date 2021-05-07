package com.example.project_myfit.main.list;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.FOLDER_TOGGLE;
import static com.example.project_myfit.util.MyFitConstant.GRIDVIEW;
import static com.example.project_myfit.util.MyFitConstant.LISTVIEW;
import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.util.MyFitConstant.SORT_LIST;
import static com.example.project_myfit.util.MyFitConstant.VIEW_TYPE;

public class ListViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private final MutableLiveData<Integer> mSelectedItemSizeLive;
    private List<Folder> mSelectedFolderList;
    private List<Size> mSelectedSizeList;
    private boolean mFavoriteView, isFolderToggleOpen;
    private LiveData<Category> mThisCategoryLive;
    private LiveData<Folder> mThisFolderLive;
    private List<Folder> mFolderPath;
    private SharedPreferences mViewTypePreference, mSortPreference, mFolderTogglePreference;
    private int mViewType, mSort;


    public ListViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mSelectedItemSizeLive = new MutableLiveData<>();
        initPreference();
    }

    private void initPreference() {
        mViewTypePreference = getApplication().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
        mViewType = mViewTypePreference.getInt(VIEW_TYPE, LISTVIEW);

        mSortPreference = getApplication().getSharedPreferences(SORT_LIST, Context.MODE_PRIVATE);
        mSort = mSortPreference.getInt(SORT_LIST, SORT_CUSTOM);

        mFolderTogglePreference = getApplication().getSharedPreferences(FOLDER_TOGGLE, Context.MODE_PRIVATE);
        isFolderToggleOpen = mFolderTogglePreference.getBoolean(FOLDER_TOGGLE, true);
    }

    public void selectAll(List<Folder> allFolderList, List<Size> allSizeList) {
        mSelectedFolderList.addAll(allFolderList);
        mSelectedFolderList.removeIf(folder -> folder.getId() == -1);
        mSelectedSizeList.addAll(allSizeList);
    }

    public List<Folder> getFolderPath(@NotNull Folder thisFolder) {
        List<Folder> allFolderList = mRepository.getFolderRepository().getFolderList(thisFolder.getParentCategory(), false, false);
        mFolderPath = new ArrayList<>();
        mFolderPath.add(thisFolder);
        completeFolderPath(allFolderList, mFolderPath, thisFolder);
        Collections.reverse(mFolderPath);
        return mFolderPath;
    }

    private void completeFolderPath(@NotNull List<Folder> allFolderList, List<Folder> folderPath, Folder thisFolder) {
        for (Folder parentFolder : allFolderList) {
            if (thisFolder != null && parentFolder.getId() == thisFolder.getParentId()) {
                folderPath.add(parentFolder);
                completeFolderPath(allFolderList, folderPath, parentFolder);
                break;
            }
        }
    }

    public void updateNewOrderFolderList(@NotNull List<Folder> newOrderNumberFolderList) {
        List<Folder> newSelectedFolderList = new ArrayList<>();
        for (Folder folder : newOrderNumberFolderList)
            if (mSelectedFolderList.contains(folder) && folder.getId() != -1)
                newSelectedFolderList.add(folder);
        mSelectedFolderList = newSelectedFolderList;

        mRepository.getFolderRepository().updateFolder(newOrderNumberFolderList);
    }

    public void updateNewOrderSizeList(@NotNull List<Size> newOrderNumberSizeList) {
        List<Size> newSelectedSizeList = new ArrayList<>();
        for (Size size : newOrderNumberSizeList)
            if (mSelectedSizeList.contains(size)) newSelectedSizeList.add(size);
        mSelectedSizeList = newSelectedSizeList;

        mRepository.getSizeRepository().updateSize(newOrderNumberSizeList);
    }

    public void clearSelectedItems() {
        if (mSelectedFolderList == null) mSelectedFolderList = new ArrayList<>();
        if (mSelectedSizeList == null) mSelectedSizeList = new ArrayList<>();

        mSelectedFolderList.clear();
        mSelectedSizeList.clear();
    }

    public void setSelectedItemSizeLiveValue() {
        mSelectedItemSizeLive.setValue(mSelectedSizeList.size() + mSelectedFolderList.size());
    }

    public void changeFolderToggleState() {
        isFolderToggleOpen = !isFolderToggleOpen;
        SharedPreferences.Editor editor = mFolderTogglePreference.edit();
        editor.putBoolean(FOLDER_TOGGLE, isFolderToggleOpen);
        editor.apply();
    }

    public void changeSort(int sort) {
        mSort = sort;
        SharedPreferences.Editor editor = mSortPreference.edit();
        editor.putInt(SORT_LIST, mSort);
        editor.apply();
    }

    public void changeViewType() {
        mViewType = mViewType == LISTVIEW ? GRIDVIEW : LISTVIEW;
        SharedPreferences.Editor editor = mViewTypePreference.edit();
        editor.putInt(VIEW_TYPE, mViewType);
        editor.apply();
    }

    public void updateSize(Size size) {
        mRepository.getSizeRepository().updateSize(size);
    }

    //getter----------------------------------------------------------------------------------------
    public MutableLiveData<Integer> getSelectedItemSizeLive() {
        return mSelectedItemSizeLive;
    }

    public List<Folder> getSelectedFolderList() {
        return mSelectedFolderList;
    }

    public List<Size> getSelectedSizeList() {
        return mSelectedSizeList;
    }

    public boolean isFavoriteView() {
        return mFavoriteView;
    }

    public void setFavoriteView(boolean favoriteView) {
        this.mFavoriteView = favoriteView;
    }

    public int getSelectedItemSize() {
        return mSelectedSizeList.size() + mSelectedFolderList.size();
    }

    public long getSelectedFolderId() {
        return mSelectedFolderList.get(0).getId();
    }

    public LiveData<List<Folder>> getFolderLive(long parentId) {
        return mRepository.getFolderRepository().getFolderLive(parentId, false, false);
    }

    public LiveData<List<Size>> getSizeLive(long parentId) {
        return mRepository.getSizeRepository().getSizeLive(parentId, false, false);
    }

    public List<Long> getFolderParentIdList(String parentCategory) {
        return mRepository.getFolderRepository().getFolderParentIdList(parentCategory, false, false);
    }

    public List<Long> getSizeParentIdList(String parentCategory) {
        return mRepository.getSizeRepository().getSizeParentIdList(parentCategory, false, false);
    }

    public LiveData<Category> getThisCategoryLive(long categoryId) {
        return mRepository.getCategoryRepository().getCategoryLive(categoryId);
    }

    public LiveData<Folder> getThisFolderLive(long folderId) {
        return mRepository.getFolderRepository().getSingleFolderLive(folderId);
    }

    public List<Folder> getFolderPathComplete() {
        return mFolderPath;
    }

    public int getViewType() {
        return mViewType;
    }

    public int getSort() {
        return mSort;
    }

    public boolean isFolderToggleOpen() {
        return isFolderToggleOpen;
    }
}