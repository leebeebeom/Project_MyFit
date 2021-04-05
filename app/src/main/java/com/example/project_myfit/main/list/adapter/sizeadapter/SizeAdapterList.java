package com.example.project_myfit.main.list.adapter.sizeadapter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemListRecyclerListBinding;
import com.example.project_myfit.main.list.ListViewModel;
import com.example.project_myfit.util.AdapterUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.MyFitConstant.SORT_CUSTOM;

@SuppressLint("ClickableViewAccessibility")
public class SizeAdapterList extends ListAdapter<Size, SizeAdapterList.SizeListVH> {
    private final ListViewModel mModel;
    private final SizeAdapterListener mListener;
    private List<Size> mSizeList, mSelectedSizeList;
    private int mActionModeState, mSort;
    private final HashSet<Long> mSelectedSizeIdHashSet;
    private AdapterUtil mAdapterUtil;

    public SizeAdapterList(ListViewModel model, SizeAdapterListener listener) {
        //checked
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

        if (mAdapterUtil == null) mAdapterUtil = new AdapterUtil(holder.itemView.getContext());

        Size size = getItem(holder.getLayoutPosition());
        holder.setSize(size);

        if (mActionModeState == ACTION_MODE_ON)
            mAdapterUtil.listActionModeOn(holder.mBinding.listCardView, holder.mBinding.listCheckBox, mSelectedSizeIdHashSet, size.getId());
        else if (mActionModeState == ACTION_MODE_OFF) {
            mAdapterUtil.listActionModeOff(holder.mBinding.listCardView, holder.mBinding.listCheckBox, mSelectedSizeIdHashSet);
            new Handler().postDelayed(() -> mActionModeState = 0, 310);
        }

        holder.mBinding.listDragHandle.setVisibility(mSort == SORT_CUSTOM && mActionModeState == ACTION_MODE_ON ? View.VISIBLE : View.GONE);
        holder.mBinding.listFavoriteCheckBox.setVisibility(mActionModeState == ACTION_MODE_ON ? View.GONE : View.VISIBLE);
    }

    public void onItemMove(int from, int to) {
        //checked
        mAdapterUtil.itemMove(from,to,mSizeList);
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

    public void sizeSelected(long id) {
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

        public SizeListVH(@NotNull ItemListRecyclerListBinding binding, SizeAdapterListener listener) {
            //checked
            super(binding.getRoot());
            this.mBinding = binding;

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
            mBinding.setSize(size);
        }

        public ItemListRecyclerListBinding getBinding() {
            return mBinding;
        }

    }
}
