package com.example.project_myfit.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemTreeFolderBinding;
import com.example.project_myfit.util.adapter.AdapterUtil;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TreeHolderFolder extends TreeNode.BaseNodeViewHolder<TreeHolderFolder.FolderTreeHolder> {
    private final TreeViewFolderFolderAddListener mListener;
    private List<Folder> mSelectedFolderList, mAllFolderList;
    private List<Size> mSelectedSizeList;
    private List<Long> mFolderParentIdList, mSizeParentIdList;
    private ItemTreeFolderBinding mBinding;
    private boolean isSelected;
    private boolean mIsClickable = true;
    private final AdapterUtil mAdapterUtil;
    private final Folder mThisFolder;

    public TreeHolderFolder(Context context, TreeViewFolderFolderAddListener listener, Folder thisFolder) {
        super(context);
        this.mListener = listener;
        this.mThisFolder = thisFolder;
        mAdapterUtil = new AdapterUtil(context);
    }

    public TreeHolderFolder setItems(List<Folder> selectedFolderList,
                                     List<Size> selectedSizeList, List<Folder> allFolderList, List<Long> folderParentIdList, List<Long> sizeParentIdList) {
        this.mSelectedFolderList = selectedFolderList;
        this.mSelectedSizeList = selectedSizeList;
        this.mAllFolderList = allFolderList;
        this.mFolderParentIdList = folderParentIdList;
        this.mSizeParentIdList = sizeParentIdList;
        return this;
    }

    @Override
    public View createNodeView(TreeNode node, @NotNull FolderTreeHolder value) {
        mBinding = ItemTreeFolderBinding.inflate(LayoutInflater.from(context));
        mBinding.setFolder(value.folder);

        mBinding.tvItemTreeFolderContentsSize.setText(String.valueOf(mAdapterUtil
                .getContentsSize(value.folder.getId(), mFolderParentIdList, mSizeParentIdList)));

        LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) mBinding.iconItemTreeFolderArrow.getLayoutParams();
        params.leftMargin = value.margin;

        isSelectedFolder(node, value);
        parentIsSelectedFolder(node);
        isSelectedFolderParent(value);
        isSelectedSizeParent(value);
        addChildNode(node, value);
        setExpandable(node);
        setCurrentPosition(value);

        mBinding.iconItemTreeFolderAdd.setOnClickListener(v -> mListener.treeViewFolderAddFolderClick(mNode, value));
        return mBinding.getRoot();
    }

    private void isSelectedFolder(TreeNode node, @NotNull FolderTreeHolder value) {
        for (Folder selectedFolder : mSelectedFolderList) {
            if (selectedFolder.getId() == value.folder.getId()) {
                setAlpha();
                isSelected = true;
                //부모 노드 알파
                if (node.getParent().getViewHolder() instanceof TreeHolderCategory)
                    ((TreeHolderCategory) node.getParent().getViewHolder()).setAlpha();
                else
                    ((TreeHolderFolder) node.getParent().getViewHolder()).setAlpha();
                break;
            }
        }
    }

    private void parentIsSelectedFolder(@NotNull TreeNode node) {
        if (node.getParent().getViewHolder() instanceof TreeHolderFolder && ((TreeHolderFolder) node.getParent().getViewHolder()).isSelected) {
            setAlpha();
            isSelected = true;
        }
    }

    private void isSelectedFolderParent(@NotNull FolderTreeHolder value) {
        if (!mSelectedFolderList.isEmpty())
            for (Folder selectedFolder : mSelectedFolderList)
                if (selectedFolder.getParentId() == value.folder.getId()) {
                    setAlpha();
                    break;
                }
    }

    private void isSelectedSizeParent(@NotNull FolderTreeHolder value) {
        if (!mSelectedSizeList.isEmpty())
            for (Size selectedSize : mSelectedSizeList)
                if (selectedSize.getParentId() == value.folder.getId()) {
                    setAlpha();
                    break;
                }
    }

    private void addChildNode(TreeNode node, @NotNull FolderTreeHolder value) {
        int margin = (int) context.getResources().getDimension(R.dimen._8sdp);

        for (Folder folder : mAllFolderList)
            if (value.folder.getId() == folder.getParentId()) {
                TreeNode treeNode = new TreeNode(new FolderTreeHolder(folder, value.margin + margin))
                        .setViewHolder(new TreeHolderFolder(context, mListener, mThisFolder).
                                setItems(mSelectedFolderList, mSelectedSizeList, mAllFolderList, mFolderParentIdList, mSizeParentIdList));
                node.addChild(treeNode);
            }
    }

    private void setExpandable(@NotNull TreeNode node) {
        if (!node.getChildren().isEmpty())
            mBinding.iconItemTreeFolderFolderLayout.setOnClickListener(v -> tView.toggleNode(node));
        else mBinding.iconItemTreeFolderArrow.setVisibility(View.INVISIBLE);
    }

    private void setCurrentPosition(@NotNull FolderTreeHolder value) {
        if (mThisFolder != null && mThisFolder.getId() == value.folder.getId())
            mBinding.tvItemTreeFolderCurrentPosition.setVisibility(View.VISIBLE);
        else mBinding.tvItemTreeFolderCurrentPosition.setVisibility(View.GONE);
    }

    private void setAlpha() {
        if (mIsClickable) {
            mBinding.iconItemTreeFolderFolderLayout.setAlpha(0.5f);
            mBinding.tvItemTreeFolderFolder.setAlpha(0.5f);
            mIsClickable = false;
        }
    }

    public boolean isClickable() {
        return mIsClickable;
    }

    public void setIconClickable() {
        mBinding.iconItemTreeFolderArrow.setVisibility(View.VISIBLE);
        mBinding.iconItemTreeFolderFolderLayout.setOnClickListener(v -> tView.toggleNode(mNode));
    }

    public ItemTreeFolderBinding getBinding() {
        return mBinding;
    }

    public Folder getFolder() {
        return ((FolderTreeHolder) mNode.getValue()).folder;
    }

    public long getFolderId() {
        return ((FolderTreeHolder) mNode.getValue()).folder.getId();
    }

    public int getMargin() {
        return ((FolderTreeHolder) mNode.getValue()).margin;
    }

    @Override
    public void toggle(boolean active) {
        mBinding.iconItemTreeFolderArrow.setImageResource(active ? R.drawable.icon_triangle_down : R.drawable.icon_triangle_right);
        mBinding.iconItemTreeFolderFolder.setImageResource(active ? R.drawable.icon_folder_open : R.drawable.icon_folder);
    }

    public static class FolderTreeHolder {
        private final Folder folder;
        private final int margin;

        public FolderTreeHolder(Folder folder, int margin) {
            this.folder = folder;
            this.margin = margin;
        }
    }

    public interface TreeViewFolderFolderAddListener {
        void treeViewFolderAddFolderClick(TreeNode node, TreeHolderFolder.FolderTreeHolder value);
    }
}


