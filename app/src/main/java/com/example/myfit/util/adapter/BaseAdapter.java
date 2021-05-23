package com.example.myfit.util.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.myfit.data.model.tuple.BaseTuple;
import com.example.myfit.util.adapter.viewholder.BaseVH;
import com.example.myfit.util.adapter.viewholder.BaseVHListener;
import com.example.myfit.util.constant.SortValue;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

import static com.example.myfit.util.MyFitConstant.ACTION_MODE_ON;
import static com.example.myfit.util.MyFitConstant.LISTVIEW;

public abstract class BaseAdapter<T extends BaseTuple, L extends BaseVHListener, VH extends BaseVH<T, L>> extends ListAdapter<T, VH> {
    private final AdapterUtil<T> adapterUtil;
    private final L listener;
    private HashSet<Long> selectedItemIds;
    private int actionModeState, sort;
    private List<T> itemList;

    protected BaseAdapter(L listener) {
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
        this.adapterUtil = new AdapterUtil<>();
        this.listener = listener;
        this.selectedItemIds = new HashSet<>();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void setItems(int sort, List<T> list) {
        submitList(list);
        this.sort = sort;
        this.itemList = list;
    }

    @NonNull
    @NotNull
    @Override
    public VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return getViewHolder(parent, listener);
    }

    protected abstract VH getViewHolder(@NotNull ViewGroup parent, L listener);

    @Override
    public void onBindViewHolder(@NonNull @NotNull VH holder, int position) {
        T tuple = getItem(position);
        holder.setTuple(tuple);

        if (actionModeState == ACTION_MODE_ON) {
            holder.getCheckBox().setChecked(selectedItemIds.contains(getItemId(position)));
            holder.setCheckBoxCheckedChangeListener(selectedItemIds, getItemId(position));
            holder.getDragHandleIcon().setVisibility(sort == SortValue.SORT_CUSTOM.getValue() ? View.VISIBLE : View.GONE);
            this.actionModeOn(holder);
        } else {
            holder.getCheckBox().setChecked(false);
            holder.getCheckBox().setOnCheckedChangeListener(null);
            holder.getDragHandleIcon().setVisibility(View.GONE);
            this.actionModeOff(holder);
        }
    }

    public void setActionModeState(int actionModeState) {
        this.actionModeState = actionModeState;
        notifyDataSetChanged();
    }

    private void actionModeOn(@NotNull VH holder) {
        if (getViewType() == LISTVIEW)
            adapterUtil.listActionModeOn(holder.getCardView());
        else holder.getCheckBox().setVisibility(View.VISIBLE);
    }

    private void actionModeOff(VH holder) {
        if (getViewType() == LISTVIEW)
            adapterUtil.listActionModeOff(holder.getCardView());
        else {
            holder.getCheckBox().jumpDrawablesToCurrentState();
            holder.getCheckBox().setVisibility(View.GONE);
        }
        if (getItemCount() - 1 == holder.getLayoutPosition()) actionModeState = 0;
    }

    protected abstract int getViewType();

    public void setSelectedItemIds(HashSet<Long> selectedItemIds) {
        this.selectedItemIds = selectedItemIds;
    }

    public void moveItem(int from, int to) {
        adapterUtil.itemMove(from, to, itemList);
        notifyItemMoved(from, to);
    }

    public void selectAll() {
        getCurrentList().forEach(tuple -> selectedItemIds.add(tuple.getId()));
        notifyDataSetChanged();
    }

    public void deselectAll() {
        selectedItemIds.clear();
        notifyDataSetChanged();
    }
}
