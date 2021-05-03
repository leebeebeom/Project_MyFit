package com.example.project_myfit.search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.ParentModel;
import com.example.project_myfit.data.model.RecentSearch;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.search.adapter.SearchFolderAdapter;
import com.example.project_myfit.search.adapter.SearchSizeAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.ETC;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.RECENT_SEARCH_SEARCH;
import static com.example.project_myfit.util.MyFitConstant.TOP;

public class SearchViewModel extends AndroidViewModel {
    private final Repository.RecentSearchRepository mRecentSearchRepository;
    private List<Folder> mSelectedFolderList;
    private List<Size> mSelectedSizeList;
    private final MutableLiveData<Integer> mSelectedItemSizeLive;
    private int mCurrentItem;
    private List<String> mSizeBrandList, mSizeNameList;
    private MutableLiveData<Integer[]> mFolderFilteredSize, mSizeFilteredSize;

    public SearchViewModel(@NonNull @NotNull Application application) {
        super(application);
        mRecentSearchRepository = Repository.getRecentSearchRepository(application);
        mSelectedItemSizeLive = new MutableLiveData<>();
    }

    public void selectAllClick(boolean isChecked, SearchFolderAdapter[] folderAdapterArray,
                               SearchSizeAdapter[] sizeAdapterArray) {
        if (!mSelectedFolderList.isEmpty()) mSelectedFolderList.clear();
        if (!mSelectedSizeList.isEmpty()) mSelectedSizeList.clear();

        if (isChecked) {
            mSelectedFolderList.addAll(folderAdapterArray[mCurrentItem].getCurrentList());
            folderAdapterArray[mCurrentItem].selectAll();
            mSelectedSizeList.addAll(sizeAdapterArray[mCurrentItem].getCurrentList());
            sizeAdapterArray[mCurrentItem].selectAll();
        } else {
            folderAdapterArray[mCurrentItem].deselectAll();
            sizeAdapterArray[mCurrentItem].deselectAll();
        }
        mSelectedItemSizeLive.setValue(mSelectedFolderList.size() + mSelectedSizeList.size());
    }

    public <T extends ParentModel> void itemSelected(T t, boolean isChecked) {
        if (t instanceof Folder) {
            if (isChecked) mSelectedFolderList.add((Folder) t);
            else mSelectedFolderList.remove(t);
        } else if (t instanceof Size) {
            if (isChecked) mSelectedSizeList.add((Size) t);
            else mSelectedSizeList.remove(t);
        }
        mSelectedItemSizeLive.setValue(mSelectedFolderList.size() + mSelectedSizeList.size());
    }

    public String getParentCategory() {
        String parentCategory = null;
        switch (mCurrentItem) {
            case 0:
                parentCategory = TOP;
                break;
            case 1:
                parentCategory = BOTTOM;
                break;
            case 2:
                parentCategory = OUTER;
                break;
            case 3:
                parentCategory = ETC;
                break;
        }
        return parentCategory;
    }

    public List<Folder> getSelectedFolderList() {
        if (mSelectedFolderList == null) mSelectedFolderList = new ArrayList<>();
        return mSelectedFolderList;
    }

    public List<Size> getSelectedSizeList() {
        if (mSelectedSizeList == null) mSelectedSizeList = new ArrayList<>();
        return mSelectedSizeList;
    }

    public int getSelectedItemSize() {
        return mSelectedFolderList.size() + mSelectedSizeList.size();
    }

    public LiveData<List<RecentSearch>> getRecentSearchLive() {
        return mRecentSearchRepository.getRecentSearchLive(RECENT_SEARCH_SEARCH);
    }

    public void recentSearchDelete(RecentSearch recentSearch) {
        mRecentSearchRepository.recentSearchDelete(recentSearch);
    }

    public long getSelectedFolderId() {
        return mSelectedFolderList.get(0).getId();
    }

    public LiveData<List<Folder>> getFolderLive() {
        return Repository.getFolderRepository(getApplication()).getFolderLive(false, false);
    }

    public LiveData<List<Size>> getSizeLive() {
        return Repository.getSizeRepository(getApplication()).getSizeLive(false, false);
    }

    public LiveData<List<String>> getFolderNameLive() {
        return Repository.getFolderRepository(getApplication()).getFolderNameLive(false, false);
    }

    public LiveData<List<String>> getSizeBrandLive() {
        return Repository.getSizeRepository(getApplication()).getSizeBrandLive(false, false);
    }

    public LiveData<List<String>> getSizeNameLive() {
        return Repository.getSizeRepository(getApplication()).getSizeNameLive(false, false);
    }

    public MutableLiveData<Integer> getSelectedItemSizeLive() {
        return mSelectedItemSizeLive;
    }

    public int getCurrentItem() {
        return mCurrentItem;
    }

    public void setCurrentItem(int currentItem) {
        this.mCurrentItem = currentItem;
    }

    public void sizeUpdate(Size size) {
        Repository.getSizeRepository(getApplication()).sizeUpdate(size);
    }

    public List<Long> getFolderParentIdList(String parentCategory) {
        return Repository.getFolderRepository(getApplication()).getFolderParentIdList(parentCategory, false, false);
    }

    public List<Long> getSizeParentIdList(String parentCategory) {
        return Repository.getSizeRepository(getApplication()).getSizeParentIdList(parentCategory, false, false);
    }

    public MutableLiveData<Integer[]> getFolderFilteredListSizeLive() {
        Integer[] init = {0,0,0,0};
        if (mFolderFilteredSize == null) mFolderFilteredSize = new MutableLiveData<>(init);
        return mFolderFilteredSize;
    }

    public MutableLiveData<Integer[]> getSizeFilteredListSizeLive() {
        Integer[] init = {0,0,0,0};
        if (mSizeFilteredSize == null) mSizeFilteredSize = new MutableLiveData<>(init);
        return mSizeFilteredSize;
    }
}