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

import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.util.MyFitConstant.SORT_LIST;
import static com.example.project_myfit.util.MyFitConstant.SORT_MAIN;

public class TreeViewModel extends AndroidViewModel {
    private final Repository.CategoryRepository mCategoryRepository;
    private final Repository.FolderRepository mFolderRepository;
    private String mParentCategory;
    private List<Folder> mAllFolderList, mSelectedFolderList;
    private List<Size> mSelectedSizeList;
    private List<Category> mAllCategoryList;
    private List<Long> mFolderParentIdList, mSizeParentIdList;
    private TreeNode mClickedNode;
    private final int mMargin, mMainSort, mListSort;

    public TreeViewModel(@NonNull @NotNull Application application) {
        super(application);
        mCategoryRepository = Repository.getCategoryRepository(application);
        mFolderRepository = Repository.getFolderRepository(application);
        mMargin = (int) application.getResources().getDimension(R.dimen._12sdp);
        mMainSort = getApplication().getSharedPreferences(SORT_MAIN, Context.MODE_PRIVATE).getInt(SORT_MAIN, SORT_CUSTOM);
        mListSort = getApplication().getSharedPreferences(SORT_LIST, Context.MODE_PRIVATE).getInt(SORT_LIST, SORT_CUSTOM);
    }

    public TreeHolderCategory getCategoryViewHolder(@NotNull TreeHolderCategory treeHolderCategory) {
        return treeHolderCategory.setItems(mSelectedFolderList, mSelectedSizeList,
                getFolderParentIdList(), getSizeParentIdList());
    }

    public TreeHolderFolder getFolderViewHolder(@NotNull TreeHolderFolder treeHolderFolder) {
        return treeHolderFolder.setItems(mSelectedFolderList, mSelectedSizeList,
                getFolderList(), getFolderParentIdList(), getSizeParentIdList());
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

    public void categoryAddFolderConfirmClick() {
        TreeHolderCategory categoryViewHolder = getClickedCategoryViewHolder();

        categoryViewHolder.setIconClickable();

        int size = Integer.parseInt(categoryViewHolder.getBinding().tvItemTreeCategoryContentsSize.getText().toString());
        categoryViewHolder.getBinding().tvItemTreeCategoryContentsSize.setText(String.valueOf(size + 1));

        Category dummy = categoryViewHolder.getCategory();
        dummy.setDummy(!dummy.getDummy());
        mCategoryRepository.categoryUpdate(dummy);
    }

    public void folderAddFolderConfirmClick() {
        TreeHolderFolder folderViewHolder = getClickedFolderViewHolder();

        folderViewHolder.setIconClickable();

        int size = Integer.parseInt(folderViewHolder.getBinding().tvItemTreeFolderContentsSize.getText().toString());
        folderViewHolder.getBinding().tvItemTreeFolderContentsSize.setText(String.valueOf(size + 1));

        Folder dummy = folderViewHolder.getFolder();
        dummy.setDummy(!dummy.getDummy());
        mFolderRepository.folderUpdate(dummy);
    }

    public void treeViewDestroy() {
        //for new category, new folder node
        mAllCategoryList = null;
        mAllFolderList = null;
        mFolderParentIdList = null;
        mSizeParentIdList = null;
    }

    public int getMargin() {
        return mMargin;
    }

    public int getPlusMargin() {
        int originMargin = getClickedFolderViewHolder().getMargin();
        int plusMarin = (int) getApplication().getResources().getDimension(R.dimen._8sdp);
        return originMargin + plusMarin;
    }

    public void setSelectedItemList(List<Folder> selectedFolderList, List<Size> selectedSizeList) {
        this.mSelectedFolderList = selectedFolderList;
        this.mSelectedSizeList = selectedSizeList;
    }

    public void setParentCategory(String mParentCategory) {
        this.mParentCategory = mParentCategory;
    }

    public String getParentCategory() {
        return mParentCategory;
    }

    private TreeHolderCategory getClickedCategoryViewHolder() {
        return (TreeHolderCategory) mClickedNode.getViewHolder();
    }

    private TreeHolderFolder getClickedFolderViewHolder() {
        return (TreeHolderFolder) mClickedNode.getViewHolder();
    }

    public List<Category> getCategoryList() {
        if (mAllCategoryList == null)
            mAllCategoryList = Sort.categorySort(mMainSort, mCategoryRepository.getCategoryListByParentCategory(mParentCategory));
        return mAllCategoryList;
    }

    public List<Folder> getFolderList() {
        if (mAllFolderList == null)
            mAllFolderList = Sort.folderSort(mListSort, mFolderRepository.getFolderListByParentCategory(mParentCategory));
        return mAllFolderList;
    }

    public int getSelectedItemSize() {
        return mSelectedFolderList.size() + mSelectedSizeList.size();
    }

    public void setClickedNode(TreeNode node) {
        mClickedNode = node;
    }

    public TreeNode getClickedNode() {
        return mClickedNode;
    }

    private List<Long> getFolderParentIdList() {
        if (mFolderParentIdList == null)
            mFolderParentIdList = mFolderRepository.getFolderParentIdListByParentCategory(mParentCategory);
        return mFolderParentIdList;
    }

    private List<Long> getSizeParentIdList() {
        if (mSizeParentIdList == null)
            mSizeParentIdList = Repository.getSizeRepository(getApplication()).getSizeParentIdListByParentCategory(mParentCategory);
        return mSizeParentIdList;
    }

    private long getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(System.currentTimeMillis())));
    }
}
