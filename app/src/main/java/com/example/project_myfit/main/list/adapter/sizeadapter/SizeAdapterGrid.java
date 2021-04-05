package com.example.project_myfit.main.list.adapter.sizeadapter;

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

import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemListRecyclerGridBinding;
import com.example.project_myfit.main.list.ListViewModel;
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
    private List<Size> mSizeList, mSelectedSizeList;
    private int mActionModeState, mSort;
    private final HashSet<Long> mSelectedSizeIdHashSet;

    public SizeAdapterGrid(ListViewModel model) {
        //checked
        super(new SizeDiffUtil());
        this.mModel = model;
        this.setHasStableIds(true);
        this.mSelectedSizeIdHashSet = new HashSet<>();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void setOnSizeAdapterListener(SizeAdapterListener listener) {
        //checked
        this.mListener = listener;
    }

    public void submitList(@Nullable @org.jetbrains.annotations.Nullable List<Size> list, int sort) {
        //checked
        super.submitList(list);
        this.mSizeList = list;
        this.mSort = sort;
    }

    @NonNull
    @NotNull
    @Override
    public SizeGridVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        //checked
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

        if (mSelectedSizeList != null) {
            mSelectedSizeIdHashSet.clear();
            for (Size s : mSelectedSizeList)
                mSelectedSizeIdHashSet.add(s.getId());
            mSelectedSizeList = null;
        }

        //check box visibility----------------------------------------------------------------------
        if (mActionModeState == ACTION_MODE_ON) {
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(mSelectedSizeIdHashSet.contains(size.getId()));
            dragHandle.setVisibility(mSort == SORT_CUSTOM ? View.VISIBLE : View.GONE);
            holder.mBinding.gridFavoriteCheckBox.setClickable(false);
        } else {
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
            if (!mSelectedSizeIdHashSet.isEmpty()) mSelectedSizeIdHashSet.clear();
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
        //checked
        mModel.getRepository().sizeUpdate(mSizeList);
        mListener.onSizeDragHandleTouch(viewHolder);
        mModel.getSelectedItemSizeList().clear();
        for (Size s : getCurrentList())
            if (mSelectedSizeIdHashSet.contains(s.getId())) mModel.getSelectedItemSizeList().add(s);
        ((SizeGridVH) viewHolder).mIsDragging = false;
    }
    //----------------------------------------------------------------------------------------------

    //action mode & drag select---------------------------------------------------------------------
    public void setActionModeState(int actionModeState) {
        //checked
        this.mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void setSelectedItem(List<Size> selectedItem) {
        this.mSelectedSizeList = selectedItem;
    }

    //drag select-----------------------------------------------------------------------------------
    public void setSelectedPosition(long id) {
        if (!mSelectedSizeIdHashSet.contains(id)) mSelectedSizeIdHashSet.add(id);
        else mSelectedSizeIdHashSet.remove(id);
    }

    public void selectAll() {
        //checked
        for (Size s : getCurrentList()) mSelectedSizeIdHashSet.add(s.getId());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedSizeIdHashSet.clear();
        notifyDataSetChanged();
    }
    //----------------------------------------------------------------------------------------------

    public static class SizeGridVH extends RecyclerView.ViewHolder {
        private final ItemListRecyclerGridBinding mBinding;
        private Size mSize;
        private boolean mIsDragging;

        public SizeGridVH(@NotNull ItemListRecyclerGridBinding binding, SizeAdapterListener listener) {
            //checked
            super(binding.getRoot());
            this.mBinding = binding;

            itemView.setOnClickListener(v -> listener.onSizeItemViewClick(mSize, mBinding.gridCheckBox));

            itemView.setOnLongClickListener(v -> {
                listener.onSizeItemViewLongClick(getLayoutPosition());
                return false;
            });

            mBinding.gridDragHandle.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN && mIsDragging) {
                    listener.onSizeDragHandleTouch(this);
                    mIsDragging = true;
                }
                return false;
            });
            //폴더랑 같이 액션모드 클래스화
            mBinding.gridFavoriteCheckBox.setOnClickListener(v -> listener.onSizeFavoriteClick(mSize));
        }

        public void setSize(Size size) {
            this.mSize = size;
        }

        public ItemListRecyclerGridBinding getBinding() {
            return mBinding;
        }
    }
}
