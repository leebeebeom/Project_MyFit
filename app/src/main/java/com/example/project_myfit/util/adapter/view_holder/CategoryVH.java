package com.example.project_myfit.util.adapter.view_holder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.databinding.ItemMainRecyclerCategoryBinding;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

public class CategoryVH extends RecyclerView.ViewHolder {
    private final ItemMainRecyclerCategoryBinding mBinding;
    private Category mCategory;

    public CategoryVH(@NotNull ItemMainRecyclerCategoryBinding binding, CategoryVHListener listener) {
        super(binding.getRoot());
        this.mBinding = binding;

        itemView.setOnClickListener(v -> listener.onCategoryCardViewClick(mCategory, mBinding.itemMainCheckBox));

        itemView.setOnLongClickListener(v -> {
            listener.onCategoryCardViewLongClick(getLayoutPosition());
            return false;
        });
    }

    public void setCategory(Category category) {
        this.mCategory = category;
        mBinding.setCategory(category);
    }

    public ItemMainRecyclerCategoryBinding getBinding() {
        return mBinding;
    }

    public interface CategoryVHListener {
        void onCategoryCardViewClick(Category category, MaterialCheckBox checkBox);

        void onCategoryCardViewLongClick(int position);

        void onCategoryDragHandleTouch(RecyclerView.ViewHolder viewHolder);
    }
}