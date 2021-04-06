package com.example.project_myfit.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemTreeCategoryBinding;
import com.example.project_myfit.util.AdapterUtil;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TreeHolderCategory extends TreeNode.BaseNodeViewHolder<TreeHolderCategory.CategoryTreeHolder> {
    //all checked
    private ItemTreeCategoryBinding mBinding;
    private boolean mIsClickable;

    public TreeHolderCategory(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, @NotNull CategoryTreeHolder value) {
        //checked
        mBinding = ItemTreeCategoryBinding.inflate(LayoutInflater.from(context));
        mBinding.setCategory(value.category);

        mBinding.contentsSize.setText(String.valueOf(new AdapterUtil(context).getCategoryContentsSize(value.category,
                value.folderFolderIdList, value.sizeFolderIdList)));

        //선택된 폴더가 이 카테고리노드라면
        if (!value.selectedFolderList.isEmpty()) {
            Folder selectedFolder = value.selectedFolderList.get(0);
            if (selectedFolder.getFolderId() == value.category.getId())
                setAlpha();
        }

        //선택된 사이즈 카테고리 텍스트 알파
        if (!value.selectedSizeList.isEmpty()) {
            Size selectedSize = value.selectedSizeList.get(0);
            if (selectedSize.getFolderId() == value.category.getId())
                setAlpha();
        }

        //expandable
        if (node.getChildren().size() != 0)
            mBinding.iconLayout.setOnClickListener(v -> tView.toggleNode(node));
        else mBinding.arrowIconCategory.setVisibility(View.INVISIBLE);

        mBinding.addIcon.setOnClickListener(v -> value.listener.treeViewCategoryFolderAddClick(node, value));

        return mBinding.getRoot();
    }

    public void setAlpha() {
        if (mBinding.iconLayout.getAlpha() != 0.5f && mBinding.text.getAlpha() != 0.5f) {
            mIsClickable = false;
            mBinding.iconLayout.setAlpha(0.5f);
            mBinding.text.setAlpha(0.5f);
        }
    }

    public boolean isClickable() {
        return mIsClickable;
    }

    public void setIconClickable() {
        //checked
        mBinding.arrowIconCategory.setVisibility(View.VISIBLE);
        mBinding.iconLayout.setOnClickListener(v -> tView.toggleNode(mNode));
    }

    public ItemTreeCategoryBinding getBinding() {
        return mBinding;
    }

    @Override
    public void toggle(boolean active) {
        //checked
        mBinding.arrowIconCategory.setImageResource(active ? R.drawable.icon_triangle_down : R.drawable.icon_triangle_right);
        mBinding.folderIconCategory.setImageResource(active ? R.drawable.icon_folder_open : R.drawable.icon_folder);
    }

    public static class CategoryTreeHolder {
        //checked
        public Category category;
        public TreeViewCategoryFolderAddListener listener;
        public List<Size> selectedSizeList;
        public List<Folder> selectedFolderList;
        public List<Long> folderFolderIdList, sizeFolderIdList;

        public CategoryTreeHolder(Category category, TreeViewCategoryFolderAddListener listener,
                                  List<Folder> selectedFolderList, List<Size> selectedSizeList,
                                  List<Long> folderFolderIdList, List<Long> sizeFolderIdList) {
            this.category = category;
            this.listener = listener;
            this.selectedFolderList = selectedFolderList;
            this.selectedSizeList = selectedSizeList;
            this.folderFolderIdList = folderFolderIdList;
            this.sizeFolderIdList = sizeFolderIdList;
        }
    }

    public interface TreeViewCategoryFolderAddListener {
        //checked
        void treeViewCategoryFolderAddClick(TreeNode node, TreeHolderCategory.CategoryTreeHolder value);
    }
}
