package com.example.project_myfit.search.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemRecyclerViewBinding;
import com.example.project_myfit.util.DragSelectImpl;
import com.example.project_myfit.util.adapter.viewholder.ViewPagerVH;

import org.jetbrains.annotations.NotNull;

public class SearchViewPagerAdapter extends RecyclerView.Adapter<ViewPagerVH> {
    private final ConcatAdapter[] mConcatAdapterArray;
    private final DragSelectImpl[] mDragSelectArray;
    private final ViewPagerVH.ViewPagerAutoScrollListener mListener;

    public SearchViewPagerAdapter(ConcatAdapter[] adapterArray, DragSelectImpl[] dragSelectArray,
                                  ViewPagerVH.ViewPagerAutoScrollListener listener) {
        this.mConcatAdapterArray = adapterArray;
        this.mDragSelectArray = dragSelectArray;
        this.mListener = listener;
        setHasStableIds(true);
    }

    @NonNull
    @NotNull
    @Override
    public ViewPagerVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemRecyclerViewBinding binding = ItemRecyclerViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewPagerVH(binding, mListener);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewPagerVH holder, int position) {
        if (holder.getBinding().rv.getAdapter() == null) {
            holder.getBinding().rv.setAdapter(mConcatAdapterArray[position]);

            mDragSelectArray[position].setRecyclerView(holder.getBinding().rv);
            holder.getBinding().rv.addOnItemTouchListener(mDragSelectArray[position]);
            holder.setNoResult(mConcatAdapterArray[position].getAdapters().isEmpty());
        }
    }
}
