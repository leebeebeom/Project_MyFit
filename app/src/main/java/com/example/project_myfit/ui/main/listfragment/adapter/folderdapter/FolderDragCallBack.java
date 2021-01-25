package com.example.project_myfit.ui.main.listfragment.adapter.folderdapter;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class FolderDragCallBack extends ItemTouchHelper.Callback {
    private final FolderAdapter mAdapter;

    public FolderDragCallBack(FolderAdapter listFolderAdapter) {
        mAdapter = listFolderAdapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
            viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
            viewHolder) {
        mAdapter.onItemDrop(viewHolder);
    }

    @Override
    public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        boolean isDraggingUp = dY < 0;
        boolean isDraggingDown = dY > 0;

        int itemCount = 0;
        if (recyclerView.getAdapter() != null) itemCount = recyclerView.getAdapter().getItemCount();
        int position = viewHolder.getLayoutPosition();

        if (isDraggingDown) {
            if (itemCount % 4 == 0 && itemCount - 5 < position)
                dY = 0;
            else if (itemCount % 4 == 1 && itemCount - 1 == position)
                dY = 0;
            else if (itemCount % 4 == 2 && itemCount - 3 < position)
                dY = 0;
            else if (itemCount % 4 == 3 && itemCount - 4 < position)
                dY = 0;
            else if (viewHolder.itemView.getY() > recyclerView.getBottom() - 380)
                dY = 0;
        } else if (isDraggingUp && position < 4)
            dY = 0;

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
