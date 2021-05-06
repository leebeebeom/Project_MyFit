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
    private final TreeHolderListener mListener;
    private List<Folder> mSelectedFolderList, mAllFolderList;
    private List<Size> mSelectedSizeList;
    private List<Long> mFolderParentIdList, mSizeParentIdList;
    private ItemTreeFolderBinding mBinding;
    private boolean mIsClickable = true;
    private final AdapterUtil mAdapterUtil;
    private final Folder mThisFolder;

    public TreeHolderFolder(Context context, TreeHolderListener listener, Folder thisFolder) {
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

        mBinding.tvContentsSize.setText(String.valueOf(mAdapterUtil
                .getContentsSize(value.folder.getId(), mFolderParentIdList, mSizeParentIdList)));

        LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) mBinding.iconArrow.getLayoutParams();
        params.leftMargin = value.margin;

        if (isSelectedFolder(value)) {
            setAlpha();
            if (node.getParent().getViewHolder() instanceof TreeHolderCategory)
                ((TreeHolderCategory) node.getParent().getViewHolder()).setAlpha();
            else
                ((TreeHolderFolder) node.getParent().getViewHolder()).setAlpha();
        }

        if (mIsClickable &&
                //is parent selected folder
                (node.getParent().getViewHolder() instanceof TreeHolderFolder && !((TreeHolderFolder) node.getParent().getViewHolder()).isClickable()) ||
                isSelectedFolderParent(value) ||
                isSelectedSizeParent(value))
            setAlpha();

        addChildNode(node, value);

        if (!node.getChildren().isEmpty())
            mBinding.layoutFolderIcon.setOnClickListener(v -> tView.toggleNode(node));
        else mBinding.iconArrow.setVisibility(View.INVISIBLE);

        if (mThisFolder != null && mThisFolder.getId() == value.folder.getId())
            mBinding.tvCurrentPosition.setVisibility(View.VISIBLE);
        else mBinding.tvCurrentPosition.setVisibility(View.GONE);

        mBinding.iconAdd.setOnClickListener(v -> mListener.addFolderIconClick(mNode, value.folder.getId()));
        return mBinding.getRoot();
    }

    private boolean isSelectedFolder(@NotNull FolderTreeHolder value) {
        if (!mSelectedFolderList.isEmpty())
            for (Folder selectedFolder : mSelectedFolderList)
                if (selectedFolder.getId() == value.folder.getId()) {
                    return true;
                }
        return false;
    }

    private boolean isSelectedFolderParent(@NotNull FolderTreeHolder value) {
        if (!mSelectedFolderList.isEmpty())
            for (Folder selectedFolder : mSelectedFolderList)
                if (selectedFolder.getParentId() == value.folder.getId())
                    return true;
        return false;
    }

    private boolean isSelectedSizeParent(@NotNull FolderTreeHolder value) {
        if (!mSelectedSizeList.isEmpty())
            for (Size selectedSize : mSelectedSizeList)
                if (selectedSize.getParentId() == value.folder.getId())
                    return true;
        return false;
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

    private void setAlpha() {
        if (mIsClickable) {
            mBinding.layoutFolderIcon.setAlpha(0.5f);
            mBinding.tvFolderName.setAlpha(0.5f);
            mIsClickable = false;
        }
    }

    public boolean isClickable() {
        return mIsClickable;
    }

    public void setIconClickable() {
        mBinding.iconArrow.setVisibility(View.VISIBLE);
        mBinding.layoutFolderIcon.setOnClickListener(v -> tView.toggleNode(mNode));
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
        mBinding.iconArrow.setImageResource(active ? R.drawable.icon_triangle_down : R.drawable.icon_triangle_right);
        mBinding.iconFolder.setImageResource(active ? R.drawable.icon_folder_open : R.drawable.icon_folder);
    }

    public static class FolderTreeHolder {
        private final Folder folder;
        private final int margin;

        public FolderTreeHolder(Folder folder, int margin) {
            this.folder = folder;
            this.margin = margin;
        }
    }
}


