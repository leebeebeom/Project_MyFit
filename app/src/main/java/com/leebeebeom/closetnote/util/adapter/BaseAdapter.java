package com.leebeebeom.closetnote.util.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.leebeebeom.closetnote.data.tuple.BaseTuple;
import com.leebeebeom.closetnote.util.SizeLiveSet;
import com.leebeebeom.closetnote.util.adapter.viewholder.BaseVH;
import com.leebeebeom.closetnote.util.adapter.viewholder.BaseVHListener;
import com.leebeebeom.closetnote.util.constant.Sort;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;

import static com.leebeebeom.closetnote.util.ActionModeImpl.ACTION_MODE_OFF;
import static com.leebeebeom.closetnote.util.ActionModeImpl.ACTION_MODE_ON;

@Accessors(prefix = "m")
public abstract class BaseAdapter<T extends BaseTuple, L extends BaseVHListener, VH extends BaseVH<T, L>> extends ListAdapter<T, VH> {
    private final L mListener;
    @Getter
    protected List<T> mNewOrderList;
    private final SizeLiveSet<T> mSelectedItems;
    @Getter
    private int mActionModeState;
    @Getter
    protected Sort mSort;

    protected BaseAdapter(SizeLiveSet<T> selectedItems, L listener) {
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
        this.mListener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void setItems(List<T> list) {
        submitList(list);
        this.mNewOrderList = list;
    }

    public abstract void setSort(Sort sort);

    @NonNull
    @NotNull
    @Override
    public VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return getViewHolder(parent, mListener, mSelectedItems);
    }

    protected abstract VH getViewHolder(@NotNull ViewGroup parent, L listener, SizeLiveSet<T> selectedItems);

    @Override
    public void onBindViewHolder(@NonNull @NotNull VH holder, int position) {
        holder.setTuple(getItem(position));
        holder.setAdapter(this);

        if (mActionModeState == ACTION_MODE_ON)
            holder.getCheckBox().setChecked(mSelectedItems.contains(getItem(position)));
        else if (mActionModeState == ACTION_MODE_OFF) {
            holder.getCheckBox().setChecked(false);
            if (!mSelectedItems.isEmpty()) mSelectedItems.clear();
        }
    }

    public void setActionModeState(int actionModeState) {
        this.mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void moveItem(int from, int to) {
        move(from, to);
        notifyItemMoved(from, to);
    }

    private void move(int from, int to) {
        if (from < to) //down
            for (int i = from; i < to; i++)
                swap(i, i + 1);
        else //up
            for (int i = from; i > to; i--)
                swap(i, i - 1);
    }

    private void swap(int i, int i2) {
        Collections.swap(mNewOrderList, i, i2);
        if (mNewOrderList.get(i).getId() != -1 && mNewOrderList.get(i2).getId() != -1) {
            BaseTuple baseTuple1 = mNewOrderList.get(i);
            BaseTuple baseTuple2 = mNewOrderList.get(i2);
            int sortNumber1 = baseTuple1.getSortNumber();
            int sortNumber2 = baseTuple2.getSortNumber();
            baseTuple1.setSortNumber(sortNumber2);
            baseTuple2.setSortNumber(sortNumber1);
        }
    }

    public void selectAll() {
        mSelectedItems.addAll2(getCurrentList());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedItems.clear2();
        notifyDataSetChanged();
    }
}
