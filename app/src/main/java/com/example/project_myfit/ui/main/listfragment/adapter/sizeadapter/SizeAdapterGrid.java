package com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemListRecyclerGridBinding;
import com.example.project_myfit.ui.main.listfragment.ListViewModel;
import com.example.project_myfit.ui.main.listfragment.database.Size;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;

public class SizeAdapterGrid extends ListAdapter<Size, SizeAdapterGrid.SizeGridVH> {

    private final ListViewModel mModel;
    private SizeAdapterListener mListener;
    private List<Size> mSizeList;
    private int mActionModeState;
    private final HashSet<Integer> mSelectedPosition = new HashSet<>();

    public SizeAdapterGrid(ListViewModel model) {
        super(new SizeDiffUtil());
        mModel = model;
        setHasStableIds(true);
    }

    public void setOnSizeAdapterListener(SizeAdapterListener listener) {
        mListener = listener;
    }

    public void setItem(List<Size> sizeList) {
        mSizeList = sizeList;
        submitList(sizeList);
    }

    @NonNull
    @NotNull
    @Override
    public SizeGridVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemListRecyclerGridBinding binding = ItemListRecyclerGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SizeGridVH(binding);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull @NotNull SizeGridVH holder, int position) {
        holder.mBinding.setSize(getItem(holder.getLayoutPosition()));
        if (getItem(holder.getLayoutPosition()).getImageUri() != null)
            holder.mBinding.addImageIcon.setVisibility(View.GONE);

        //click-------------------------------------------------------------------------------------
        holder.mBinding.gridCardView.setOnClickListener(v -> mListener.onCardViewClick(getCurrentList().get(holder.getLayoutPosition()), holder.mBinding.gridCheckBox, holder.getLayoutPosition()));
        holder.mBinding.gridCardView.setOnLongClickListener(v -> {
            mListener.onCardViewLongClick(getCurrentList().get(holder.getLayoutPosition()), holder.mBinding.gridCheckBox, holder.getLayoutPosition());
            return true;
        });
        holder.mBinding.gridDragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) mListener.onDragHandTouch(holder);
            return false;
        });
        holder.mBinding.gridCheckBox.setOnClickListener(v -> mListener.onCheckBoxClick(getCurrentList().get(holder.getLayoutPosition()), holder.mBinding.gridCheckBox, holder.getLayoutPosition()));
        //------------------------------------------------------------------------------------------

        //check box visibility----------------------------------------------------------------------
        if (mActionModeState == ACTION_MODE_ON)
            holder.mBinding.gridCheckBox.setVisibility(View.VISIBLE);
        else {
            holder.mBinding.gridCheckBox.setVisibility(View.GONE);
            holder.mBinding.gridCheckBox.setChecked(false);
            mSelectedPosition.clear();
        }
        //------------------------------------------------------------------------------------------

        holder.mBinding.gridCheckBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
    }

    @Override
    public long getItemId(int position) {
        return getCurrentList().get(position).getId();
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

    public void onItemDrop() {
        mModel.updateSizeOrder(mSizeList);
    }
    //----------------------------------------------------------------------------------------------

    //action mode & drag select---------------------------------------------------------------------
    public void setActionModeState(int actionModeState) {
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public int getActionModeState() {
        return mActionModeState;
    }

    //drag select-----------------------------------------------------------------------------------
    public void setSelectedPosition(int position) {
        if (!mSelectedPosition.contains(position)) mSelectedPosition.add(position);
        else mSelectedPosition.remove(position);
    }

    public void selectAll() {
        for (int i = 0; i < getCurrentList().size(); i++) {
            mSelectedPosition.add(i);
        }
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedPosition.clear();
    }
    //----------------------------------------------------------------------------------------------

    public static class SizeGridVH extends RecyclerView.ViewHolder {
        ItemListRecyclerGridBinding mBinding;

        public SizeGridVH(ItemListRecyclerGridBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
