package com.example.myfit.util.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.databinding.ItemRecyclerViewBinding;
import com.example.myfit.ui.main.main.adapter.CategoryAdapter;
import com.example.myfit.ui.main.main.listener.MainAutoScrollImpl;
import com.example.myfit.util.adapter.viewholder.ViewPagerVH;
import com.example.myfit.util.dragselect.DragSelect;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.scopes.FragmentScoped;

public class BaseViewPagerAdapter<T extends BaseAdapter<?, ?, ?>> extends RecyclerView.Adapter<ViewPagerVH> {
    private final ViewPagerVH.AutoScrollListener mListener;
    private final T[] mAdapters;
    private final DragSelect[] mDragSelectListeners;
    private ItemTouchHelper[] mItemTouchHelpers;

    public BaseViewPagerAdapter(ViewPagerVH.AutoScrollListener listener,
                                T[] adapters,
                                DragSelect[] dragSelectListeners,
                                ItemTouchHelper[] itemTouchHelpers) {
        this.mListener = listener;
        this.mAdapters = adapters;
        this.mDragSelectListeners = dragSelectListeners;
        this.mItemTouchHelpers = itemTouchHelpers;
        setHasStableIds(true);
    }

    public BaseViewPagerAdapter(ViewPagerVH.AutoScrollListener listener,
                                T[] adapters,
                                DragSelect[] dragSelectListeners) {
        this.mListener = listener;
        this.mAdapters = adapters;
        this.mDragSelectListeners = dragSelectListeners;
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
        if (holder.getAdapter() == null) {
            holder.setAdapter(mAdapters[position]);
            holder.addItemTouchListener(mDragSelectListeners[position]);
            if (mItemTouchHelpers != null)
                mItemTouchHelpers[position].attachToRecyclerView(holder.getRecyclerView());
        }
    }

    @Override
    public int getItemCount() {
        return mAdapters.length;
    }

    @FragmentScoped
    public static class MainViewPagerAdapter extends BaseViewPagerAdapter<CategoryAdapter> {
        @Inject
        public MainViewPagerAdapter(MainAutoScrollImpl listener,
                                    CategoryAdapter[] adapters,
                                    DragSelect[] dragSelectListeners,
                                    ItemTouchHelper[] itemTouchHelpers) {
            super(listener, adapters, dragSelectListeners, itemTouchHelpers);
        }
    }
}
