package com.example.project_myfit.ui.main.listfragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static com.example.project_myfit.MyFitConstant.LISTVIEW;
import static com.example.project_myfit.MyFitConstant.SORT_BRAND;
import static com.example.project_myfit.MyFitConstant.SORT_BRAND_REVERSE;
import static com.example.project_myfit.MyFitConstant.SORT_CREATE;
import static com.example.project_myfit.MyFitConstant.SORT_CREATE_REVERSE;
import static com.example.project_myfit.MyFitConstant.SORT_NAME;
import static com.example.project_myfit.MyFitConstant.SORT_NAME_REVERSE;

public class ListViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private final MutableLiveData<Integer> mSelectedAmount;
    private Folder mThisFolder;
    private long mFolderId;
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
        mSelectedItemFolder = new ArrayList<>();
        mSelectedItemSize = new ArrayList<>();
    }

    public List<Category> getCategoryListByParent() {
        return mRepository.getCategoryListByParent(mActivityModel.getCategory().getParentCategory());
    }

    public void deleteSize(@NotNull List<Size> sizeList) {
        for (Size s : sizeList) s.setIsDeleted(1);
        mRepository.sizeUpdate(sizeList);
    }

    public List<Folder> getAllFolder() {
        List<Folder> allFolderList = new ArrayList<>(mRepository.getAllFolderList());
        if (mSort == SORT_CREATE)
            allFolderList.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
        else if (mSort == SORT_CREATE_REVERSE)
            allFolderList.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
        else if (mSort == SORT_BRAND || mSort == SORT_NAME)
            allFolderList.sort((o1, o2) -> o1.getFolderName().compareTo(o2.getFolderName()));
        else if (mSort == SORT_BRAND_REVERSE || mSort == SORT_NAME_REVERSE)
            allFolderList.sort((o1, o2) -> o2.getFolderName().compareTo(o1.getFolderName()));
        return allFolderList;
    }

    public void deleteFolder(@NotNull List<Folder> folderList) {
        for (Folder f : folderList) f.setIsDeleted(1);
        mRepository.folderUpdate(folderList);
    }

    public void selectedItemMove(long folderId) {
        for (Folder f : mSelectedItemFolder) f.setFolderId(folderId);
        for (Size s : mSelectedItemSize) s.setFolderId(folderId);
        mRepository.folderUpdate(mSelectedItemFolder);
        mRepository.sizeUpdate(mSelectedItemSize);

        increaseItemAmount(folderId);
        decreaseItemAmount();
    }

    public void selectedItemDelete() {
        deleteFolder(mSelectedItemFolder);
        deleteSize(mSelectedItemSize);
        decreaseItemAmount();
    }

    public void setThisFolder(MainActivityViewModel activityViewModel) {
        mActivityModel = activityViewModel;
        if (mThisFolder == null)
            mThisFolder = mActivityModel.getFolder() == null ? null : mActivityModel.getFolder();
        mFolderId = mThisFolder == null ? mActivityModel.getCategory().getId() : mThisFolder.getId();
    }

    public List<Folder> getFolderHistory() {
        List<Folder> allFolderList = getAllFolder();
        List<Folder> folderHistory = new ArrayList<>();
        folderHistory.add(mThisFolder);
        List<Folder> folderHistory2 = getFolderHistory2(allFolderList, folderHistory, mThisFolder);
        Collections.reverse(folderHistory2);
        return folderHistory2;
    }

    @Contract("_, _, _ -> param2")
    private List<Folder> getFolderHistory2(@NotNull List<Folder> allFolderList, List<Folder> folderHistory, Folder thisFolder) {
        for (Folder parentFolder : allFolderList) {
            if (parentFolder.getId() == thisFolder.getFolderId()) {
                folderHistory.add(parentFolder);
                getFolderHistory2(allFolderList, folderHistory, parentFolder);
                break;
            }
        }
        return folderHistory;
    }

    public void folderSelected(@NotNull Folder folder, boolean isChecked, int position, FolderAdapter folderAdapter) {
        if (!folder.getFolderName().equals("dummy")) {
            if (isChecked) mSelectedItemFolder.add(folder);
            else mSelectedItemFolder.remove(folder);
            folderAdapter.setSelectedPosition(position);
            setSelectedAmount();
        }
    }

    public void sizeSelected(Size size, boolean isChecked, int position, SizeAdapterList sizeAdapterList, SizeAdapterGrid sizeAdapterGrid, int viewType) {
        if (isChecked) mSelectedItemSize.add(size);
        else mSelectedItemSize.remove(size);
        if (viewType == LISTVIEW) sizeAdapterList.setSelectedPosition(position);
        else sizeAdapterGrid.setSelectedPosition(position);
        setSelectedAmount();
    }

    public void setSelectedAmount() {
        mSelectedAmount.setValue(mSelectedItemSize.size() + mSelectedItemFolder.size());
    }

    public void selectedItemListInit() {
        if (mSelectedItemFolder == null)
            mSelectedItemFolder = new ArrayList<>();
        else mSelectedItemFolder.clear();
        if (mSelectedItemSize == null)
            mSelectedItemSize = new ArrayList<>();
        else mSelectedItemSize.clear();
    }

    public long getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(System.currentTimeMillis())));
    }

    public void addFolder(String folderName) {
        mRepository.folderInsert(new Folder(getCurrentTime(), folderName, mFolderId, mRepository.getFolderLargestOrder() + 1, "0", mActivityModel.getCategory().getParentCategory()));
        if (mThisFolder == null) {
            String amount = String.valueOf(Integer.parseInt(mActivityModel.getCategory().getItemAmount()) + 1);
            mActivityModel.getCategory().setItemAmount(amount);
            mRepository.categoryUpdate(mActivityModel.getCategory());
        } else {
            String amount = String.valueOf(Integer.parseInt(mThisFolder.getItemAmount()) + 1);
            mThisFolder.setItemAmount(amount);
            mRepository.folderUpdate(mThisFolder);
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

    public List<Size> getSelectedItemSize() {
        return mSelectedItemSize;
    }

    public boolean isFavoriteView() {
        return mFavoriteView;
    }

    public void setFavoriteView(boolean favoriteView) {
        this.mFavoriteView = favoriteView;
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

    public HashSet<Integer> getSelectedPositionFolder() {
        return mSelectedPositionFolder;
    }

    public HashSet<Integer> getSelectedPositionSizeList() {
        return mSelectedPositionSizeList;
    }

    public HashSet<Integer> getSelectedPositionSizeGrid() {
        return mSelectedPositionSizeGrid;
    }

    public void setSelectedPositionFolder(HashSet<Integer> mSelectedPositionFolder) {
        this.mSelectedPositionFolder = mSelectedPositionFolder;
    }

    public void setSelectedPositionSizeList(HashSet<Integer> mSelectedPositionSizeList) {
        this.mSelectedPositionSizeList = mSelectedPositionSizeList;
    }

    public void setSelectedPositionSizeGrid(HashSet<Integer> mSelectedPositionSizeGrid) {
        this.mSelectedPositionSizeGrid = mSelectedPositionSizeGrid;
    }

    public Repository getRepository() {
        return mRepository;
    }
}