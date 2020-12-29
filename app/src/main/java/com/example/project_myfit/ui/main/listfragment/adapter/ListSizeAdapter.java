package com.example.project_myfit.ui.main.listfragment.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemListRecyclerBinding;
import com.example.project_myfit.ui.main.listfragment.database.Size;

import java.util.List;

public class ListSizeAdapter extends RecyclerView.Adapter<ListSizeAdapter.SizeVH> {
    List<Size> mItem;

    public void setItem(List<Size> sizeList) {
        mItem = sizeList;
    }

    @NonNull
    @Override
    public SizeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListRecyclerBinding binding = ItemListRecyclerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SizeVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeVH holder, int position) {
        Size size = mItem.get(position);
        holder.binding.setSize(size);
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    public static class SizeVH extends RecyclerView.ViewHolder {
        ItemListRecyclerBinding binding;

        public SizeVH(ItemListRecyclerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
