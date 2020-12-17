package com.example.project_myfit.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.project_myfit.databinding.ItemMainFragmentChildBinding;
import com.example.project_myfit.databinding.ItemMainFragmentParentBinding;
import com.example.project_myfit.ui.main.database.ChildCategory;
import com.example.project_myfit.ui.main.database.ParentCategory;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

public class CategoryAdapter extends ExpandableRecyclerViewAdapter<CategoryAdapter.ParentCategoryVH, CategoryAdapter.ChildCategoryVH> {

    public CategoryAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    public interface OnItemClickListener {
        void onAddIconClicked(ParentCategory parentCategory);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public ParentCategoryVH onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        ItemMainFragmentParentBinding binding = ItemMainFragmentParentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ParentCategoryVH(binding);
    }

    @Override
    public ChildCategoryVH onCreateChildViewHolder(ViewGroup parent, int viewType) {
        ItemMainFragmentChildBinding binding = ItemMainFragmentChildBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChildCategoryVH(binding);
    }

    @Override
    public void onBindChildViewHolder(ChildCategoryVH holder, int flatPosition, ExpandableGroup group, int childIndex) {
        holder.binding.setChildCategory((ChildCategory) group.getItems().get(childIndex));
    }

    @Override
    public void onBindGroupViewHolder(ParentCategoryVH holder, int flatPosition, ExpandableGroup group) {
        holder.binding.setParentCategory((ParentCategory) group);
        holder.binding.addIcon.setOnClickListener(v -> mListener.onAddIconClicked((ParentCategory) group));
    }

    //부모 뷰홀더
    public static class ParentCategoryVH extends GroupViewHolder {
        ItemMainFragmentParentBinding binding;

        public ParentCategoryVH(ItemMainFragmentParentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    //자식 뷰홀더
    public static class ChildCategoryVH extends ChildViewHolder {
        ItemMainFragmentChildBinding binding;

        public ChildCategoryVH(ItemMainFragmentChildBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
