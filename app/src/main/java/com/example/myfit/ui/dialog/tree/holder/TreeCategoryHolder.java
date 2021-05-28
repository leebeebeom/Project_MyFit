package com.example.myfit.ui.dialog.tree.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.navigation.NavController;

import com.example.myfit.databinding.ItemTreeCategoryBinding;
import com.example.myfit.ui.dialog.tree.holder.value.CategoryValue;

public class TreeCategoryHolder extends BaseTreeHolder<CategoryValue> {
    private ItemTreeCategoryBinding mBinding;

    public TreeCategoryHolder(Context context, NavController navController) {
        super(context, navController);
    }

    @Override
    protected void bind(CategoryValue value) {
        mBinding = ItemTreeCategoryBinding.inflate(LayoutInflater.from(context));
        mBinding.setCategoryTuple(value.getTuple());
    }

    @Override
    protected LinearLayoutCompat getFolderIconLayout() {
        return mBinding.layoutFolderIcon;
    }

    @Override
    protected TextView getNameTextView() {
        return mBinding.tvCategoryName;
    }

    @Override
    protected AppCompatImageView getArrowIcon() {
        return mBinding.iconArrow;
    }

    @Override
    protected AppCompatImageView getAddIcon() {
        return mBinding.iconAdd;
    }

    @Override
    protected AppCompatImageView getFolderIcon() {
        return mBinding.iconFolder;
    }

    @Override
    protected TextView getCurrentPosition() {
        return mBinding.tvCurrentPosition;
    }

    @Override
    public TextView getContentsSize() {
        return mBinding.tvContentsSize;
    }

    @Override
    protected View getBindingRoot() {
        return mBinding.getRoot();
    }
}
