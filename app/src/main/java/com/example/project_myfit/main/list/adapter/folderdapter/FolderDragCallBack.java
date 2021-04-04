package com.example.project_myfit.main.list.adapter.folderdapter;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class FolderDragCallBack extends ItemTouchHelper.Callback {
    private final FolderAdapter mFolderAdapter;

    public FolderDragCallBack(FolderAdapter folderAdapter) {
        this.mFolderAdapter = folderAdapter;
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
        mFolderAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
            viewHolder) {
        mFolderAdapter.onItemDrop(viewHolder);
    }

    @Override
    public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        boolean isDraggingUp = dY < 0;
        boolean isDraggingDown = dY > 0;

        int itemCount = 0;
        if (recyclerView.getAdapter() != null) itemCount = recyclerView.getAdapter().getItemCount();

        int position = viewHolder.getLayoutPosition();
        if ((isDraggingDown && itemCount - 5 < position) || isDraggingUp && position < 4) dY = 0;

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
