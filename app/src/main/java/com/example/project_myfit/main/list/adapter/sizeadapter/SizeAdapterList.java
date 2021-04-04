package com.example.project_myfit.main.list.adapter.sizeadapter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemListRecyclerListBinding;
import com.example.project_myfit.main.list.ListViewModel;
import com.example.project_myfit.util.AdapterUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.MyFitConstant.SORT_CUSTOM;

@SuppressLint("ClickableViewAccessibility")
public class SizeAdapterList extends ListAdapter<Size, SizeAdapterList.SizeListVH> {
    private final ListViewModel mModel;
    private SizeAdapterListener mListener;
    private List<Size> mSizeList, mSelectedSizeList;
    private int mActionModeState, mSort;
    private final HashSet<Long> mSelectedSizeIdHashSet;
    private Animation mAnimation;

    public SizeAdapterList(ListViewModel model) {
        //checked
        super(new SizeDiffUtil());
        this.mModel = model;
        this.mSelectedSizeIdHashSet = new HashSet<>();
        setHasStableIds(true);
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
    public SizeListVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        //checked
        ItemListRecyclerListBinding binding = ItemListRecyclerListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SizeListVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SizeListVH holder, int position) {
        //checked
        if (mSelectedSizeList != null) {
            mSelectedSizeIdHashSet.clear();
            for (Size size : mSelectedSizeList)
                mSelectedSizeIdHashSet.add(size.getId());
            mSelectedSizeList = null;
        }

        Size size = getItem(holder.getLayoutPosition());
        holder.mBinding.setSize(size);
        holder.setSize(size);

        holder.setActionMode(mActionModeState, mSelectedSizeIdHashSet, mSort);
        if (mActionModeState == ACTION_MODE_OFF)
            new Handler().postDelayed(() -> mActionModeState = 0, 310);
    }

    public void onItemMove(int from, int to) {
        //checked
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
        ((SizeListVH) viewHolder).mIsDragging = false;
    }

    public void setActionModeState(int actionModeState) {
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void setSelectedSizeList(List<Size> selectedSizeList) {
        //checked
        this.mSelectedSizeList = selectedSizeList;
    }

    public void setSelectedSizeIdHashSet(long id) {
        //checked
        if (!mSelectedSizeIdHashSet.contains(id)) mSelectedSizeIdHashSet.add(id);
        else mSelectedSizeIdHashSet.remove(id);
    }

    public void selectAll() {
        //checked
        for (Size s : getCurrentList())
            mSelectedSizeIdHashSet.add(s.getId());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        //checked
        mSelectedSizeIdHashSet.clear();
        notifyDataSetChanged();
    }

    public static class SizeListVH extends RecyclerView.ViewHolder {
        //all checked
        private final ItemListRecyclerListBinding mBinding;
        private Size mSize;
        private boolean mIsDragging;
        private final AdapterUtils mAdapterUtils;

        public SizeListVH(@NotNull ItemListRecyclerListBinding binding, SizeAdapterListener listener) {
            //checked
            super(binding.getRoot());
            this.mBinding = binding;
            this.mAdapterUtils = new AdapterUtils(itemView.getContext());

            itemView.setOnClickListener(v -> listener.onSizeItemViewClick(mSize, mBinding.listCheckBox));

            itemView.setOnLongClickListener(v -> {
                listener.onSizeItemViewLongClick(getLayoutPosition());
                return false;
            });

            mBinding.listDragHandle.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !mIsDragging) {
                    mIsDragging = true;
                    listener.onSizeDragHandleTouch(this);
                }
                return false;
            });

            mBinding.listFavoriteCheckBox.setOnClickListener(v -> listener.onSizeFavoriteClick(mSize));
        }

        public void setSize(Size size) {
            mSize = size;
        }

        public ItemListRecyclerListBinding getBinding() {
            return mBinding;
        }

        public void setActionMode(int actionModeState, HashSet<Long> selectedSizeHashSet, int sort) {
            mBinding.listDragHandle.setVisibility(sort == SORT_CUSTOM && actionModeState == ACTION_MODE_ON ? View.VISIBLE : View.GONE);
            mBinding.listFavoriteCheckBox.setVisibility(actionModeState == ACTION_MODE_ON ? View.VISIBLE : View.GONE);

            if (actionModeState == ACTION_MODE_ON)
                mAdapterUtils.listActionModeOn(mBinding.listCardView, mBinding.listCheckBox, selectedSizeHashSet, mSize.getId());
            else if (actionModeState == ACTION_MODE_OFF) {
                mAdapterUtils.listActionModeOff(mBinding.listCardView, mBinding.listCheckBox);
                if (!selectedSizeHashSet.isEmpty()) selectedSizeHashSet.clear();
            }
        }
    }
}
