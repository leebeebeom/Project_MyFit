package com.example.project_myfit.ui.main.listfragment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemListFragmentFolderBinding;
import com.example.project_myfit.ui.main.listfragment.database.ListFolder;

import java.util.List;

public class ListFolderAdapter extends RecyclerView.Adapter<ListFolderAdapter.ListFolderVH> {
    List<ListFolder> mItem;

    public void setItem(List<ListFolder> listFolders) {
        mItem = listFolders;
    }

    @NonNull
    @Override
    public ListFolderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListFragmentFolderBinding binding = ItemListFragmentFolderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ListFolderVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ListFolderVH holder, int position) {
        ListFolder listFolder = mItem.get(position);
        holder.binding.setListFolder(listFolder);
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    public static class ListFolderVH extends RecyclerView.ViewHolder {
        ItemListFragmentFolderBinding binding;

        public ListFolderVH(ItemListFragmentFolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
