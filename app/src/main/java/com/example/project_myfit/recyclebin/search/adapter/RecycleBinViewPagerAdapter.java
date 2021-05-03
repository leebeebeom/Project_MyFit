package com.example.project_myfit.recyclebin.search.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemRecyclerViewBinding;
import com.example.project_myfit.util.DragSelectImpl;
import com.example.project_myfit.util.adapter.ParentAdapter;
import com.example.project_myfit.util.adapter.viewholder.ViewPagerVH;

import org.jetbrains.annotations.NotNull;

public class RecycleBinViewPagerAdapter extends RecyclerView.Adapter<ViewPagerVH> {
    private final ParentAdapter<?, ?>[] mAdapterArray;
    private final DragSelectImpl[] mDragSelectArray;
    private final ViewPagerVH.ViewPagerAutoScrollListener mListener;

    public RecycleBinViewPagerAdapter(ParentAdapter<?, ?>[] mAdapterArray, DragSelectImpl[] dragSelectArray, ViewPagerVH.ViewPagerAutoScrollListener mListener) {
        this.mAdapterArray = mAdapterArray;
        this.mDragSelectArray = dragSelectArray;
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
        if (holder.getBinding().rv.getAdapter() == null) {
            holder.getBinding().rv.setAdapter(mAdapterArray[position]);
            mDragSelectArray[position].setRecyclerView(holder.getBinding().rv);

            holder.getBinding().rv.addOnItemTouchListener(mDragSelectArray[position]);
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
