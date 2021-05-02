package com.example.project_myfit.util.adapter;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.main.list.adapter.sizeadapter.SizeAdapterList;
import com.example.project_myfit.main.main.adapter.CategoryAdapter;
import com.example.project_myfit.util.MyFitVariable;

import org.jetbrains.annotations.NotNull;

public class DragCallBackList extends ItemTouchHelper.Callback {
    private CategoryAdapter mCategoryAdapter;
    private SizeAdapterList mSizeAdapterList;

    public DragCallBackList(CategoryAdapter categoryAdapter) {
        this.mCategoryAdapter = categoryAdapter;
    }

    public DragCallBackList(SizeAdapterList sizeAdapterList) {
        this.mSizeAdapterList = sizeAdapterList;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
            viewHolder, @NonNull RecyclerView.ViewHolder target) {
        if (mCategoryAdapter != null)
            mCategoryAdapter.itemMove(viewHolder.getAbsoluteAdapterPosition(), target.getAbsoluteAdapterPosition());
        else if (mSizeAdapterList != null)
            mSizeAdapterList.itemMove(viewHolder.getAbsoluteAdapterPosition(), target.getAbsoluteAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
            viewHolder) {
        MyFitVariable.isDragging = false;
        if (mCategoryAdapter != null) mCategoryAdapter.itemDrop(viewHolder);
        else if (mSizeAdapterList != null) mSizeAdapterList.itemDrop(viewHolder);
    }

    @Override
    public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        RecyclerView.ViewHolder previousViewHolder = recyclerView.findViewHolderForAdapterPosition(viewHolder.getAbsoluteAdapterPosition() - 1);
        boolean isDraggingUp = dY < 0;
        if (isDraggingUp && previousViewHolder == null) dY = 0;

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}