package com.example.project_myfit.ui.main.listfragment.treeview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemTreeFolderBinding;
import com.example.project_myfit.dialog.TreeViewDialog;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TreeHolderFolder extends TreeNode.BaseNodeViewHolder<TreeHolderFolder.FolderTreeHolder> {
    private ItemTreeFolderBinding mBinding;
    private boolean isSelected;

    public TreeHolderFolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, @NotNull FolderTreeHolder value) {
        mBinding = ItemTreeFolderBinding.inflate(LayoutInflater.from(context));
        mBinding.setFolder(value.folder);

        int amount = 0;
        for (Long l : value.folderFolderIdList)
            if (l == value.folder.getId()) amount++;
        for (Long l : value.sizeFolderIdList)
            if (l == value.folder.getId()) amount++;
        mBinding.amount.setText(String.valueOf(amount));

        LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) mBinding.arrowIcon.getLayoutParams();
        params.leftMargin = value.margin;

        //if this node is selected folder
        for (Folder f : value.selectedItemFolder) {
            if (f.getId() == value.folder.getId()) {
                mBinding.iconLayout.setAlpha(0.5f);
                mBinding.text.setAlpha(0.5f);
                isSelected = true;
                //parent node alpha
                TreeNode parent = node.getParent();
                if (parent.getViewHolder() instanceof TreeHolderCategory) {
                    TreeHolderCategory holder = (TreeHolderCategory) parent.getViewHolder();
                    holder.getBinding().iconLayout.setAlpha(0.5f);
                    holder.getBinding().text.setAlpha(0.5f);
                } else {
                    TreeHolderFolder holder = (TreeHolderFolder) parent.getViewHolder();
                    holder.getBinding().iconLayout.setAlpha(0.5f);
                    holder.getBinding().text.setAlpha(0.5f);
                }
                break;
            }
        }

        //if parent node is selected folder
        if (node.getParent().getViewHolder() instanceof TreeHolderFolder && ((TreeHolderFolder) node.getParent().getViewHolder()).isSelected) {
            mBinding.iconLayout.setAlpha(0.5f);
            mBinding.text.setAlpha(0.5f);
            isSelected = true;
        }

        //if selected item position is this node
        if (value.selectedItemSize.size() != 0) {
            Size size = value.selectedItemSize.get(0);
            if (size.getFolderId() == value.folder.getId()) {
                mBinding.folderIcon.setAlpha(0.5f);
                mBinding.text.setAlpha(0.5f);
            }
        }

        int margin = (int) context.getResources().getDimension(R.dimen._8sdp);
        for (Folder folder : value.allFolderList) {
            if (value.folder.getId() == folder.getFolderId()) {
                TreeNode treeNode = new TreeNode(new FolderTreeHolder(folder, value.margin + margin, value.listener,
                        value.selectedItemFolder, value.selectedItemSize, value.allFolderList, value.folderFolderIdList, value.sizeFolderIdList))
                        .setViewHolder(new TreeHolderFolder(context));
                node.addChild(treeNode);
            }
        }

        if (node.getChildren().size() != 0)
            mBinding.iconLayout.setOnClickListener(v -> tView.toggleNode(node));
        else mBinding.arrowIcon.setVisibility(View.INVISIBLE);

        mBinding.addIcon.setOnClickListener(v -> value.listener.treeViewFolderAddClick(mNode, value));
        return mBinding.getRoot();
    }

    public void setIconClickable() {
        mBinding.arrowIcon.setVisibility(View.VISIBLE);
        mBinding.iconLayout.setOnClickListener(v -> tView.toggleNode(mNode));
    }

    public boolean isNotSelected() {
        return !isSelected;
    }

    public ItemTreeFolderBinding getBinding() {
        return mBinding;
    }

    @Override
    public void toggle(boolean active) {
        mBinding.arrowIcon.setImageResource(active ? R.drawable.icon_triangle_down : R.drawable.icon_triangle_right);
        mBinding.folderIcon.setImageResource(active ? R.drawable.icon_folder_open : R.drawable.icon_folder);
    }

    public static class FolderTreeHolder {
        private final Folder folder;
        private final int margin;
        private final TreeViewDialog.TreeViewAddClick listener;
        private final List<Folder> selectedItemFolder, allFolderList;
        private final List<Size> selectedItemSize;
        private final List<Long> folderFolderIdList, sizeFolderIdList;

        public FolderTreeHolder(Folder folder, int margin, TreeViewDialog.TreeViewAddClick listener, List<Folder> selectedItemFolder,
                                List<Size> selectedItemSize, List<Folder> allFolderList, List<Long> folderFolderIdList, List<Long> sizeFolderIdList) {
            this.folder = folder;
            this.margin = margin;
            this.listener = listener;
            this.selectedItemFolder = selectedItemFolder;
            this.selectedItemSize = selectedItemSize;
            this.allFolderList = allFolderList;
            this.folderFolderIdList = folderFolderIdList;
            this.sizeFolderIdList = sizeFolderIdList;
        }

        public Folder getFolder() {
            return folder;
        }

        public int getMargin() {
            return margin;
        }
    }
}


