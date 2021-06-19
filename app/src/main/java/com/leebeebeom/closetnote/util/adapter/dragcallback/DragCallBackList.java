package com.leebeebeom.closetnote.util.adapter.dragcallback;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.leebeebeom.closetnote.util.adapter.BaseAdapter;

import org.jetbrains.annotations.NotNull;

public class DragCallBackList extends BaseDragCallBack {

    public DragCallBackList(BaseAdapter<?, ?, ?> adapter) {
        super(adapter);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
    }

    @Override
    public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        RecyclerView.ViewHolder previousViewHolder = recyclerView.findViewHolderForAdapterPosition(viewHolder.getAbsoluteAdapterPosition() - 1);
        boolean isDraggingUp = dY < 0;
        if (isDraggingUp && previousViewHolder == null) dY = 0;

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}