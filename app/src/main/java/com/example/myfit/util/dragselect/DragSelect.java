package com.example.myfit.util.dragselect;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.util.LockableScrollView;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true, prefix = "m")
public class DragSelect extends DragSelectTouchListener implements DragSelectTouchListener.OnAdvancedDragSelectListener, Cloneable {
    public static boolean sDragSelecting;
    @Setter
    protected LockableScrollView mScrollView;
    @Setter
    protected RecyclerView mRecyclerView;

    @Inject
    public DragSelect() {
        withSelectListener(this);
    }

    @Override
    public void onSelectionStarted(int i) {
        sDragSelecting = true;

        try {
            mScrollView.setScrollable(false);
            RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForLayoutPosition(i);
            if (viewHolder != null) viewHolder.itemView.callOnClick();
        } catch (NullPointerException e) {
            logE(e);
        }
    }

    @Override
    public void onSelectionFinished(int i) {
        sDragSelecting = false;

        try {
            mScrollView.setScrollable(true);
        } catch (NullPointerException e) {
            logE(e);
        }
    }

    @Override
    public void onSelectChange(int i, int i1, boolean b) {
        try {
            for (int j = i; j <= i1; j++) {
                RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForLayoutPosition(j);
                if (viewHolder != null) viewHolder.itemView.callOnClick();
            }
        } catch (NullPointerException e) {
            logE(e);
        }
    }

    private void logE(Exception e) {
        Log.e("로그", "logE: " + e.getMessage(), e);
    }

    @NonNull
    @NotNull
    @Override
    public DragSelect clone() throws CloneNotSupportedException {
        return (DragSelect) super.clone();
    }
}

