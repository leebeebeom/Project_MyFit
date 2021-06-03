package com.example.myfit.util.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.util.SizeLiveSet;
import com.example.myfit.util.adapter.viewholder.BaseVH;
import com.example.myfit.util.adapter.viewholder.BaseVHListener;
import com.example.myfit.util.constant.Sort;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static com.example.myfit.util.ActionModeImpl.ACTION_MODE_OFF;
import static com.example.myfit.util.ActionModeImpl.ACTION_MODE_ON;

@Accessors(prefix = "m")
public abstract class BaseAdapter<T extends BaseTuple, L extends BaseVHListener, VH extends BaseVH<T, L>> extends ListAdapter<T, VH> {
    @Setter
    private L mListener;
    @Getter
    private List<T> mNewOrderList;
    private final Set<T> mSelectedItems;
    @Getter
    private int mActionModeState;
    @Getter
    private Sort mSort;

    protected BaseAdapter(SizeLiveSet<T> selectedItems) {
        super(new DiffUtil.ItemCallback<T>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull T oldItem, @NonNull @NotNull T newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull T oldItem, @NonNull @NotNull T newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.mSelectedItems = selectedItems;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void setItems(Sort sort, List<T> list) {
        submitList(list);
        this.mSort = sort;
        this.mNewOrderList = list;
    }

    @NonNull
    @NotNull
    @Override
    public VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return getViewHolder(parent, mListener, mSelectedItems);
    }

    protected abstract VH getViewHolder(@NotNull ViewGroup parent, L listener, Set<T> selectedItemIds);

    @Override
    public void onBindViewHolder(@NonNull @NotNull VH holder, int position) {
        holder.setTuple(getItem(position));
        holder.setAdapter(this);

        if (mActionModeState == ACTION_MODE_ON)
            holder.getCheckBox().setChecked(mSelectedItems.contains(getItem(position)));
        else if (mActionModeState == ACTION_MODE_OFF) {
            holder.getCheckBox().setChecked(false);
            if (!mSelectedItems.isEmpty()) mSelectedItems.clear();
            if (getItemCount() - 1 == holder.getLayoutPosition()) mActionModeState = 0;
        }
    }

    public void setActionModeState(int actionModeState) {
        this.mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void moveItem(int from, int to) {
        AdapterUtil.itemMove(from, to, mNewOrderList);
        notifyItemMoved(from, to);
    }

    public void selectAll() {
        mSelectedItems.addAll(getCurrentList());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }
}
