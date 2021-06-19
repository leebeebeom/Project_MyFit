//package com.leebeebeom.closetnote.ui.recyclebin.search;
//
//import android.app.Application;
//
//import androidx.annotation.NonNull;
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//import com.leebeebeom.closetnote.R;
//import com.leebeebeom.closetnote.data.model.model.Category;
//import com.leebeebeom.closetnote.data.model.model.Folder;
//import com.leebeebeom.closetnote.data.model.model.RecentSearch;
//import com.leebeebeom.closetnote.util.CommonUtil;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static com.leebeebeom.closetnote.util.MyFitConstant.RECENT_SEARCH_RECYCLE_BIN;
//
//public class RecycleBinSearchViewModel extends AndroidViewModel {
//    private final Repository mRepository;
//    private final MutableLiveData<Integer> mSelectedItemSizeLive;
//    private final MutableLiveData<Integer[]> mFilteredListSizeLive;
//    private int mCurrentItem;
//    private final List<Object> mSelectedItemList;
//    private SelectedItemTreat mSelectedItemTreat;
//    private List<String> mNoParentNameList;
//    private DummyUtil mDummyUtil;
//
//    public RecycleBinSearchViewModel(@NonNull @NotNull Application application) {
//        super(application);
//        mRepository = new Repository(application);
//
//        mSelectedItemList = new ArrayList<>();
//        mSelectedItemSizeLive = new MutableLiveData<>();
//        mFilteredListSizeLive = new MutableLiveData<>(new Integer[]{0, 0, 0, 0, 0});
//    }
//
//    public MutableLiveData<Integer[]> getFilteredListSizeLive() {
//        return mFilteredListSizeLive;
//    }
//
//    public void recentSearchDelete(RecentSearch recentSearch) {
//        mRepository.getRecentSearchRepository().deleteRecentSearch(recentSearch);
//    }
//
//    public void setCurrentItem(int currentItem) {
//        mCurrentItem = currentItem;
//    }
//
//    public int getCurrentItem() {
//        return mCurrentItem;
//    }
//
//    public LiveData<List<Category>> getCategoryLive() {
//        return mRepository.getCategoryRepository().getCategoryLive(true);
//    }
//
//    public LiveData<List<Folder>> getFolderLive() {
//        return mRepository.getFolderRepository().getFolderLive(true, false);
//    }
//
//    public LiveData<List<SizeTop>> getSizeLive() {
//        return mRepository.getSizeRepository().getSizeLive(true, false);
//    }
//
//    public List<Long> getFolderParentIdList() {
//        return mRepository.getFolderRepository().getFolderParentIdList(false, true);
//    }
//
//    public List<Long> getSizeParentIdList() {
//        return mRepository.getSizeRepository().getSizeParentIdList(false, true);
//    }
//
//    public LiveData<List<RecentSearch>> getRecentSearchLive() {
//        return mRepository.getRecentSearchRepository().getRecentSearchLive(RECENT_SEARCH_RECYCLE_BIN);
//    }
//
//    public LiveData<List<String>> getCategoryNameLive() {
//        return mRepository.getCategoryRepository().getCategoryNameLive(true);
//    }
//
//    public LiveData<List<String>> getFolderNameLive() {
//        return mRepository.getFolderRepository().getFolderNameLive(true, false);
//    }
//
//    public LiveData<List<String>> getSizeBrandLive() {
//        return mRepository.getSizeRepository().getSizeBrandLive(true, false);
//    }
//
//    public LiveData<List<String>> getSizeNameLive() {
//        return mRepository.getSizeRepository().getSizeNameLive(true, false);
//    }
//
//    public void selectAllClick(boolean isChecked, List<?> currentList) {
//        if (!mSelectedItemList.isEmpty()) mSelectedItemList.clear();
//        if (isChecked) mSelectedItemList.addAll(currentList);
//        mSelectedItemSizeLive.setValue(mSelectedItemList.size());
//    }
//
//    public int getSelectedItemSize() {
//        return mSelectedItemList.size();
//    }
//
//    public MutableLiveData<Integer> getSelectedItemSizeLive() {
//        return mSelectedItemSizeLive;
//    }
//
//    public List<?> getSelectedItem() {
//        return mSelectedItemList;
//    }
//
//    public <T extends ParentModel> void itemSelected(T t, boolean isChecked) {
//        if (isChecked) mSelectedItemList.add(t);
//        else mSelectedItemList.remove(t);
//        mSelectedItemSizeLive.setValue(mSelectedItemList.size());
//    }
//
//    public boolean restore() {
//        if (mSelectedItemTreat == null)
//            mSelectedItemTreat = new SelectedItemTreat(getApplication());
//
//        switch (mCurrentItem) {
//            case 0:
//                return categoryRestore();
//            case 1:
//                return folderRestore();
//            default:
//                return false;
//        }
//    }
//
//    private boolean categoryRestore() {
//        List<Category> selectedCategoryList = new ArrayList<>();
//
//        for (Object o : mSelectedItemList)
//            if (o instanceof Category) selectedCategoryList.add((Category) o);
//
//        mSelectedItemTreat.categoryTreat(selectedCategoryList, false);
//        return false;
//    }
//
//    private boolean folderRestore() {
//        List<Folder> selectedFolderList = new ArrayList<>();
//        for (Object o : mSelectedItemList)
//            if (o instanceof Folder) selectedFolderList.add((Folder) o);
//
//        if (!getNoParentFolderNameList(selectedFolderList).isEmpty())
//            return true;
//
//        if (mDummyUtil == null) mDummyUtil = new DummyUtil(getApplication());
//        mDummyUtil.setDummy(selectedFolderList);
//
//        mSelectedItemTreat.folderTreat(selectedFolderList, false);
//        return false;
//    }
//
//    private List<String> getNoParentFolderNameList(List<Folder> selectedFolderList) {
//        if (mNoParentNameList == null) mNoParentNameList = new ArrayList<>();
//        mNoParentNameList.clear();
//
//        for (Folder folder : selectedFolderList) {
//            Category category = mRepository.getCategoryRepository().getCategory(folder.getParentId(), false);
//            Folder parentFolder = mRepository.getFolderRepository().getFolder(folder.getParentId(), false, false);
//            if (category == null && parentFolder == null)
//                mNoParentNameList.add(folder.getName());
//        }
//        return mNoParentNameList;
//    }
//
//    public void noParentRestore() {
//        if (mSelectedItemList.get(0) instanceof Folder) {
//            noParentFolderRestore();
//        } else if (mSelectedItemList.get(0) instanceof SizeTop) {
//            noParentSizeRestore();
//        }
//    }
//
//    private void noParentFolderRestore() {
//        List<Folder> selectedFolderList = new ArrayList<>();
//        for (Object o : mSelectedItemList)
//            if (o instanceof Folder) selectedFolderList.add((Folder) o);
//
//        List<Folder> noParentFolderList = new ArrayList<>();
//        for (Folder folder : selectedFolderList) {
//            Category category = mRepository.getCategoryRepository().getCategory(folder.getParentId(), false);
//            Folder parentFolder = mRepository.getFolderRepository().getFolder(folder.getParentId(), false, false);
//            if (category == null && parentFolder == null) noParentFolderList.add(folder);
//        }
//        selectedFolderList.removeAll(noParentFolderList);
//
//        mSelectedItemTreat.folderTreat(selectedFolderList, false);
//        if (mDummyUtil == null) mDummyUtil = new DummyUtil(getApplication());
//        mDummyUtil.setDummy(selectedFolderList);
//
//        //------------------------------------------------------------------------------------------
//
//        List<List<Folder>> list = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
////        for (Folder folder : noParentFolderList)
////            switch (folder.getParentCategory()) {
////                case Constant.ParentCategory.TOP.name():
////                    list.get(0).add(folder);
////                    break;
////                case Constant.ParentCategory.BOTTOM.name():
////                    list.get(1).add(folder);
////                    break;
////                case Constant.ParentCategory.OUTER.toString():
////                    list.get(2).add(folder);
////                    break;
////                default:
////                    list.get(3).add(folder);
////            }
//
//        //get restore category
//        Category[] categoryArray = new Category[4];
//        for (int i = 0; i < categoryArray.length; i++)
//            categoryArray[i] = mRepository.getCategoryRepository().getCategory(getApplication().getString(R.string.restore_category_name),
//                    i, false);
//
//        //create category
//        int orderNumber = mRepository.getCategoryRepository().getCategoryLargestOrderPlus1();
//        long id = CommonUtil.createId();
//        for (int i = 0; i < list.size(); i++) {
////            if (!list.get(i).isEmpty() && categoryArray[i] == null) {
////                categoryArray[i] = new Category(id, getApplication().getString(R.string.restore_category_name),
////                        MyFitVariable.parentCategoryArray[i], orderNumber);
////                mRepository.getCategoryRepository().insertCategory(categoryArray[i]);
////                id++;
////                orderNumber++;
////            }
//        }
//
//        for (int i = 0; i < list.size(); i++) {
//            if (!list.get(i).isEmpty())
//                for (Folder folder : list.get(i)) folder.setParentId(categoryArray[i].getId());
//        }
//
//        for (Category category : categoryArray) category.setDummy(!category.getDummy());
//
//        mSelectedItemTreat.folderTreat(noParentFolderList, false);
//
//        mRepository.getCategoryRepository().updateCategory(Arrays.asList(categoryArray));
//    }
//
//    private void noParentSizeRestore() {
//        List<SizeTop> selectedSizeList = new ArrayList<>();
//    }
//
//    public String[] getNoParentNameArray() {
//        int size = mNoParentNameList.size();
//        return mNoParentNameList.toArray(new String[size]);
//    }
//}