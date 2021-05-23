package com.example.myfit.util;

import androidx.recyclerview.widget.RecyclerView;

import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

public class DragSelectImpl extends DragSelectTouchListener implements DragSelectTouchListener.OnAdvancedDragSelectListener {
    public static boolean isDragSelecting;
    private final LockableScrollView scrollView;
    private RecyclerView recyclerView;

    public DragSelectImpl(LockableScrollView scrollView) {
        this.scrollView = scrollView;
        withSelectListener(this);
    }

    public DragSelectImpl setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        return this;
    }

    @Override
    public void onSelectionStarted(int i) {
        scrollView.setScrollable(false);
        isDragSelecting = true;

        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }

    @Override
    public void onSelectionFinished(int i) {
        scrollView.setScrollable(true);
        isDragSelecting = false;
    }

    @Override
    public void onSelectChange(int i, int i1, boolean b) {
        for (int j = i; j <= i1; j++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(j);
            if (viewHolder != null) viewHolder.itemView.callOnClick();
        }
    }
}

