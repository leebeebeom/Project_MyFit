package com.example.project_myfit;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.main.adapter.CategoryAdapter;
import com.example.project_myfit.main.list.adapter.sizeadapter.SizeAdapterList;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.CATEGORY;
import static com.example.project_myfit.MyFitConstant.SIZE;

public class DragCallBackList extends ItemTouchHelper.Callback {
    //all checked
    private CategoryAdapter mCategoryAdapter;
    private SizeAdapterList mSizeAdapterList;
    private final String mType;

    public DragCallBackList(CategoryAdapter categoryAdapter, String type) {
        this.mCategoryAdapter = categoryAdapter;
        this.mType = type;
    }

    public DragCallBackList(SizeAdapterList sizeAdapterList, String type) {
        this.mSizeAdapterList = sizeAdapterList;
        this.mType = type;
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
        //checked
        if (mType.equals(CATEGORY))
            mCategoryAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        else if (mType.equals(SIZE))
            mSizeAdapterList.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
            viewHolder) {
        //checked
        if (mType.equals(CATEGORY)) mCategoryAdapter.onItemDrop(viewHolder);
        else if (mType.equals(SIZE)) mSizeAdapterList.onItemDrop(viewHolder);
    }

    @Override
    public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //checked
        RecyclerView.ViewHolder previousViewHolder = recyclerView.findViewHolderForAdapterPosition(viewHolder.getAdapterPosition() - 1);
        boolean isDraggingUp = dY < 0;
        if (isDraggingUp && previousViewHolder == null) dY = 0;

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}