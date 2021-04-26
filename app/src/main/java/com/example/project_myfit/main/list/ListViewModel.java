package com.example.project_myfit.main.list;

import android.app.Application;

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
import com.example.project_myfit.util.Sort;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.LISTVIEW;

public class ListViewModel extends AndroidViewModel {
    private final Repository.FolderRepository mFolderRepository;
    private final Repository.SizeRepository mSizeRepository;
    private final MutableLiveData<Integer> mSelectedItemSizeLive;
    private List<Folder> mSelectedFolderList;
    private List<Size> mSelectedSizeList;
    private boolean mFavoriteView;
    private LiveData<Category> mThisCategoryLive;
    private LiveData<Folder> mThisFolderLive;
    private List<Folder> mFolderHistory;

    public ListViewModel(@NonNull Application application) {
        super(application);
        mFolderRepository = Repository.getFolderRepository(application);
        mSizeRepository = Repository.getSizeRepository(application);
        mSelectedItemSizeLive = new MutableLiveData<>();
        mSelectedFolderList = new ArrayList<>();
        mSelectedSizeList = new ArrayList<>();
    }

    public void selectAllClick(boolean isChecked, FolderAdapter folderAdapter, int viewType,
                               SizeAdapterList sizeAdapterList, SizeAdapterGrid sizeAdapterGrid) {
        if (!mSelectedFolderList.isEmpty()) mSelectedFolderList.clear();
        if (!mSelectedSizeList.isEmpty()) mSelectedSizeList.clear();

        if (isChecked) {
            mSelectedFolderList.addAll(folderAdapter.getCurrentList());
            mSelectedFolderList.removeIf(folder -> folder.getId() == -1);
            folderAdapter.selectAll();

            if (viewType == LISTVIEW) {
                mSelectedSizeList.addAll(sizeAdapterList.getCurrentList());
                sizeAdapterList.selectAll();
            } else {
                mSelectedSizeList.addAll(sizeAdapterGrid.getCurrentList());
                sizeAdapterGrid.selectAll();
            }
        } else {
            folderAdapter.deselectAll();
            if (viewType == LISTVIEW) sizeAdapterList.deselectAll();
            else sizeAdapterGrid.deselectAll();
        }
        mSelectedItemSizeLive.setValue(mSelectedSizeList.size() + mSelectedFolderList.size());
    }

    public List<Folder> getFolderHistory(int sort, @NotNull Folder thisFolder) {
        List<Folder> allFolderList = Sort.folderSort(sort, mFolderRepository.getFolderListByParentCategory(thisFolder.getParentCategory()));
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

    public void folderSelected(@NotNull Folder folder, boolean isChecked, FolderAdapter folderAdapter) {
        if (folder.getId() != -1) {
            if (isChecked) mSelectedFolderList.add(folder);
            else mSelectedFolderList.remove(folder);
            folderAdapter.folderSelected(folder.getId());
            mSelectedItemSizeLive.setValue(mSelectedSizeList.size() + mSelectedFolderList.size());
        }
    }

    public void sizeSelected(Size size, boolean isChecked, SizeAdapterList sizeAdapterList, SizeAdapterGrid sizeAdapterGrid, int viewType) {
        if (isChecked) mSelectedSizeList.add(size);
        else mSelectedSizeList.remove(size);

        if (viewType == LISTVIEW) sizeAdapterList.sizeSelected(size.getId());
        else sizeAdapterGrid.sizeSelected(size.getId());
        mSelectedItemSizeLive.setValue(mSelectedSizeList.size() + mSelectedFolderList.size());
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

    public LiveData<List<Folder>> getFolderLiveByParentId(long parentId) {
        return mFolderRepository.getFolderLiveByParentId(parentId);
    }

    public LiveData<List<Size>> getSizeLiveByParentId(long parentId) {
        return mSizeRepository.getSizeLiveByParentId(parentId);
    }

    public List<Long> getFolderParentIdListByParentCategory(String parentCategory) {
        return mFolderRepository.getFolderParentIdListByParentCategory(parentCategory);
    }

    public List<Long> getSizeParentIdListByParentCategory(String parentCategory) {
        return mSizeRepository.getSizeParentIdListByParentCategory(parentCategory);
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
        return mFolderRepository.getFolderLive(folderId);
    }

    public List<Folder> getFolderHistory3() {
        return mFolderHistory;
    }

    public void selectedItemsClear() {
        mSelectedFolderList.clear();
        mSelectedSizeList.clear();
    }
}