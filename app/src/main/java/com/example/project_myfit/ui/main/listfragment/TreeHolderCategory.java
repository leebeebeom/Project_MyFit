package com.example.project_myfit.ui.main.listfragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemTreeCategoryBinding;
import com.unnamed.b.atv.model.TreeNode;

public class TreeHolderCategory extends TreeNode.BaseNodeViewHolder<TreeHolderCategory.IconTreeHolder> {

    public TreeHolderCategory(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, IconTreeHolder value) {
        ItemTreeCategoryBinding binding = ItemTreeCategoryBinding.inflate(LayoutInflater.from(context));
        binding.text.setText(value.text);
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
        binding.addIcon.setOnClickListener(v -> {
            //TODO
        });

        return binding.getRoot();
    }

    public static class IconTreeHolder {
        public String text;

        public IconTreeHolder(String text) {
            this.text = text;
        }
    }
}
