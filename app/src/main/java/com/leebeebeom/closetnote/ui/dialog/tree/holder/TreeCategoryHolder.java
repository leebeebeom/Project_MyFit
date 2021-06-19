package com.leebeebeom.closetnote.ui.dialog.tree.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.navigation.NavController;

import com.leebeebeom.closetnote.data.tuple.tuple.CategoryTuple;
import com.leebeebeom.closetnote.databinding.ItemTreeCategoryBinding;
import com.leebeebeom.closetnote.databinding.ItemTreePostfixBinding;
import com.leebeebeom.closetnote.databinding.ItemTreePrefixBinding;
import com.leebeebeom.closetnote.ui.dialog.tree.BaseTreeValue;

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
    public CategoryTuple getTuple() {
        return ((BaseTreeValue.CategoryValue) getNode().getValue()).getTuple();
    }

    @Override
    protected ItemTreePrefixBinding getPrefix() {
        return mBinding.prefix;
    }

    @Override
    protected TextView getNameTextView() {
        return mBinding.tv.tv;
    }

    @Override
    public ItemTreePostfixBinding getPostFix() {
        return mBinding.postfix;
    }

    @Override
    protected View getBindingRoot() {
        return mBinding.getRoot();
    }
}
