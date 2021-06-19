//package com.leebeebeom.closetnote.ui.search;
//
//import android.app.Application;
//
//import androidx.annotation.NonNull;
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//import com.leebeebeom.closetnote.data.model.model.Folder;
//import com.leebeebeom.closetnote.data.model.model.RecentSearch;
//import com.leebeebeom.closetnote.ui.search.adapter.SearchFolderAdapter;
//import com.leebeebeom.closetnote.ui.search.adapter.SearchSizeAdapter;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static com.leebeebeom.closetnote.util.MyFitConstant.RECENT_SEARCH_SEARCH;
//
//public class SearchViewModel extends AndroidViewModel {
//    private final Repository mRepository;
//    private List<Folder> mSelectedFolderList;
//    private List<SizeTop> mSelectedSizeList;
//    private final MutableLiveData<Integer> mSelectedItemSizeLive;
//    private int mCurrentItem;
//    private List<String> mSizeBrandList, mSizeNameList;
//    private MutableLiveData<Integer[]> mFolderFilteredSize, mSizeFilteredSize;
//
//    public SearchViewModel(@NonNull @NotNull Application application) {
//        super(application);
//        mRepository = new Repository(application);
//        mSelectedItemSizeLive = new MutableLiveData<>();
//    }
//
//    public void selectAllClick(boolean isChecked, SearchFolderAdapter[] folderAdapterArray,
//                               SearchSizeAdapter[] sizeAdapterArray) {
//        if (!mSelectedFolderList.isEmpty()) mSelectedFolderList.clear();
//        if (!mSelectedSizeList.isEmpty()) mSelectedSizeList.clear();
//
//        if (isChecked) {
//            mSelectedFolderList.addAll(folderAdapterArray[mCurrentItem].getCurrentList());
//            folderAdapterArray[mCurrentItem].selectAll();
//            mSelectedSizeList.addAll(sizeAdapterArray[mCurrentItem].getCurrentList());
//            sizeAdapterArray[mCurrentItem].selectAll();
//        } else {
//            folderAdapterArray[mCurrentItem].deselectAll();
//            sizeAdapterArray[mCurrentItem].deselectAll();
//        }
//        mSelectedItemSizeLive.setValue(mSelectedFolderList.size() + mSelectedSizeList.size());
//    }
//
//    public <T extends ParentModel> void itemSelected(T t, boolean isChecked) {
//        if (t instanceof Folder) {
//            if (isChecked) mSelectedFolderList.add((Folder) t);
//            else mSelectedFolderList.remove(t);
//        } else if (t instanceof SizeTop) {
//            if (isChecked) mSelectedSizeList.add((SizeTop) t);
//            else mSelectedSizeList.remove(t);
//        }
//        mSelectedItemSizeLive.setValue(mSelectedFolderList.size() + mSelectedSizeList.size());
//    }
//
//    public List<Folder> getSelectedFolderList() {
//        if (mSelectedFolderList == null) mSelectedFolderList = new ArrayList<>();
//        return mSelectedFolderList;
//    }
//
//    public List<SizeTop> getSelectedSizeList() {
//        if (mSelectedSizeList == null) mSelectedSizeList = new ArrayList<>();
//        return mSelectedSizeList;
//    }
//
//    public int getSelectedItemSize() {
//        return mSelectedFolderList.size() + mSelectedSizeList.size();
//    }
//
//    public LiveData<List<RecentSearch>> getRecentSearchLive() {
//        return mRepository.getRecentSearchRepository().getRecentSearchLive(RECENT_SEARCH_SEARCH);
//    }
//
//    public void recentSearchDelete(RecentSearch recentSearch) {
//        mRepository.getRecentSearchRepository().deleteRecentSearch(recentSearch);
//    }
//
//    public long getSelectedFolderId() {
//        return mSelectedFolderList.get(0).getId();
//    }
//
//    public LiveData<List<Folder>> getFolderLive() {
//        return mRepository.getFolderRepository().getFolderLive(false, false);
//    }
//
//    public LiveData<List<SizeTop>> getSizeLive() {
//        return mRepository.getSizeRepository().getSizeLive(false, false);
//    }
//
//    public LiveData<List<String>> getFolderNameLive() {
//        return mRepository.getFolderRepository().getFolderNameLive(false, false);
//    }
//
//    public LiveData<List<String>> getSizeBrandLive() {
//        return mRepository.getSizeRepository().getSizeBrandLive(false, false);
//    }
//
//    public LiveData<List<String>> getSizeNameLive() {
//        return mRepository.getSizeRepository().getSizeNameLive(false, false);
//    }
//
//    public MutableLiveData<Integer> getSelectedItemSizeLive() {
//        return mSelectedItemSizeLive;
//    }
//
//    public int getCurrentItem() {
//        return mCurrentItem;
//    }
//
//    public void setCurrentItem(int currentItem) {
//        this.mCurrentItem = currentItem;
//    }
//
//    public void sizeUpdate(SizeTop size) {
//        mRepository.getSizeRepository().updateSize(size);
//    }
//
//    public List<Long> getFolderParentIdList(int parentCategory) {
//        return mRepository.getFolderRepository().getFolderParentIdList(parentCategory, false, false);
//    }
//
//    public List<Long> getSizeParentIdList(int parentCategory) {
//        return mRepository.getSizeRepository().getSizeParentIdList(parentCategory, false, false);
//    }
//
//    public MutableLiveData<Integer[]> getFolderFilteredListSizeLive() {
//        Integer[] init = {0, 0, 0, 0};
//        if (mFolderFilteredSize == null) mFolderFilteredSize = new MutableLiveData<>(init);
//        return mFolderFilteredSize;
//    }
//
//    public MutableLiveData<Integer[]> getSizeFilteredListSizeLive() {
//        Integer[] init = {0, 0, 0, 0};
//        if (mSizeFilteredSize == null) mSizeFilteredSize = new MutableLiveData<>(init);
//        return mSizeFilteredSize;
//    }
//
//    public long[] getSelectedItemIdArray() {
//        List<Long> selectedItemIdList = mSelectedFolderList.stream().map(Folder::getId).collect(Collectors.toList());
//        selectedItemIdList.addAll(mSelectedSizeList.stream().map(SizeTop::getId).collect(Collectors.toList()));
//        return selectedItemIdList.stream().mapToLong(Long::longValue).toArray();
//    }
//}