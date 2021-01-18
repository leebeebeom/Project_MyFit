package com.example.project_myfit.ui.main.listfragment.treeview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemTreeFolderBinding;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.unnamed.b.atv.model.TreeNode;

import java.util.List;

public class TreeHolderFolder extends TreeNode.BaseNodeViewHolder<TreeHolderFolder.IconTreeHolder> {

    private ItemTreeFolderBinding mBinding;
    private boolean isSelected;

    public TreeHolderFolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, IconTreeHolder value) {
        mBinding = ItemTreeFolderBinding.inflate(LayoutInflater.from(context));
        mBinding.text.setText(value.folder.getFolderName());
        LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) mBinding.arrowIcon.getLayoutParams();
        params.leftMargin = value.margin;
        for (Folder f : value.selectedFolderList) {
            if (f.getId() == value.folder.getId()) {
                mBinding.getRoot().setAlpha(0.5f);
                isSelected = true;
                break;
            }
        }
        if (node.getParent().getViewHolder() instanceof TreeHolderFolder && ((TreeHolderFolder) node.getParent().getViewHolder()).isSelected) {
            mBinding.getRoot().setAlpha(0.5f);
            isSelected = true;
        }
        for (Folder folder : value.allFolderList) {
            if (value.folder.getId() == folder.getFolderId()) {
                TreeNode treeNode = new TreeNode(new IconTreeHolder(folder, value.allFolderList, value.margin + 20, value.listener, value.selectedFolderList)).setViewHolder(new TreeHolderFolder(context));
                node.addChild(treeNode);
            }
        }
        if (node.getChildren().size() != 0) {
            mBinding.arrowIcon.setVisibility(View.VISIBLE);
            mBinding.iconLayout.setOnClickListener(v -> {
                getTreeView().toggleNode(node);
                if (node.isExpanded()) {
                    mBinding.arrowIcon.setImageResource(R.drawable.icon_triangle_down);
                    mBinding.folderIcon.setImageResource(R.drawable.icon_folder_open);
                } else {
                    mBinding.arrowIcon.setImageResource(R.drawable.icon_triangle_right);
                    mBinding.folderIcon.setImageResource(R.drawable.icon_folder);
                }
            });
        } else mBinding.arrowIcon.setVisibility(View.INVISIBLE);
        mBinding.addIcon.setOnClickListener(v -> {
            value.listener.OnFolderAddClick(node, value);
        });

        return mBinding.getRoot();
    }

    public void setClickListener() {
        mBinding.arrowIcon.setVisibility(View.VISIBLE);
        mBinding.iconLayout.setOnClickListener(v -> {
            getTreeView().toggleNode(mNode);
            if (mNode.isExpanded()) {
                mBinding.arrowIcon.setImageResource(R.drawable.icon_triangle_down);
                mBinding.folderIcon.setImageResource(R.drawable.icon_folder_open);
            } else {
                mBinding.arrowIcon.setImageResource(R.drawable.icon_triangle_right);
                mBinding.folderIcon.setImageResource(R.drawable.icon_folder);
            }
        });
    }

    public boolean isSelected() {
        return isSelected;
    }

    public static class IconTreeHolder {
        public Folder folder;
        public List<Folder> allFolderList;
        public int margin;
        public TreeViewAddClick listener;
        public List<Folder> selectedFolderList;

        public IconTreeHolder(Folder folder, List<Folder> allFolderList, int margin, TreeViewAddClick listener, List<Folder> selectedFolderList) {
            this.folder = folder;
            this.allFolderList = allFolderList;
            this.margin = margin;
            this.listener = listener;
            this.selectedFolderList = selectedFolderList;
        }
    }
}
