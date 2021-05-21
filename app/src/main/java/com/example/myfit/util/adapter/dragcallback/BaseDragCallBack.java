package com.example.myfit.util.adapter.dragcallback;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.data.model.tuple.BaseTuple;
import com.example.myfit.util.adapter.BaseAdapter;
import com.example.myfit.util.adapter.viewholder.BaseVH;

public abstract  class BaseDragCallBack<T extends BaseTuple, VH extends BaseVH<T>> extends ItemTouchHelper.Callback {
    private final BaseAdapter<T, VH> adapter;

    public BaseDragCallBack(BaseAdapter<T, VH> adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        adapter.moveItem(viewHolder.getAbsoluteAdapterPosition(), target.getAbsoluteAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof BaseVH) ((BaseVH<?>) viewHolder).dragStop();
    }


}
