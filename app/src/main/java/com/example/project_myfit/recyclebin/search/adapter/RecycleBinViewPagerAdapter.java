package com.example.project_myfit.recyclebin.search.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemRecyclerViewBinding;
import com.example.project_myfit.util.adapter.viewholder.ViewPagerVH;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

public class RecycleBinViewPagerAdapter extends RecyclerView.Adapter<ViewPagerVH> {
    private final Object[] mAdapterArray;
    private final DragSelectTouchListener mDragSelectListener;
    private final ViewPagerVH.ViewPagerAutoScrollListener mListener;

    public RecycleBinViewPagerAdapter(Object[] mAdapterArray, DragSelectTouchListener mDragSelectListener, ViewPagerVH.ViewPagerAutoScrollListener mListener) {
        this.mAdapterArray = mAdapterArray;
        this.mDragSelectListener = mDragSelectListener;
        this.mListener = mListener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @NotNull
    @Override
    public ViewPagerVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemRecyclerViewBinding binding = ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewPagerVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewPagerVH holder, int position) {
        if (holder.getBinding().rvItemRv.getAdapter() == null) {
            if (position == 0) {
                RecycleBinCategoryAdapter categoryAdapter = (RecycleBinCategoryAdapter) mAdapterArray[0];
                categoryAdapter.setViewPagerVH(holder);
                holder.getBinding().rvItemRv.setAdapter(categoryAdapter);
                holder.setNoResult(categoryAdapter.getItemCount() == 0);
            } else if (position == 1) {
                RecycleBinFolderAdapter folderAdapter = (RecycleBinFolderAdapter) mAdapterArray[1];
                folderAdapter.setViewPagerVH(holder);
                holder.getBinding().rvItemRv.setAdapter(folderAdapter);
                holder.setNoResult(folderAdapter.getItemCount() == 0);
            } else if (position == 2) {
                RecycleBinSizeAdapter sizeAdapter = (RecycleBinSizeAdapter) mAdapterArray[2];
                sizeAdapter.setViewPagerVH(holder);
                holder.getBinding().rvItemRv.setAdapter(sizeAdapter);
                holder.setNoResult(sizeAdapter.getItemCount() == 0);
            }
            holder.getBinding().rvItemRv.addOnItemTouchListener(mDragSelectListener);
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
