package com.example.project_myfit.main.list.adapter.sizeadapter;

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
import com.example.project_myfit.main.list.ListViewModel;
import com.example.project_myfit.util.adapter.AdapterUtil;
import com.example.project_myfit.util.adapter.view_holder.SizeGridVH;
import com.example.project_myfit.util.adapter.view_holder.SizeVHListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;

@SuppressLint("ClickableViewAccessibility")
public class SizeAdapterGrid extends ListAdapter<Size, SizeGridVH> {
    private final ListViewModel mModel;
    private final SizeVHListener mListener;
    private List<Size> mSizeList, mSelectedSizeList;
    private int mActionModeState, mSort;
    private final HashSet<Long> mSelectedSizeIdHashSet;
    private AdapterUtil mAdapterUtil;
    private boolean mIsDragging;

    public SizeAdapterGrid(ListViewModel model, SizeVHListener listener) {
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
        com.example.project_myfit.databinding.ItemSizeGridBinding binding =
                com.example.project_myfit.databinding.ItemSizeGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        holder.getBinding().iconItemSizeGridDragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !mIsDragging) {
                mListener.onSizeDragHandleTouch(holder);
                draggingView(holder);
                mIsDragging = true;
            }
            return false;
        });

        if (mActionModeState == ACTION_MODE_ON)
            mAdapterUtil.gridActionModeOn(holder.getBinding().cbItemSizeGrid, mSelectedSizeIdHashSet, size.getId());
        else if (mActionModeState == ACTION_MODE_OFF)
            mAdapterUtil.gridActionModeOff(holder.getBinding().cbItemSizeGrid, mSelectedSizeIdHashSet);

        holder.getBinding().iconItemSizeGridDragHandle.setVisibility(mSort == SORT_CUSTOM && mActionModeState == ACTION_MODE_ON ? View.VISIBLE : View.GONE);
        holder.getBinding().cbItemSizeGridFavorite.setClickable(mActionModeState != ACTION_MODE_ON);
    }

    private void draggingView(@NotNull SizeGridVH holder) {
        holder.itemView.setTranslationZ(10);
        holder.getBinding().ivItemSizeGridPicture.setAlpha(0.5f);
        holder.getBinding().tvItemSizeGridBrand.setAlpha(0.4f);
        holder.getBinding().tvItemSizeGridName.setAlpha(0.6f);
        holder.getBinding().cbItemSizeGrid.setAlpha(0.5f);
        holder.getBinding().cbItemSizeGridFavorite.setAlpha(0.5f);
    }

    private void dropView(@NotNull SizeGridVH holder) {
        holder.itemView.setTranslationZ(0);
        holder.getBinding().ivItemSizeGridPicture.setAlpha(1f);
        holder.getBinding().tvItemSizeGridBrand.setAlpha(0.7f);
        holder.getBinding().tvItemSizeGridName.setAlpha(0.9f);
        holder.getBinding().cbItemSizeGrid.setAlpha(0.8f);
        holder.getBinding().cbItemSizeGridFavorite.setAlpha(1);
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
}
