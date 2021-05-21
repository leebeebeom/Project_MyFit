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

public class TreeCategoryHolder extends BaseTreeHolder<TreeCategoryHolder.TreeCategoryValue, CategoryTuple> {
    private ItemTreeCategoryBinding binding;

    public TreeCategoryHolder(Context context, NavController navController) {
        super(context, navController);
    }

    @Override
    protected CategoryTuple getTuple() {
        return ((TreeCategoryValue) mNode.getValue()).categoryTuple;
    }

    @Override
    protected void bind() {
        binding = ItemTreeCategoryBinding.inflate(LayoutInflater.from(context));
        binding.setCategoryTuple(getTuple());
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

    public static class TreeCategoryValue {
        private final CategoryTuple categoryTuple;

        public TreeCategoryValue(CategoryTuple categoryTuple) {
            this.categoryTuple = categoryTuple;
        }
    }
}
