package com.example.myfit.ui.main.main.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.databinding.ItemVhCategoryBinding;
import com.example.myfit.util.SizeLiveSet;
import com.example.myfit.util.adapter.BaseAdapter;
import com.example.myfit.util.adapter.viewholder.BaseVHListener;
import com.example.myfit.util.adapter.viewholder.CategoryVH;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import javax.inject.Inject;

import lombok.NonNull;

public class CategoryAdapter extends BaseAdapter<CategoryTuple, BaseVHListener, CategoryVH> implements Cloneable {
    @Inject
    public CategoryAdapter(SizeLiveSet<CategoryTuple> selectedItems) {
        super(selectedItems);
    }

    @Override
    protected CategoryVH getViewHolder(@NotNull ViewGroup parent, BaseVHListener listener, Set<CategoryTuple> selectedItems) {
        ItemVhCategoryBinding binding = ItemVhCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryVH(binding, listener, selectedItems);
    }

    @NonNull
    @NotNull
    @Override
    public CategoryAdapter clone() throws CloneNotSupportedException {
        return (CategoryAdapter) super.clone();
    }
}
