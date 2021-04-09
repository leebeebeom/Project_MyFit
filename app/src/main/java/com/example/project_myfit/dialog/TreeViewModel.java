package com.example.project_myfit.dialog;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_myfit.R;
import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.util.Sort;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.project_myfit.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.MyFitConstant.SORT_LIST;
import static com.example.project_myfit.MyFitConstant.SORT_MAIN;

public class TreeViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private String mParentCategory;
    private List<Folder> mAllFolderList, mSelectedFolderList;
    private List<Size> mSelectedSizeList;
    private List<Category> mAllCategoryList;
    private List<Long> mFolderFolderIdList, mSizeFolderIdList;
    private TreeNode mClickedNode;
    private final int mMargin, mMainSort, mListSort;

    public TreeViewModel(@NonNull @NotNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mMargin = (int) application.getResources().getDimension(R.dimen._12sdp);
        mMainSort = getApplication().getSharedPreferences(SORT_MAIN, Context.MODE_PRIVATE).getInt(SORT_MAIN, SORT_CUSTOM);
        mListSort = getApplication().getSharedPreferences(SORT_LIST, Context.MODE_PRIVATE).getInt(SORT_LIST, SORT_CUSTOM);
    }

    public void setParentCategory(String mParentCategory) {
        this.mParentCategory = mParentCategory;
    }

    public void setSelectedItemList(List<Folder> selectedFolderList, List<Size> selectedSizeList) {
        this.mSelectedFolderList = selectedFolderList;
        this.mSelectedSizeList = selectedSizeList;
    }

    public List<Category> getCategoryList() {
        if (mAllCategoryList == null)
            mAllCategoryList = Sort.categorySort(mMainSort, mRepository.getCategoryListByParent(mParentCategory));
        return mAllCategoryList;
    }

    public List<Folder> getFolderList() {
        if (mAllFolderList == null)
            mAllFolderList = Sort.folderSort(mListSort, mRepository.getFolderListByParent(mParentCategory));
        return mAllFolderList;
    }

    public int getMargin() {
        return mMargin;
    }

    public TreeHolderCategory getCategoryViewHolder(@NotNull TreeHolderCategory treeHolderCategory) {
        return treeHolderCategory.setItems(mSelectedFolderList, mSelectedSizeList,
                getFolderFolderIdList(), getSizeFolderIdList());
    }

    public TreeHolderFolder getFolderViewHolder(@NotNull TreeHolderFolder treeHolderFolder) {
        return treeHolderFolder.setItems(mSelectedFolderList, mSelectedSizeList,
                getFolderList(), getFolderFolderIdList(), getSizeFolderIdList());
    }

    public String getParentCategory() {
        return mParentCategory;
    }

    public void treeViewDestroy() {
        mAllCategoryList = null;
        mAllFolderList = null;
        mFolderFolderIdList = null;
        mSizeFolderIdList = null;
    }

    public int getSelectedItemSize() {
        return mSelectedFolderList.size() + mSelectedSizeList.size();
    }

    public Category addCategoryConfirmClick(@NotNull String categoryName) {
        return mRepository.treeViewAddCategory(new Category(categoryName.trim(), mParentCategory, mRepository.getCategoryLargestOrderPlus1()));
    }

    public void setClickedNode(TreeNode node) {
        mClickedNode = node;
    }

    public TreeNode getClickedNode() {
        return mClickedNode;
    }

    public void findCategoryClickedNode(@NotNull TreeNode nodeRoot) {
        for (TreeNode newNode : nodeRoot.getChildren()) {
            TreeHolderCategory newCategoryViewHolder = (TreeHolderCategory) newNode.getViewHolder();
            if (getClickedCategoryViewHolder().getCategoryId() == newCategoryViewHolder.getCategoryId()) {
                mClickedNode = newNode;
                break;
            }
        }
    }

    private TreeHolderCategory getClickedCategoryViewHolder() {
        return (TreeHolderCategory) mClickedNode.getViewHolder();
    }

    public Folder categoryFolderInsert(@NotNull String folderName) {
        Folder folder = new Folder(getCurrentTime(), folderName,
                getClickedCategoryViewHolder().getCategoryId(),
                mRepository.getFolderLargestOrderPlus1(), mParentCategory);
        mRepository.folderInsert(folder);
        return folder;
    }

    public void categoryAddFolderConfirmClick() {
        TreeHolderCategory categoryViewHolder = getClickedCategoryViewHolder();

        categoryViewHolder.setIconClickable();

        int size = Integer.parseInt(categoryViewHolder.getBinding().contentsSize.getText().toString());
        categoryViewHolder.getBinding().contentsSize.setText(String.valueOf(size + 1));

        Category dummy = getClickedCategoryViewHolder().getCategory();
        dummy.setDummy(!dummy.getDummy());
        mRepository.categoryUpdate(dummy);
    }

    public void findFolderClickedNode(TreeNode nodeRoot) {
        for (TreeNode folderNode : findAllFolderNode(nodeRoot)) {
            TreeHolderFolder newFolderViewHolder = (TreeHolderFolder) folderNode.getViewHolder();
            if (getClickedFolderViewHolder().getFolderId() == newFolderViewHolder.getFolderId()) {
                mClickedNode = folderNode;
                break;
            }
        }
    }

    @NotNull
    private List<TreeNode> findAllFolderNode(@NotNull TreeNode nodeRoot) {
        List<TreeNode> topFolderNodeList = new ArrayList<>();
        for (TreeNode categoryNode : nodeRoot.getChildren())
            if (!categoryNode.getChildren().isEmpty())
                topFolderNodeList.addAll(categoryNode.getChildren());

        List<TreeNode> allFolderNodeList = new ArrayList<>(topFolderNodeList);
        findAllFolderNode2(topFolderNodeList, allFolderNodeList);
        return allFolderNodeList;
    }

    private void findAllFolderNode2(@NotNull List<TreeNode> topFolderNodeList, List<TreeNode> allFolderNodeList) {
        for (TreeNode topFolderNode : topFolderNodeList)
            if (!topFolderNode.getChildren().isEmpty()) {
                allFolderNodeList.addAll(topFolderNode.getChildren());
                findAllFolderNode2(topFolderNode.getChildren(), allFolderNodeList);
            }
    }

    public TreeHolderFolder getClickedFolderViewHolder() {
        return (TreeHolderFolder) mClickedNode.getViewHolder();
    }

    @NotNull
    public Folder folderFolderInsert(@NotNull String folderName) {
        Folder folder = new Folder(getCurrentTime(), folderName, getClickedFolderViewHolder().getFolderId(),
                mRepository.getFolderLargestOrderPlus1(), mParentCategory);
        mRepository.folderInsert(folder);
        return folder;
    }

    public int getMargin2() {
        int originMargin = getClickedFolderViewHolder().getMargin();
        int plusMarin = (int) getApplication().getResources().getDimension(R.dimen._8sdp);
        return originMargin + plusMarin;
    }

    public void folderAddFolderConfirmClick() {
        TreeHolderFolder folderViewHolder = getClickedFolderViewHolder();

        folderViewHolder.setIconClickable();

        int size = Integer.parseInt(folderViewHolder.getBinding().contentsSize.getText().toString());
        folderViewHolder.getBinding().contentsSize.setText(String.valueOf(size + 1));

        Folder dummy = getClickedFolderViewHolder().getFolder();
        dummy.setDummy(!dummy.getDummy());
        mRepository.folderUpdate(dummy);
    }

    private List<Long> getFolderFolderIdList() {
        if (mFolderFolderIdList == null)
            mFolderFolderIdList = mRepository.getFolderFolderIdByParent(mParentCategory);
        return mFolderFolderIdList;
    }

    private List<Long> getSizeFolderIdList() {
        if (mSizeFolderIdList == null)
            mSizeFolderIdList = mRepository.getSizeFolderIdByParent(mParentCategory);
        return mSizeFolderIdList;
    }

    private long getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(System.currentTimeMillis())));
    }
}
