package com.leebeebeom.closetnote.ui.main.main.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.leebeebeom.closetnote.data.tuple.tuple.CategoryTuple;
import com.leebeebeom.closetnote.databinding.ItemVhCategoryBinding;
import com.leebeebeom.closetnote.util.SizeLiveSet;
import com.leebeebeom.closetnote.util.SortUtil;
import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
import com.leebeebeom.closetnote.util.adapter.viewholder.BaseVHListener;
import com.leebeebeom.closetnote.util.adapter.viewholder.CategoryVH;
import com.leebeebeom.closetnote.util.constant.Sort;

import org.jetbrains.annotations.NotNull;

public class CategoryAdapter extends BaseAdapter<CategoryTuple, BaseVHListener, CategoryVH> {

    public CategoryAdapter(SizeLiveSet<CategoryTuple> selectedCategoryTuples, BaseVHListener listener) {
        super(selectedCategoryTuples, listener);
    }

    @Override
    public void setSort(Sort sort) {
        if (mSort != sort) {
            mSort = sort;
            SortUtil.sortCategoryFolderTuples(sort, mNewOrderList);
            submitList(mNewOrderList);
        }
    }

    @Override
    protected CategoryVH getViewHolder(@NotNull ViewGroup parent, BaseVHListener listener, SizeLiveSet<CategoryTuple> selectedItems) {
        ItemVhCategoryBinding binding = ItemVhCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryVH(binding, listener, selectedItems);
    }
}
