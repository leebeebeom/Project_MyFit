package com.example.myfit.util.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.databinding.ItemRecyclerViewBinding;
import com.example.myfit.util.DragSelectImpl;
import com.example.myfit.util.adapter.viewholder.ViewPagerVH;

import org.jetbrains.annotations.NotNull;

public class BaseViewPagerAdapter extends RecyclerView.Adapter<ViewPagerVH> {
    private final ViewPagerVH.ViewPagerAutoScrollListener listener;
    private final BaseAdapter<?, ?, ?>[] adapters;
    private final DragSelectImpl[] dragSelectListeners;
    private final ItemTouchHelper[] itemTouchHelpers;

    public BaseViewPagerAdapter(ViewPagerVH.ViewPagerAutoScrollListener listener,
                                BaseAdapter<?, ?, ?>[] adapters,
                                DragSelectImpl[] dragSelectListeners,
                                ItemTouchHelper[] itemTouchHelpers) {
        this.listener = listener;
        this.adapters = adapters;
        this.dragSelectListeners = dragSelectListeners;
        this.itemTouchHelpers = itemTouchHelpers;
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
        return new ViewPagerVH(binding, listener);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewPagerVH holder, int position) {
        if (holder.getAdapter() == null) {
            holder.setAdapter(adapters[position]);
            holder.addItemTouchListener(dragSelectListeners[position]);
            itemTouchHelpers[position].attachToRecyclerView(holder.getRecyclerView());
        }
    }

    @Override
    public int getItemCount() {
        return adapters.length;
    }
}
