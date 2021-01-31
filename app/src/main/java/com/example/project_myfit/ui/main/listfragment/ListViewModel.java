package com.example.project_myfit.ui.main.listfragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.Repository;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.adapter.folderdapter.FolderAdapter;
import com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter.SizeAdapterGrid;
import com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter.SizeAdapterList;
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

import static com.example.project_myfit.MyFitConstant.SORT_LATEST;
import static com.example.project_myfit.MyFitConstant.SORT_LATEST_REVERSE;

public class ListViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private final MutableLiveData<Integer> mSelectedAmount;
    private final FolderAdapter mFolderAdapter;
    private final SizeAdapterList mSizeAdapterList;
    private final SizeAdapterGrid mSizeAdapterGrid;
    private Folder mThisFolder;
    private long mFolderId;
    private List<Folder> mFolderHistory;
    private List<Folder> mSelectedItemFolder;
    private List<Size> mSelectedItemSize;
    private TreeNode mAddNode;
    private TreeHolderCategory.CategoryTreeHolder mCategoryAddValue;
    private TreeHolderFolder.FolderTreeHolder mFolderAddValue;
    private HashSet<Integer> mSelectedPositionFolder, mSelectedPositionSizeList, mSelectedPositionSizeGrid;
    private MainActivityViewModel mActivityModel;
    private boolean mFavoriteView;
    private int mSort;
    private TreeNode mTreeNodeRoot;

    public ListViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mSelectedAmount = new MutableLiveData<>();

        mFolderAdapter = new FolderAdapter(this);
        mSizeAdapterList = new SizeAdapterList(this);
        mSizeAdapterGrid = new SizeAdapterGrid(this);
    }

    //category--------------------------------------------------------------------------------------
    public void updateCategory(Category category) {
        mRepository.categoryUpdate(category);
    }

    public List<Category> getCategoryList() {
        return mRepository.getCategoryList(mActivityModel.getCategory().getParentCategory());
    }

    public int getCategoryLargestOrder() {
        return mRepository.getCategoryLargestOrder();
    }

    public Category treeViewAddCategory(Category category) {
        return mRepository.treeViewAddCategory(category);
    }
    //----------------------------------------------------------------------------------------------

    //folder----------------------------------------------------------------------------------------
    public LiveData<List<Folder>> getFolderLive() {
        return mRepository.getFolderLive(mFolderId);
    }

    public List<Folder> getAllFolder() {
        List<Folder> allFolderList = new ArrayList<>(mRepository.getAllFolder());
        if (mSort == SORT_LATEST)
            allFolderList.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
        else if (mSort == SORT_LATEST_REVERSE)
            allFolderList.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
        return allFolderList;
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

        increaseItemAmount(folderId);
        decreaseItemAmount();
    }

    public void selectedItemDelete() {
        deleteFolder(mSelectedItemFolder);
        deleteSize(mSelectedItemSize);
        decreaseItemAmount();
    }
    //----------------------------------------------------------------------------------------------

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
    //----------------------------------------------------------------------------------------------

    public void setThisFolder(MainActivityViewModel activityViewModel) {
        mActivityModel = activityViewModel;
        if (mThisFolder == null)
            mThisFolder = mActivityModel.getFolder() == null ? null : mActivityModel.getFolder();
        mFolderId = mThisFolder == null ? mActivityModel.getCategory().getId() : mThisFolder.getId();
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

    public void setAdapterActionModeState(int actionModeState) {
        mFolderAdapter.setActionModeState(actionModeState);
        mSizeAdapterList.setActionModeState(actionModeState);
        mSizeAdapterGrid.setActionModeState(actionModeState);
    }

    public void selectAllClick(boolean isChecked) {
        mSelectedItemSize.clear();
        mSelectedItemFolder.clear();
        if (isChecked) {
            mSelectedItemFolder.addAll(mFolderAdapter.getCurrentList());
            mSelectedItemSize.addAll(mSizeAdapterList.getCurrentList());
            mFolderAdapter.selectAll();
            mSizeAdapterList.selectAll();
            mSizeAdapterGrid.selectAll();
        } else {
            mFolderAdapter.deselectAll();
            mSizeAdapterList.deselectAll();
            mSizeAdapterGrid.deselectAll();
        }
        setSelectedAmount();
    }

    public void setSelectedPosition() {
        mSelectedPositionFolder = mFolderAdapter.getSelectedPosition();
        mSelectedPositionSizeList = mSizeAdapterList.getSelectedPosition();
        mSelectedPositionSizeGrid = mSizeAdapterGrid.getSelectedPosition();
    }

    public void restoreAdapterSelectedPosition() {
        mFolderAdapter.setSelectedPosition(mSelectedPositionFolder);
        mSizeAdapterList.setSelectedPosition(mSelectedPositionSizeList);
        mSizeAdapterGrid.setSelectedPosition(mSelectedPositionSizeGrid);
    }

    public void folderSelected(Folder folder, boolean isChecked, int position) {
        if (!folder.getFolderName().equals("dummy")) {
            if (isChecked) mSelectedItemFolder.add(folder);
            else mSelectedItemFolder.remove(folder);
            mFolderAdapter.setSelectedPosition(position);
            setSelectedAmount();
        }
    }

    public void sizeSelected(Size size, boolean isChecked, int position) {
        if (isChecked) mSelectedItemSize.add(size);
        else mSelectedItemSize.remove(size);
        mSizeAdapterList.setSelectedPosition(position);
        mSizeAdapterGrid.setSelectedPosition(position);
        setSelectedAmount();
    }

    public void setSelectedAmount() {
        mSelectedAmount.setValue(mSelectedItemSize.size() + mSelectedItemFolder.size());
    }

    public void selectedItemListInit() {
        mSelectedItemFolder = new ArrayList<>();
        mSelectedItemSize = new ArrayList<>();
    }

    public long getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(System.currentTimeMillis())));
    }

    public void addFolder(String folderName) {
        insertFolder(new Folder(getCurrentTime(), folderName, mFolderId, getFolderLargestOrder() + 1, "0"));
        if (mThisFolder == null) {
            String amount = String.valueOf(Integer.parseInt(mActivityModel.getCategory().getItemAmount()) + 1);
            mActivityModel.getCategory().setItemAmount(amount);
            mRepository.categoryUpdate(mActivityModel.getCategory());
        } else {
            String amount = String.valueOf(Integer.parseInt(mThisFolder.getItemAmount()) + 1);
            mThisFolder.setItemAmount(amount);
            updateFolder(mThisFolder);
        }
    }

    private void increaseItemAmount(long id) {
        Category category = mRepository.getCategory(id);
        Folder folder = mRepository.getFolder(id);
        int amount;
        if (mSelectedAmount.getValue() != null) {
            if (category != null) {
                amount = Integer.parseInt(category.getItemAmount()) + mSelectedAmount.getValue();
                category.setItemAmount(String.valueOf(amount));
                mRepository.categoryUpdate(category);
            } else {
                amount = Integer.parseInt(folder.getItemAmount()) + mSelectedAmount.getValue();
                folder.setItemAmount(String.valueOf(amount));
                mRepository.folderUpdate(folder);
            }
        }
    }

    public void decreaseItemAmount() {
        int amount;
        if (mSelectedAmount.getValue() != null) {
            if (mThisFolder == null) {
                amount = Integer.parseInt(mActivityModel.getCategory().getItemAmount()) - mSelectedAmount.getValue();
                mActivityModel.getCategory().setItemAmount(String.valueOf(amount));
                mRepository.categoryUpdate(mActivityModel.getCategory());
            } else {
                amount = Integer.parseInt(mThisFolder.getItemAmount()) - mSelectedAmount.getValue();
                mThisFolder.setItemAmount(String.valueOf(amount));
                mRepository.folderUpdate(mThisFolder);
            }
        }
    }

    //node------------------------------------------------------------------------------------------
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

    //getter----------------------------------------------------------------------------------------
    public Folder getThisFolder() {
        return mThisFolder;
    }

    public long getFolderId() {
        return mFolderId;
    }

    public MutableLiveData<Integer> getSelectedAmount() {
        return mSelectedAmount;
    }

    public List<Folder> getSelectedItemFolder() {
        return mSelectedItemFolder;
    }

    public FolderAdapter getFolderAdapter() {
        return mFolderAdapter;
    }

    public SizeAdapterList getSizeAdapterList() {
        return mSizeAdapterList;
    }

    public SizeAdapterGrid getSizeAdapterGrid() {
        return mSizeAdapterGrid;
    }

    public List<Size> getSelectedItemSize() {
        return mSelectedItemSize;
    }

    public boolean isFavoriteView() {
        return mFavoriteView;
    }

    public void setFavoriteView(boolean favoriteView) {
        this.mFavoriteView = favoriteView;
    }

    public int getSort() {
        return mSort;
    }

    public void setSort(int sort) {
        this.mSort = sort;
    }

    public Category getThisCategory() {
        return mActivityModel.getCategory();
    }

    public TreeNode getTreeNodeRoot() {
        return mTreeNodeRoot;
    }

    public void setTreeNodeRoot(TreeNode treeNodeRoot) {
        this.mTreeNodeRoot = treeNodeRoot;
    }
}