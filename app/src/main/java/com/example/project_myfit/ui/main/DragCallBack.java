package com.example.project_myfit.ui.main;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.dragswipe.DragAndSwipeCallback;
import com.chad.library.adapter.base.module.BaseDraggableModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.project_myfit.ui.main.adapter.MainFragmentAdapter;
import com.example.project_myfit.ui.main.database.ChildCategory;
import com.example.project_myfit.ui.main.database.ParentCategory;

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
        //If target is not parent && Not same parent, Can drop
        if (targetParentPosition != -1) {
            ParentCategory currentParent = (ParentCategory) mAdapter.getItem(currentParentPosition);
            ParentCategory targetParent = (ParentCategory) mAdapter.getItem(targetParentPosition);
            sameParent = currentParent.getParentCategory().equals(targetParent.getParentCategory());
        } else {
            sameParent = false;
        }
        return target.getItemViewType() == 2 && targetParentPosition != -1 &&sameParent;
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
        //If without 'previousViewHolder != null & nextViewHolder != null' NPE
        boolean cantDrag = (isDraggingUp && previousViewHolder != null && !canDropOver(recyclerView, viewHolder, previousViewHolder))
                || (isDraggingDown && nextViewHolder != null && !canDropOver(recyclerView, viewHolder, nextViewHolder));
        float newDy;
        if (cantDrag) {
            newDy = 0f;
        } else {
            newDy = dY;
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, newDy, actionState, isCurrentlyActive);
    }

    public interface DragListener {
        void onStartDrag(BaseViewHolder holder);
    }

    @Override
    public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
        ChildCategory sourceItem = (ChildCategory) mAdapter.getItem(fromPos);
        ChildCategory targetItem = (ChildCategory) mAdapter.getItem(toPos);



        super.onMoved(recyclerView, source, fromPos, target, toPos, x, y);
    }
}


