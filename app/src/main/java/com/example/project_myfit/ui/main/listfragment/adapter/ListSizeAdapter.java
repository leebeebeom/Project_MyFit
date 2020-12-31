package com.example.project_myfit.ui.main.listfragment.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemListRecyclerBinding;
import com.example.project_myfit.ui.main.listfragment.database.Size;

import java.util.List;

public class ListSizeAdapter extends RecyclerView.Adapter<ListSizeAdapter.SizeVH> {
    private List<Size> mSizeList;
    private OnSizeClickListener mListener;

    public void setItem(List<Size> sizeList) {
        mSizeList = sizeList;
    }

    public interface OnSizeClickListener {
        void onItemClick(Size size);

        void onEditClick(Size size, int position);

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

    @Override
    public void onBindViewHolder(@NonNull SizeVH holder, int position) {
        Size size = mSizeList.get(position);
        holder.binding.setSize(size);
        holder.binding.listCardView.setOnClickListener(v -> mListener.onItemClick(mSizeList.get(holder.getAdapterPosition())));
        holder.binding.listEditIcon.setOnClickListener(v -> mListener.onEditClick(mSizeList.get(holder.getAdapterPosition()), position));
        holder.binding.listDeleteIcon.setOnClickListener(v -> mListener.onDeleteClick(mSizeList.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return mSizeList.size();
    }

    public static class SizeVH extends RecyclerView.ViewHolder {
        ItemListRecyclerBinding binding;

        public SizeVH(ItemListRecyclerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
