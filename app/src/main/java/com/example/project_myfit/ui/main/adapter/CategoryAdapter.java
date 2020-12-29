package com.example.project_myfit.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.project_myfit.databinding.ItemMainRecyclerBinding;
import com.example.project_myfit.ui.main.MainViewModel;
import com.example.project_myfit.ui.main.database.Category;

import java.util.Collections;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MainRecyclerVH> implements MainDragCallBack.DragFolderListener {
    private List<Category> mCategoryList;
    private OnCategoryClickListener mListener;
    private final ViewBinderHelper mViewBinderHelper = new ViewBinderHelper();
    private MainViewModel mModel;

    public void setItem(List<Category> categoryList, MainViewModel model) {
        mCategoryList = categoryList;
        mModel = model;
    }

    public interface OnCategoryClickListener {
        void onItemClick(Category category);

        void onEditClick(Category category, int position);

        void onDeleteClick(Category category);
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public MainRecyclerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMainRecyclerBinding binding = ItemMainRecyclerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MainRecyclerVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MainRecyclerVH holder, int position) {
        Category category = mCategoryList.get(position);
        holder.binding.setCategory(category);

        mViewBinderHelper.bind(holder.binding.mainSwipeLayout, String.valueOf(category.getId()));
        mViewBinderHelper.setOpenOnlyOne(true);

        holder.binding.mainCardView.setOnClickListener(v -> mListener.onItemClick(category));
        holder.binding.editIcon.setOnClickListener(v -> {
            mListener.onEditClick(mCategoryList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            holder.binding.mainSwipeLayout.close(true);
        });
        holder.binding.deleteIcon.setOnClickListener(v -> {
            mListener.onDeleteClick(mCategoryList.get(holder.getAdapterPosition()));
            holder.binding.mainSwipeLayout.close(true);
        });
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }


    public void updateDiffUtils(List<Category> newCategoryList) {
        CategoryDiffUtil diffUtil = new CategoryDiffUtil(mCategoryList, newCategoryList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil, true);
        mCategoryList.clear();
        mCategoryList.addAll(newCategoryList);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void onItemMove(int from, int to) {
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(mCategoryList, i, i + 1);

                int toOrder = mCategoryList.get(i).getOrderNumber();
                int fromOrder = mCategoryList.get(i + 1).getOrderNumber();
                mCategoryList.get(i).setOrderNumber(fromOrder);
                mCategoryList.get(i + 1).setOrderNumber(toOrder);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(mCategoryList, i, i - 1);

                int toOrder = mCategoryList.get(i).getOrderNumber();
                int fromOrder = mCategoryList.get(i - 1).getOrderNumber();
                mCategoryList.get(i).setOrderNumber(fromOrder);
                mCategoryList.get(i - 1).setOrderNumber(toOrder);
            }
        }
        notifyItemMoved(from, to);
    }


    @Override
    public void onItemDrop() {
        mModel.updateOrder(mCategoryList);
    }

    public static class MainRecyclerVH extends RecyclerView.ViewHolder {
        ItemMainRecyclerBinding binding;

        public MainRecyclerVH(ItemMainRecyclerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
