package com.example.myfit.ui.dialog.tree.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.navigation.NavController;

import com.example.myfit.data.model.category.CategoryTuple;
import com.example.myfit.databinding.ItemTreeCategoryBinding;
import com.example.myfit.ui.dialog.tree.holder.value.CategoryValue;

public class TreeCategoryHolder extends BaseTreeHolder<CategoryTuple, CategoryValue> {
    private ItemTreeCategoryBinding binding;

    public TreeCategoryHolder(Context context, NavController navController) {
        super(context, navController);
    }

    @Override
    protected void bind(CategoryTuple tuple) {
        binding = ItemTreeCategoryBinding.inflate(LayoutInflater.from(context));
        binding.setCategoryTuple(tuple);
    }

    @Override
    protected LinearLayoutCompat getFolderIconLayout() {
        return binding.layoutFolderIcon;
    }

    @Override
    protected TextView getNameTextView() {
        return binding.tvCategoryName;
    }

    @Override
    protected AppCompatImageView getArrowIcon() {
        return binding.iconArrow;
    }

    @Override
    protected AppCompatImageView getAddIcon() {
        return binding.iconAdd;
    }

    @Override
    protected AppCompatImageView getFolderIcon() {
        return binding.iconFolder;
    }

    @Override
    protected TextView getCurrentPosition() {
        return binding.tvCurrentPosition;
    }

    @Override
    protected View getBindingRoot() {
        return binding.getRoot();
    }
}
