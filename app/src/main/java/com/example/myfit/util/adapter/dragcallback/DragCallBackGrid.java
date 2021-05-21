package com.example.myfit.util.adapter.dragcallback;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.data.model.tuple.BaseTuple;
import com.example.myfit.util.adapter.BaseAdapter;
import com.example.myfit.util.adapter.viewholder.BaseVH;

import org.jetbrains.annotations.NotNull;

public class DragCallBackGrid<T extends BaseTuple, VH extends BaseVH<T>> extends BaseDragCallBack<T, VH> {

    public DragCallBackGrid(BaseAdapter<T, VH> adapter) {
        super(adapter);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0);
    }


    @Override
    public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        RecyclerView.ViewHolder previousViewHolder = recyclerView.findViewHolderForAdapterPosition(viewHolder.getLayoutPosition() - 1);
        boolean isDraggingUp = dY < 0;

        if (isDraggingUp && (previousViewHolder == null || previousViewHolder.getLayoutPosition() == 0))
            dY = 0;

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
