package com.example.myfit.ui.dialog.tree.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.navigation.NavController;

import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.databinding.ItemTreeCategoryBinding;
import com.example.myfit.databinding.ItemTreePrefixBinding;
import com.example.myfit.ui.dialog.tree.BaseTreeValue;
import com.unnamed.b.atv.model.TreeNode;

import java.util.List;

public class TreeCategoryHolder extends BaseTreeHolder<CategoryTuple> {
    private ItemTreeCategoryBinding mBinding;

    public TreeCategoryHolder(Context context, NavController navController) {
        super(context, navController);
    }

    @Override
    protected void bind() {
        mBinding = ItemTreeCategoryBinding.inflate(LayoutInflater.from(context));
        mBinding.setCategoryHolder(this);
    }

    @Override
    protected LinearLayoutCompat getFolderIconLayout() {
        return mBinding.prefix.layout;
    }

    @Override
    protected TextView getNameTextView() {
        return mBinding.tv.tv;
    }

    @Override
    protected AppCompatImageView getArrowIcon() {
        return mBinding.prefix.iconArrow;
    }

    @Override
    protected TextView getCurrentPosition() {
        return mBinding.postfix.tvCurrentPosition;
    }

    @Override
    public TextView getContentSize() {
        return mBinding.getCategoryHolder().getContentSize();
    }

    @Override
    public CategoryTuple getTuple() {
        return ((BaseTreeValue.CategoryValue) getNode().getValue()).getTuple();
    }

    @Override
    protected ItemTreePrefixBinding getPrefix() {
        return mBinding.prefix;
    }

    @Override
    protected View getBindingRoot() {
        return mBinding.getRoot();
    }

    @Override
    public void addChild(List<TreeNode> folderNodes) {
        folderNodes.stream()
                .filter(folderNode -> ((BaseTreeValue.FolderValue) folderNode.getValue()).getTuple().getParentId() == getTupleId())
                .forEach(folderNode -> mNode.addChild(folderNode));
    }
}
