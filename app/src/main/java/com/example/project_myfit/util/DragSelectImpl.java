package com.example.project_myfit.util;

import androidx.recyclerview.widget.RecyclerView;

import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

public class DragSelectImpl extends DragSelectTouchListener implements DragSelectTouchListener.OnAdvancedDragSelectListener {
    public static boolean isDragSelecting;
    private LockableScrollView mScrollView;
    private RecyclerView mRecyclerView;

    public DragSelectImpl() {
        withSelectListener(this);
    }

    public DragSelectImpl hasScrollView(LockableScrollView scrollView) {
        this.mScrollView = scrollView;
        return this;
    }

    public DragSelectImpl setRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        return this;
    }

    @Override
    public void onSelectionStarted(int i) {
        if (mScrollView != null) mScrollView.setScrollable(false);
        isDragSelecting = true;

        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }

    @Override
    public void onSelectionFinished(int i) {
        if (mScrollView != null) mScrollView.setScrollable(true);
        isDragSelecting = false;
    }

    @Override
    public void onSelectChange(int i, int i1, boolean b) {
        for (int j = i; j <= i1; j++) {
            RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForLayoutPosition(j);
            if (viewHolder != null) viewHolder.itemView.callOnClick();
        }
    }
}

