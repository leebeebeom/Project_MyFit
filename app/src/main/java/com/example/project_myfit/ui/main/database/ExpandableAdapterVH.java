package com.example.project_myfit.ui.main.database;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemState;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.expandable.annotation.ExpandableItemStateFlags;

public abstract class ExpandableAdapterVH extends RecyclerView.ViewHolder implements ExpandableItemViewHolder {
    private final ExpandableItemState mExpandState = new ExpandableItemState();

    public ExpandableAdapterVH(@NonNull View itemView) {
        super(itemView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExpandStateFlags(@ExpandableItemStateFlags int flags) {
        mExpandState.setFlags(flags);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ExpandableItemStateFlags
    public int getExpandStateFlags() {
        return mExpandState.getFlags();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public ExpandableItemState getExpandState() {
        return mExpandState;
    }
}