package com.example.myfit.util.adapter.dragcallback;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.util.adapter.BaseAdapter;
import com.example.myfit.util.adapter.viewholder.BaseVH;

import org.jetbrains.annotations.NotNull;

public abstract class BaseDragCallBack extends ItemTouchHelper.Callback {
    private final BaseAdapter<?, ?, ?> mAdapter;

    public BaseDragCallBack(BaseAdapter<?, ?, ?> adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mAdapter.moveItem(viewHolder.getAbsoluteAdapterPosition(), target.getAbsoluteAdapterPosition());
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void clearView(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof BaseVH) ((BaseVH<?, ?>) viewHolder).dragStop();
    }
}
