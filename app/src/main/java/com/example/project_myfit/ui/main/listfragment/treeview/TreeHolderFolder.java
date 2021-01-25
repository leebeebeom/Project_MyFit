package com.example.project_myfit.ui.main.listfragment.treeview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemTreeFolderBinding;
import com.example.project_myfit.dialog.TreeViewDialog;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.unnamed.b.atv.model.TreeNode;

import java.util.List;

public class TreeHolderFolder extends TreeNode.BaseNodeViewHolder<TreeHolderFolder.FolderTreeHolder> {

    private ItemTreeFolderBinding mBinding;
    private boolean isSelected;

    public TreeHolderFolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, FolderTreeHolder value) {
        mBinding = ItemTreeFolderBinding.inflate(LayoutInflater.from(context));
        mBinding.text.setText(value.folder.getFolderName());

        LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) mBinding.arrowIcon.getLayoutParams();
        params.leftMargin = value.margin;

        for (Folder f : value.selectedFolderList) {
            if (f.getId() == value.folder.getId()) {
                TreeNode parent = node.getParent();
                if (parent.getViewHolder() instanceof TreeHolderCategory) {
                    TreeHolderCategory holder = (TreeHolderCategory) parent.getViewHolder();
                    holder.getBinding().getRoot().setAlpha(0.5f);
                } else {
                    TreeHolderFolder holder = (TreeHolderFolder) parent.getViewHolder();
                    holder.getBinding().getRoot().setAlpha(0.5f);
                }
                mBinding.getRoot().setAlpha(0.5f);
                isSelected = true;
                break;
            }
        }

        //if parent folder is selected folder
        if (node.getParent().getViewHolder() instanceof TreeHolderFolder && ((TreeHolderFolder) node.getParent().getViewHolder()).isSelected) {
            mBinding.getRoot().setAlpha(0.5f);
            isSelected = true;
        }

        for (Folder folder : value.allFolderList) {
            if (value.folder.getId() == folder.getFolderId()) {
                TreeNode treeNode = new TreeNode(new FolderTreeHolder(folder, value.allFolderList, value.margin + 20, value.listener, value.selectedFolderList)).setViewHolder(new TreeHolderFolder(context));
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

    public boolean isSelected() {
        return isSelected;
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
        public Folder folder;
        public List<Folder> allFolderList;
        public int margin;
        public TreeViewDialog.TreeViewAddClick listener;
        public List<Folder> selectedFolderList;


        public FolderTreeHolder(Folder folder, List<Folder> allFolderList, int margin, TreeViewDialog.TreeViewAddClick listener, List<Folder> selectedFolderList) {
            this.folder = folder;
            this.allFolderList = allFolderList;
            this.margin = margin;
            this.listener = listener;
            this.selectedFolderList = selectedFolderList;
        }
    }
}


