package com.leebeebeom.closetnote.ui.main.list.adapter.folderadapter;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.leebeebeom.closetnote.util.adapter.dragcallback.BaseDragCallBack;

import org.jetbrains.annotations.NotNull;

public class FolderDragCallBack extends BaseDragCallBack {
    public FolderDragCallBack(FolderAdapter folderAdapter) {
        super(folderAdapter);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
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
