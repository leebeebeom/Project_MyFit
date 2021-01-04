package com.example.project_myfit.ui.main.listfragment.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.project_myfit.databinding.ItemListRecyclerBinding;
import com.example.project_myfit.ui.main.DragStartListener;
import com.example.project_myfit.ui.main.listfragment.ListViewModel;
import com.example.project_myfit.ui.main.listfragment.database.Size;

import java.util.Collections;
import java.util.List;

public class ListSizeAdapter extends RecyclerView.Adapter<ListSizeAdapter.SizeVH> {
    private List<Size> mSizeList;
    private OnSizeClickListener mListener;
    private final ViewBinderHelper mBinderHelper = new ViewBinderHelper();
    private ListViewModel mModel;
    private DragStartListener mDragStartListener;

    public void setItem(List<Size> sizeList, ListViewModel model, DragStartListener listener) {
        mSizeList = sizeList;
        mModel = model;
        mDragStartListener = listener;
    }

    public void onItemMove(int from, int to) {
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(mSizeList, i, i + 1);

                int toOrder = mSizeList.get(i).getOrderNumberSize();
                int fromOrder = mSizeList.get(i + 1).getOrderNumberSize();
                mSizeList.get(i).setOrderNumberSize(fromOrder);
                mSizeList.get(i + 1).setOrderNumberSize(toOrder);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(mSizeList, i, i - 1);

                int toOrder = mSizeList.get(i).getOrderNumberSize();
                int fromOrder = mSizeList.get(i - 1).getOrderNumberSize();
                mSizeList.get(i).setOrderNumberSize(fromOrder);
                mSizeList.get(i - 1).setOrderNumberSize(toOrder);
            }
        }
        notifyItemMoved(from, to);
    }

    public void onItemDrop() {
        mModel.updateSizeOrder(mSizeList);
    }

    public interface OnSizeClickListener {
        void onItemClick(Size size);

        void onDeleteClick(Size size);
    }

    public void setOnSizeClickListener(OnSizeClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public SizeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListRecyclerBinding binding = ItemListRecyclerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SizeVH(binding);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull SizeVH holder, int position) {
        Size size = mSizeList.get(position);
        mBinderHelper.bind(holder.binding.listSwipeLayout, String.valueOf(size.getId()));
        mBinderHelper.setOpenOnlyOne(true);
        holder.binding.setSize(size);
        holder.binding.listCardView.setOnClickListener(v -> mListener.onItemClick(mSizeList.get(holder.getAdapterPosition())));
        holder.binding.listDeleteIcon.setOnClickListener(v -> mListener.onDeleteClick(mSizeList.get(holder.getAdapterPosition())));
        holder.binding.listDragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mDragStartListener.startDrag(holder);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mSizeList.size();
    }

    public void updateDiffUtils(List<Size> newSizeList) {
        SizeDiffUtil diffUtil = new SizeDiffUtil(mSizeList, newSizeList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil, true);
        mSizeList.clear();
        mSizeList.addAll(newSizeList);
        diffResult.dispatchUpdatesTo(this);
    }

    public static class SizeVH extends RecyclerView.ViewHolder {
        ItemListRecyclerBinding binding;

        public SizeVH(ItemListRecyclerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
