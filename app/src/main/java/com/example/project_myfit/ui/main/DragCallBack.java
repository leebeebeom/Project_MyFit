package com.example.project_myfit.ui.main;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.dragswipe.DragAndSwipeCallback;
import com.chad.library.adapter.base.module.BaseDraggableModule;
import com.example.project_myfit.ui.main.database.ParentCategory;
import com.example.project_myfit.ui.main.nodedapter.MainFragmentAdapter;

public class DragCallBack extends DragAndSwipeCallback {
    private final MainFragmentAdapter mAdapter;


    public DragCallBack(BaseDraggableModule draggableModule, MainFragmentAdapter mAdapter) {
        super(draggableModule);
        this.mAdapter = mAdapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
    }

    @Override
    public boolean canDropOver(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder current, @NonNull RecyclerView.ViewHolder target) {
        int currentParentPosition = mAdapter.findParentNode(current.getAdapterPosition());
        int targetParentPosition = mAdapter.findParentNode(target.getAdapterPosition());
        boolean sameParent;
        //If target is parent : targetParentPosition = -1
        if (targetParentPosition != -1) {
            ParentCategory currentParent = (ParentCategory) mAdapter.getItem(currentParentPosition);
            ParentCategory targetParent = (ParentCategory) mAdapter.getItem(targetParentPosition);
            sameParent = currentParent.getParentCategory().equals(targetParent.getParentCategory());
        } else {
            sameParent = false;
        }
        return target.getItemViewType() == 2 && targetParentPosition != -1 && sameParent;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        RecyclerView.ViewHolder previousViewHolder = recyclerView.findViewHolderForAdapterPosition(viewHolder.getAdapterPosition() - 1);
        RecyclerView.ViewHolder nextViewHolder = recyclerView.findViewHolderForAdapterPosition(viewHolder.getAdapterPosition() + 1);
        boolean isDraggingUp = dY < 0;
        boolean isDraggingDown = dY > 0;
        //if without 'previousViewHolder != null & nextViewHolder != null' NPE
        boolean canDrag = (isDraggingUp && previousViewHolder != null && !canDropOver(recyclerView, viewHolder, previousViewHolder))
                || (isDraggingDown && nextViewHolder != null && !canDropOver(recyclerView, viewHolder, nextViewHolder));
        float newDy;
        if (canDrag) {
            newDy = 0f;
        } else {
            newDy = dY;
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, newDy, actionState, isCurrentlyActive);
    }

}


