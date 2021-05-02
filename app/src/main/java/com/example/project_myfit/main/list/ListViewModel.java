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
import com.example.project_myfit.main.list.adapter.folderadapter.FolderAdapter;
import com.example.project_myfit.main.list.adapter.sizeadapter.SizeAdapterGrid;
import com.example.project_myfit.main.list.adapter.sizeadapter.SizeAdapterList;

import org.jetbrains.annotations.Contract;
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
    private final Repository.FolderRepository mFolderRepository;
    private final Repository.SizeRepository mSizeRepository;
    private final MutableLiveData<Integer> mSelectedItemSizeLive;
    private List<Folder> mSelectedFolderList;
    private List<Size> mSelectedSizeList;
    private boolean mFavoriteView, isFolderToggleOpen;
    private LiveData<Category> mThisCategoryLive;
    private LiveData<Folder> mThisFolderLive;
    private List<Folder> mFolderHistory;
    private SharedPreferences mViewTypePreference, mSortPreference, mFolderTogglePreference;
    private int mViewType, mSort;


    public ListViewModel(@NonNull Application application) {
        super(application);
        mFolderRepository = Repository.getFolderRepository(application);
        mSizeRepository = Repository.getSizeRepository(application);
        mSelectedItemSizeLive = new MutableLiveData<>();
        mSelectedFolderList = new ArrayList<>();
        mSelectedSizeList = new ArrayList<>();
        preferenceInit();
    }

    private void preferenceInit() {
        mViewTypePreference = getApplication().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
        mViewType = mViewTypePreference.getInt(VIEW_TYPE, LISTVIEW);

        mSortPreference = getApplication().getSharedPreferences(SORT_LIST, Context.MODE_PRIVATE);
        mSort = mSortPreference.getInt(SORT_LIST, SORT_CUSTOM);

        mFolderTogglePreference = getApplication().getSharedPreferences(FOLDER_TOGGLE, Context.MODE_PRIVATE);
        isFolderToggleOpen = mFolderTogglePreference.getBoolean(FOLDER_TOGGLE, true);
    }

    public void selectAllClick(boolean isChecked, FolderAdapter folderAdapter,
                               SizeAdapterList sizeAdapterList, SizeAdapterGrid sizeAdapterGrid) {
        if (!mSelectedFolderList.isEmpty()) mSelectedFolderList.clear();
        if (!mSelectedSizeList.isEmpty()) mSelectedSizeList.clear();

        if (isChecked) {
            mSelectedFolderList.addAll(folderAdapter.getCurrentList());
            mSelectedFolderList.removeIf(folder -> folder.getId() == -1);
            folderAdapter.selectAll();

            if (mViewType == LISTVIEW) {
                mSelectedSizeList.addAll(sizeAdapterList.getCurrentList());
                sizeAdapterList.selectAll();
            } else {
                mSelectedSizeList.addAll(sizeAdapterGrid.getCurrentList());
                sizeAdapterGrid.selectAll();
            }
        } else {
            folderAdapter.deselectAll();
            if (mViewType == LISTVIEW) sizeAdapterList.deselectAll();
            else sizeAdapterGrid.deselectAll();
        }
        mSelectedItemSizeLive.setValue(mSelectedSizeList.size() + mSelectedFolderList.size());
    }

    public List<Folder> getFolderHistory(@NotNull Folder thisFolder) {
        List<Folder> allFolderList = mFolderRepository.getFolderList(thisFolder.getParentCategory(), false, false);
        List<Folder> folderHistory = new ArrayList<>();
        folderHistory.add(thisFolder);
        List<Folder> folderHistory2 = getFolderHistory2(allFolderList, folderHistory, thisFolder);
        Collections.reverse(folderHistory2);
        mFolderHistory = folderHistory2;
        return mFolderHistory;
    }

    @Contract("_, _, _ -> param2")
    private List<Folder> getFolderHistory2(@NotNull List<Folder> allFolderList, List<Folder> folderHistory, Folder thisFolder) {
        for (Folder parentFolder : allFolderList) {
            if (thisFolder != null && parentFolder.getId() == thisFolder.getParentId()) {
                folderHistory.add(parentFolder);
                getFolderHistory2(allFolderList, folderHistory, parentFolder);
                break;
            }
        }
        return folderHistory;
    }

    //getter----------------------------------------------------------------------------------------
    public MutableLiveData<Integer> getSelectedSizeLive() {
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
        return mFolderRepository.getFolderLive(parentId, false, false);
    }

    public LiveData<List<Size>> getSizeLive(long parentId) {
        return mSizeRepository.getSizeLive(parentId, false, false);
    }

    public List<Long> getFolderParentIdList(String parentCategory) {
        return mFolderRepository.getFolderParentIdList(parentCategory, false, false);
    }

    public List<Long> getSizeParentIdList(String parentCategory) {
        return mSizeRepository.getSizeParentIdList(parentCategory, false, false);
    }

    public void sizeFavoriteClick(Size size) {
        mSizeRepository.sizeUpdate(size);
    }

    public void folderItemDrop(@NotNull List<Folder> newOrderNumberFolderList) {
        List<Folder> newSelectedFolderList = new ArrayList<>();
        for (Folder folder : newOrderNumberFolderList)
            if (mSelectedFolderList.contains(folder) && folder.getId() != -1)
                newSelectedFolderList.add(folder);
        mSelectedFolderList = newSelectedFolderList;

        mFolderRepository.folderUpdate(newOrderNumberFolderList);
    }

    public void sizeItemDrop(@NotNull List<Size> newOrderNumberSizeList) {
        List<Size> newSelectedSizeList = new ArrayList<>();
        for (Size size : newOrderNumberSizeList)
            if (mSelectedSizeList.contains(size)) newSelectedSizeList.add(size);
        mSelectedSizeList = newSelectedSizeList;

        mSizeRepository.sizeUpdate(newOrderNumberSizeList);
    }

    public LiveData<Category> getThisCategoryLive(long categoryId) {
        return Repository.getCategoryRepository(getApplication()).getCategoryLive(categoryId);
    }

    public LiveData<Folder> getThisFolderLive(long folderId) {
        return mFolderRepository.getSingleFolderLive(folderId);
    }

    public List<Folder> getFolderHistory3() {
        return mFolderHistory;
    }

    public void selectedItemsClear() {
        mSelectedFolderList.clear();
        mSelectedSizeList.clear();
    }

    public void itemSelected(Object o, boolean isChecked) {
        if (o instanceof Folder) {
            if (((Folder) o).getId() != -1) {
                if (isChecked) mSelectedFolderList.add((Folder) o);
                else mSelectedFolderList.remove(o);
            }
        } else if (o instanceof Size) {
            if (isChecked) mSelectedSizeList.add((Size) o);
            else mSelectedSizeList.remove(o);
        }
        mSelectedItemSizeLive.setValue(mSelectedSizeList.size() + mSelectedFolderList.size());
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

    public void folderToggleClick() {
        isFolderToggleOpen = !isFolderToggleOpen;
        SharedPreferences.Editor editor = mFolderTogglePreference.edit();
        editor.putBoolean(FOLDER_TOGGLE, isFolderToggleOpen);
        editor.apply();
    }

    public boolean sortChanged(int sort) {
        if (mSort != sort) {
            mSort = sort;
            SharedPreferences.Editor editor = mSortPreference.edit();
            editor.putInt(SORT_LIST, mSort);
            editor.apply();
            return true;
        } else return false;
    }

    public int viewTypeClick() {
        mViewType = mViewType == LISTVIEW ? GRIDVIEW : LISTVIEW;
        SharedPreferences.Editor editor = mViewTypePreference.edit();
        editor.putInt(VIEW_TYPE, mViewType);
        editor.apply();
        return mViewType;
    }
}