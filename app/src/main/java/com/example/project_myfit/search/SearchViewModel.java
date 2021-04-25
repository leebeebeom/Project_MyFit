package com.example.project_myfit.search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.RecentSearch;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.search.main.adapter.SearchAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.ETC;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.TOP;

public class SearchViewModel extends AndroidViewModel {
    private final Repository.RecentSearchRepository mRecentSearchRepository;
    private final List<Object> mSelectedItemList;
    private final MutableLiveData<Integer> mSelectedItemSizeLive;
    private boolean mActionModeOn;
    private int mCurrentItem;
    private List<String> mSizeBrandList, mSizeNameList;

    public SearchViewModel(@NonNull @NotNull Application application) {
        super(application);
        mRecentSearchRepository = Repository.getRecentSearchRepository(application);
        mSelectedItemList = new ArrayList<>();
        mSelectedItemSizeLive = new MutableLiveData<>();
    }

    public void selectAllClick(boolean isChecked, SearchAdapter[] searchAdapterArray) {
        if (!mSelectedItemList.isEmpty()) mSelectedItemList.clear();
        if (isChecked) {
            mSelectedItemList.addAll(searchAdapterArray[mCurrentItem].getCurrentList());
            searchAdapterArray[mCurrentItem].selectAll();
        } else
            searchAdapterArray[mCurrentItem].deselectAll();
        mSelectedItemSizeLive.setValue(mSelectedItemList.size());
    }

//    public void sizeSelected(@NotNull Size size, boolean isChecked, @NotNull SearchAdapter[] searchAdapterArray) {
//        searchAdapterArray[mCurrentItem].itemSelected(size.getId());
//        if (isChecked) mSelectedItemList.add(size);
//        else mSelectedItemList.remove(size);
//        mSelectedItemSizeLive.setValue(mSelectedItemList.size());
//    }
//
//    public void folderSelected(@NotNull Folder folder, boolean isChecked, @NotNull SearchAdapter[] searchAdapterArray) {
//        searchAdapterArray[mCurrentItem].itemSelected(folder.getId());
//        if (isChecked) mSelectedItemList.add(folder);
//        else mSelectedItemList.remove(folder);
//        mSelectedItemSizeLive.setValue(mSelectedItemList.size());
//    }

    //TODO check
    public void itemSelected(Object item, long id, boolean isChecked, @NotNull SearchAdapter[] searchAdapterArray) {
        if (isChecked) mSelectedItemList.add(item);
        else mSelectedItemList.remove(item);
        mSelectedItemSizeLive.setValue(mSelectedItemList.size());
        searchAdapterArray[mCurrentItem].itemSelected(id);
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
        List<Folder> selectedFolderList = new ArrayList<>();
        for (Object o : mSelectedItemList)
            if (o instanceof Folder) selectedFolderList.add((Folder) o);
        return selectedFolderList;
    }

    public List<Size> getSelectedSizeList() {
        List<Size> selectedSizeList = new ArrayList<>();
        for (Object o : mSelectedItemList)
            if (o instanceof Size) selectedSizeList.add((Size) o);
        return selectedSizeList;
    }

    public int getSelectedItemSize() {
        if (mSelectedItemSizeLive.getValue() != null)
            return mSelectedItemSizeLive.getValue();
        else return 0;
    }

    public void overLapRecentSearchReInsert(String word) {
        mRecentSearchRepository.overLapRecentSearchReInsert(word);
    }

    public void insertRecentSearch(String word) {
        mRecentSearchRepository.insertRecentSearch(word);
    }

    public LiveData<List<RecentSearch>> getAllRecentSearchLive() {
        return mRecentSearchRepository.getAllRecentSearchLive();
    }

    public void deleteRecentSearch(RecentSearch recentSearch) {
        mRecentSearchRepository.deleteRecentSearch(recentSearch);
    }

    public long getSelectedFolderId() {
        return ((Folder) mSelectedItemList.get(0)).getId();
    }


    public LiveData<List<Folder>> getAllFolderLive() {
        return Repository.getFolderRepository(getApplication()).getAllFolderLive();
    }

    public LiveData<List<Size>> getAllSizeLive() {
        return Repository.getSizeRepository(getApplication()).getAllSizeLive();
    }

    public LiveData<List<String>> getAllFolderNameLive() {
        return Repository.getFolderRepository(getApplication()).getAllFolderNameLive();
    }

    public LiveData<List<String>> getAllSizeBrandLive() {
        return Repository.getSizeRepository(getApplication()).getAllSizeBrandLive();
    }

    public LiveData<List<String>> getAllSizeNameLive() {
        return Repository.getSizeRepository(getApplication()).getAllSizeNameLive();
    }

    public List<Object> getSelectedItemList() {
        return mSelectedItemList;
    }

    public MutableLiveData<Integer> getSelectedItemSizeLive() {
        return mSelectedItemSizeLive;
    }

    public boolean isActionModeOn() {
        return mActionModeOn;
    }

    public void setActionModeOn(boolean mActionModeOn) {
        this.mActionModeOn = mActionModeOn;
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
}