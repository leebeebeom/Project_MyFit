package com.example.project_myfit.fragment.list.adapter.sizeadapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemListRecyclerGridBinding;
import com.example.project_myfit.fragment.list.ListViewModel;
import com.example.project_myfit.util.adapter.AdapterUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;

@SuppressLint("ClickableViewAccessibility")
public class SizeAdapterGrid extends ListAdapter<Size, SizeAdapterGrid.SizeGridVH> {
    private final ListViewModel mModel;
    private final SizeAdapterListener mListener;
    private List<Size> mSizeList, mSelectedSizeList;
    private int mActionModeState, mSort;
    private final HashSet<Long> mSelectedSizeIdHashSet;
    private AdapterUtil mAdapterUtil;
    private boolean mIsDragging;

    public SizeAdapterGrid(ListViewModel model, SizeAdapterListener listener) {
        super(new SizeDiffUtil());
        this.mModel = model;
        this.mSelectedSizeIdHashSet = new HashSet<>();
        this.mListener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void submitList(@Nullable @org.jetbrains.annotations.Nullable List<Size> list, int sort) {
        super.submitList(list);
        this.mSizeList = list;
        this.mSort = sort;
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
        if (mAdapterUtil == null) mAdapterUtil = new AdapterUtil(holder.itemView.getContext());

        if (mSelectedSizeList != null && !mSelectedSizeList.isEmpty()) {
            mAdapterUtil.restoreActionMode(mSelectedSizeList, mSelectedSizeIdHashSet);
            mSelectedSizeList = null;
        }

        Size size = getItem(holder.getLayoutPosition());
        holder.setSize(size);
        holder.mBinding.gridDragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !mIsDragging) {
                mListener.onSizeDragHandleTouch(holder);
                draggingView(holder);
                mIsDragging = true;
            }
            return false;
        });

        if (mActionModeState == ACTION_MODE_ON)
            mAdapterUtil.gridActionModeOn(holder.mBinding.gridCheckBox, mSelectedSizeIdHashSet, size.getId());
        else if (mActionModeState == ACTION_MODE_OFF)
            mAdapterUtil.gridActionModeOff(holder.mBinding.gridCheckBox, mSelectedSizeIdHashSet);

        holder.mBinding.gridDragHandle.setVisibility(mSort == SORT_CUSTOM && mActionModeState == ACTION_MODE_ON ? View.VISIBLE : View.GONE);
        holder.mBinding.gridFavoriteCheckBox.setClickable(mActionModeState != ACTION_MODE_ON);
    }

    private void draggingView(@NotNull SizeGridVH holder) {
        holder.itemView.setTranslationZ(10);
        holder.mBinding.gridImage.setAlpha(0.5f);
        holder.mBinding.gridBrandText.setAlpha(0.4f);
        holder.mBinding.gridNameText.setAlpha(0.6f);
        holder.mBinding.gridCheckBox.setAlpha(0.5f);
        holder.mBinding.gridFavoriteCheckBox.setAlpha(0.5f);
    }

    private void dropView(@NotNull SizeGridVH holder) {
        holder.itemView.setTranslationZ(0);
        holder.mBinding.gridImage.setAlpha(1f);
        holder.mBinding.gridBrandText.setAlpha(0.7f);
        holder.mBinding.gridNameText.setAlpha(0.9f);
        holder.mBinding.gridCheckBox.setAlpha(0.8f);
        holder.mBinding.gridFavoriteCheckBox.setAlpha(1);
    }

    public void onItemMove(int from, int to) {
        mAdapterUtil.itemMove(from, to, mSizeList);
        notifyItemMoved(from, to);
    }

    public void onItemDrop(@NotNull RecyclerView.ViewHolder viewHolder) {
        mListener.onSizeDragHandleTouch(viewHolder);
        mModel.sizeItemDrop(mSizeList);
        dropView((SizeGridVH) viewHolder);
        mIsDragging = false;
    }

    public void setActionModeState(int actionModeState) {
        this.mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void setSelectedSizeList(List<Size> selectedSizeList) {
        this.mSelectedSizeList = selectedSizeList;
    }

    public void sizeSelected(long id) {
        if (!mSelectedSizeIdHashSet.contains(id)) mSelectedSizeIdHashSet.add(id);
        else mSelectedSizeIdHashSet.remove(id);
    }

    public void selectAll() {
        for (Size s : getCurrentList())
            mSelectedSizeIdHashSet.add(s.getId());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedSizeIdHashSet.clear();
        notifyDataSetChanged();
    }

    public static class SizeGridVH extends RecyclerView.ViewHolder {
        private final ItemListRecyclerGridBinding mBinding;
        private Size mSize;

        public SizeGridVH(@NotNull ItemListRecyclerGridBinding binding, SizeAdapterListener listener) {
            super(binding.getRoot());
            this.mBinding = binding;

            itemView.setOnClickListener(v -> listener.onSizeItemViewClick(mSize, mBinding.gridCheckBox));

            itemView.setOnLongClickListener(v -> {
                listener.onSizeItemViewLongClick(getLayoutPosition());
                return false;
            });

            mBinding.gridFavoriteCheckBox.setOnClickListener(v -> listener.onSizeFavoriteClick(mSize));
        }

        public void setSize(Size size) {
            this.mSize = size;
            mBinding.setSize(size);
        }
    }
}
