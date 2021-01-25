package com.example.project_myfit.ui.main.listfragment.adapter.folderdapter;

import android.graphics.Canvas;
import android.view.View;

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
        ((FolderAdapter.FolderVH) viewHolder).mBinding.folderAmountLayout.setVisibility(View.VISIBLE);
        viewHolder.itemView.setTranslationZ(0);
        viewHolder.itemView.setAlpha(1);
        mAdapter.onItemDrop();
    }

    @Override
    public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        boolean isDraggingUp = dY < 0;
        boolean isDraggingDown = dY > 0;
        if (recyclerView.getAdapter() != null && isDraggingDown) {
            if (recyclerView.getAdapter().getItemCount() % 4 == 0 && recyclerView.getAdapter().getItemCount() - 5 < viewHolder.getLayoutPosition())
                dY = 0;
            else if (recyclerView.getAdapter().getItemCount() % 4 == 1 && recyclerView.getAdapter().getItemCount() - 1 == viewHolder.getLayoutPosition())
                dY = 0;
            else if (recyclerView.getAdapter().getItemCount() % 4 == 2 && recyclerView.getAdapter().getItemCount() - 3 < viewHolder.getLayoutPosition())
                dY = 0;
            else if (recyclerView.getAdapter().getItemCount() % 4 == 3 && recyclerView.getAdapter().getItemCount() - 4 < viewHolder.getLayoutPosition())
                dY = 0;
            else if (viewHolder.itemView.getY() > recyclerView.getBottom() - 380)
                dY = 0;
        } else if (recyclerView.getAdapter() != null && isDraggingUp && viewHolder.getLayoutPosition() < 4)
            dY = 0;
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
