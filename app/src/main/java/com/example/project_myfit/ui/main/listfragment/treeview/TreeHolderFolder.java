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

    public TreeHolderFolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, IconTreeHolder value) {
        ItemTreeFolderBinding binding = ItemTreeFolderBinding.inflate(LayoutInflater.from(context));
        binding.text.setText(value.folder.getFolderName());
        LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) binding.arrowIcon.getLayoutParams();
        params.leftMargin = value.margin;
        for (Folder folder : value.allFolderList) {
            if (value.folder.getId() == folder.getFolderId()) {
                TreeNode treeNode = new TreeNode(new IconTreeHolder(folder, value.allFolderList, value.margin + 20, value.listener)).setViewHolder(new TreeHolderFolder(context));
                node.addChild(treeNode);
            }
        }
        if (node.getChildren().size() != 0) {
            binding.arrowIcon.setVisibility(View.VISIBLE);
            binding.iconLayout.setOnClickListener(v -> {
                getTreeView().toggleNode(node);
                if (node.isExpanded()) {
                    binding.arrowIcon.setImageResource(R.drawable.icon_triangle_down);
                    binding.folderIcon.setImageResource(R.drawable.icon_folder_open);
                } else {
                    binding.arrowIcon.setImageResource(R.drawable.icon_triangle_right);
                    binding.folderIcon.setImageResource(R.drawable.icon_folder);
                }
            });
        } else binding.arrowIcon.setVisibility(View.INVISIBLE);
        binding.addIcon.setOnClickListener(v -> value.listener.onAddClick(node, value.folder.getId()));

        return binding.getRoot();
    }

    public static class IconTreeHolder {
        public Folder folder;
        public List<Folder> allFolderList;
        public int margin;
        public TreeViewAddClickListener listener;

        public IconTreeHolder(Folder folder, List<Folder> allFolderList, int margin, TreeViewAddClickListener listener) {
            this.folder = folder;
            this.allFolderList = allFolderList;
            this.margin = margin;
            this.listener = listener;
        }
    }
}
