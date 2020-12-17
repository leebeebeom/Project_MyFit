package com.example.project_myfit.ui.main.database;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemAdapter;

import java.util.List;


public abstract class ExpandableAdapter<GVH extends RecyclerView.ViewHolder, CVH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ExpandableItemAdapter<GVH, CVH> {

    /**
     * This method will not be called.
     * Override {@link #onCreateGroupViewHolder(android.view.ViewGroup, int)} and
     * {@link #onCreateChildViewHolder(android.view.ViewGroup, int)} instead.
     *
     * @param parent   not used
     * @param viewType not used
     * @return null
     */
    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        throw new IllegalStateException("This method should not be called");
    }

    /**
     * This method will not be called.
     * Override {@link #getGroupId(int)} and {@link #getChildId(int, int)} instead.
     *
     * @param position not used
     * @return {@link RecyclerView#NO_ID}
     */
    @Override
    public final long getItemId(int position) {
        return RecyclerView.NO_ID;
    }

    /**
     * This method will not be called.
     * Override {@link #getGroupItemViewType(int)} and {@link #getChildItemViewType(int, int)} instead.
     *
     * @param position not used
     * @return 0
     */
    @Override
    public final int getItemViewType(int position) {
        return 0;
    }

    /**
     * This method will not be called.
     * Override {@link #getGroupCount()} and {@link #getChildCount(int)} instead.
     *
     * @return 0
     */
    @Override
    public final int getItemCount() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    /**
     * This method will not be called.
     * Override {@link #onBindGroupViewHolder(RecyclerView.ViewHolder, int, int)} ()} and
     * {@link #onBindChildViewHolder(RecyclerView.ViewHolder, int, int, int)} instead.
     *
     * @param holder   not used
     * @param position not used
     */
    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindGroupViewHolder(@NonNull GVH holder, int groupPosition, int viewType, @NonNull List<Object> payloads) {
        onBindGroupViewHolder(holder, groupPosition, viewType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindChildViewHolder(@NonNull CVH holder, int groupPosition, int childPosition, int viewType, @NonNull List<Object> payloads) {
        onBindChildViewHolder(holder, groupPosition, childPosition, viewType);
    }

    /**
     * Override this method if need to customize the behavior.
     * {@inheritDoc}
     */
    @Override
    public boolean onHookGroupExpand(int groupPosition, boolean fromUser) {
        return true;
    }

    /**
     * Override this method if need to customize the behavior.
     * {@inheritDoc}
     */
    @Override
    public boolean onHookGroupExpand(int groupPosition, boolean fromUser, @Nullable Object payload) {
        return onHookGroupExpand(groupPosition, fromUser);
    }

    /**
     * Override this method if need to customize the behavior.
     * {@inheritDoc}
     */
    @Override
    public boolean onHookGroupCollapse(int groupPosition, boolean fromUser) {
        return true;
    }

    /**
     * Override this method if need to customize the behavior.
     * {@inheritDoc}
     */
    @Override
    public boolean onHookGroupCollapse(int groupPosition, boolean fromUser, @Nullable Object payload) {
        return onHookGroupCollapse(groupPosition, fromUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getInitialGroupExpandedState(int groupPosition) {
        return false;
    }
}