package com.leebeebeom.closetnote.util.dragselect;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.leebeebeom.closetnote.ui.view.LockableScrollView;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import lombok.experimental.Accessors;

@Accessors(chain = true, prefix = "m")
public class DragSelect extends DragSelectTouchListener implements DragSelectTouchListener.OnAdvancedDragSelectListener, Cloneable {
    public static boolean sDragSelecting;
    private final DragSelectListener mListener;

    public DragSelect(Fragment fragment) {
        withSelectListener(this);
        mListener = (DragSelectListener) fragment;
    }

    @Override
    public void onSelectionStarted(int i) {
        sDragSelecting = true;
        mListener.getScrollView().setScrollable(false);
        RecyclerView.ViewHolder viewHolder = mListener.getRecyclerView().findViewHolderForLayoutPosition(i);
        if (viewHolder != null) viewHolder.itemView.callOnClick();
    }

    @Override
    public void onSelectionFinished(int i) {
        sDragSelecting = false;
        mListener.getScrollView().setScrollable(true);
    }

    @Override
    public void onSelectChange(int i, int i1, boolean b) {
        for (int j = i; j <= i1; j++) {
            RecyclerView.ViewHolder viewHolder = mListener.getRecyclerView().findViewHolderForLayoutPosition(j);
            if (viewHolder != null) viewHolder.itemView.callOnClick();
        }
    }

    public interface DragSelectListener {
        LockableScrollView getScrollView();

        RecyclerView getRecyclerView();
    }
}

