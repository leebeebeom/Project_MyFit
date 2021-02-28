package com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemListRecyclerGridBinding;
import com.example.project_myfit.ui.main.listfragment.ListViewModel;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.MyFitConstant.SORT_CUSTOM;

@SuppressLint("ClickableViewAccessibility")
public class SizeAdapterGrid extends ListAdapter<Size, SizeAdapterGrid.SizeGridVH> {
    private final ListViewModel mModel;
    private SizeAdapterListener mListener;
    private List<Size> mSizeList;
    private int mActionModeState, mSort;
    private HashSet<Integer> mSelectedPosition;


    public SizeAdapterGrid(ListViewModel model) {
        super(new SizeDiffUtil());
        this.mModel = model;
        this.setHasStableIds(true);
        this.mSelectedPosition = new HashSet<>();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void setOnSizeAdapterListener(SizeAdapterListener listener) {
        this.mListener = listener;
    }

    @Override
    public void submitList(@Nullable @org.jetbrains.annotations.Nullable List<Size> list) {
        super.submitList(list);
        this.mSizeList = list;
    }

    @NonNull
    @NotNull
    @Override
    public SizeGridVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemListRecyclerGridBinding binding = ItemListRecyclerGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SizeGridVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SizeGridVH holder, int position) {
        Size size = getItem(holder.getLayoutPosition());
        holder.mBinding.setSize(size);
        holder.setSize(size);

        MaterialCheckBox checkBox = holder.mBinding.gridCheckBox;
        AppCompatImageView dragHandle = holder.mBinding.gridDragHandle;

        if (size.getImageUri() != null) holder.mBinding.addImageIcon.setVisibility(View.GONE);
        //check box visibility----------------------------------------------------------------------
        if (mActionModeState == ACTION_MODE_ON) {
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
            dragHandle.setVisibility(mSort == SORT_CUSTOM ? View.VISIBLE : View.GONE);
            holder.mBinding.gridFavoriteCheckBox.setClickable(false);
        } else {
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
            if (!mSelectedPosition.isEmpty()) mSelectedPosition.clear();
            dragHandle.setVisibility(View.GONE);
            holder.mBinding.gridFavoriteCheckBox.setClickable(true);
        }
    }

    //drag------------------------------------------------------------------------------------------
    public void onItemMove(int from, int to) {
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(mSizeList, i, i + 1);

                int toOrder = mSizeList.get(i).getOrderNumber();
                int fromOrder = mSizeList.get(i + 1).getOrderNumber();
                mSizeList.get(i).setOrderNumber(fromOrder);
                mSizeList.get(i + 1).setOrderNumber(toOrder);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(mSizeList, i, i - 1);

                int toOrder = mSizeList.get(i).getOrderNumber();
                int fromOrder = mSizeList.get(i - 1).getOrderNumber();
                mSizeList.get(i).setOrderNumber(fromOrder);
                mSizeList.get(i - 1).setOrderNumber(toOrder);
            }
        }
        notifyItemMoved(from, to);
    }

    public void onItemDrop(@NotNull RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setTranslationZ(0);
        mListener.onSizeDragHandleTouch(viewHolder);
        mModel.getRepository().sizeUpdate(mSizeList);
    }
    //----------------------------------------------------------------------------------------------

    //action mode & drag select---------------------------------------------------------------------
    public void setActionModeState(int actionModeState) {
        this.mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public HashSet<Integer> getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(HashSet<Integer> selectedPosition) {
        this.mSelectedPosition = selectedPosition;
    }

    //drag select-----------------------------------------------------------------------------------
    public void setSelectedPosition(int position) {
        if (!mSelectedPosition.contains(position)) mSelectedPosition.add(position);
        else mSelectedPosition.remove(position);
    }

    public void selectAll() {
        for (int i = 0; i < getCurrentList().size(); i++) mSelectedPosition.add(i);
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedPosition.clear();
        notifyDataSetChanged();
    }
    //----------------------------------------------------------------------------------------------

    public void setSort(int sort) {
        if (mSort != sort) {
            this.mSort = sort;
            notifyDataSetChanged();
        }
    }

    public static class SizeGridVH extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener, View.OnTouchListener {
        private final ItemListRecyclerGridBinding mBinding;
        private final SizeAdapterListener mListener;
        private Size mSize;

        public SizeGridVH(@NotNull ItemListRecyclerGridBinding binding, SizeAdapterListener listener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.mListener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mBinding.gridDragHandle.setOnTouchListener(this);
            mBinding.gridFavoriteCheckBox.setOnClickListener(v -> mListener.onSizeFavoriteClick(mSize));
        }

        public void setSize(Size size) {
            this.mSize = size;
        }

        @Override
        public void onClick(View v) {
            mListener.onSizeItemViewClick(mSize, mBinding.gridCheckBox, getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onSizeItemViewLongClick(mBinding.gridCardView, getLayoutPosition());
            return false;
        }

        @Override
        public boolean onTouch(View v, @NotNull MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                itemView.setTranslationZ(10);
                mListener.onSizeDragHandleTouch(this);
            }
            return false;
        }
    }
}
