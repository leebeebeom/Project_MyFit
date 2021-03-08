package com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemListRecyclerListBinding;
import com.example.project_myfit.ui.main.listfragment.ListViewModel;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

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
    private List<Size> mSizeList, mSelectedItem;
    private int mActionModeState, mSort;
    private final HashSet<Integer> mSelectedPosition;
    private Animation mAnimation;

    public SizeAdapterList(ListViewModel model) {
        super(new SizeDiffUtil());
        this.mModel = model;
        this.mSelectedPosition = new HashSet<>();
        setHasStableIds(true);
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
    public SizeListVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemListRecyclerListBinding binding = ItemListRecyclerListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SizeListVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SizeListVH holder, int position) {
        MaterialCardView cardView = holder.mBinding.listCardView;
        MaterialCheckBox checkBox = holder.mBinding.listCheckBox;
        ImageView dragHandle = holder.mBinding.listDragHandle;

        Size size = getItem(holder.getLayoutPosition());
        holder.mBinding.setSize(size);
        holder.setSize(size);

        if (mSelectedItem != null) {
            mSelectedPosition.clear();
            for (Size selectedItem : mSelectedItem) {
                int selectedPosition = getCurrentList().indexOf(selectedItem);
                mSelectedPosition.add(selectedPosition);
            }
            mSelectedItem = null;
        }
        //animation---------------------------------------------------------------------------------
        if (mActionModeState == ACTION_MODE_ON) {
            if (mAnimation == null)
                mAnimation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycler_list_slide_right);
            if (!mAnimation.hasStarted()) cardView.setAnimation(mAnimation);
            checkBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
            dragHandle.setVisibility(mSort == SORT_CUSTOM ? View.VISIBLE : View.GONE);
            holder.mBinding.listFavoriteCheckBox.setVisibility(View.GONE);
        } else if (mActionModeState == ACTION_MODE_OFF) {
            mAnimation = null;
            cardView.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycler_list_slide_left));
            checkBox.setChecked(false);
            setActionModeStateNone();
            if (!mSelectedPosition.isEmpty()) mSelectedPosition.clear();
            dragHandle.setVisibility(View.GONE);
            holder.mBinding.listFavoriteCheckBox.setVisibility(View.VISIBLE);
        }
    }

    private void setActionModeStateNone() {
        new Handler().postDelayed(() -> mActionModeState = 0, 310);
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
        mModel.getRepository().sizeUpdate(mSizeList);
        mListener.onSizeDragHandleTouch(viewHolder);
    }
    //----------------------------------------------------------------------------------------------

    //action mode & drag select---------------------------------------------------------------------
    public void setActionModeState(int actionModeState) {
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void setSelectedItem(List<Size> selectedItem) {
        this.mSelectedItem = selectedItem;
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

    public static class SizeListVH extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener, View.OnTouchListener {
        private final ItemListRecyclerListBinding mBinding;
        private final SizeAdapterListener mListener;
        private Size mSize;

        public SizeListVH(@NotNull ItemListRecyclerListBinding binding, SizeAdapterListener listener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.mListener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mBinding.listDragHandle.setOnTouchListener(this);
            mBinding.listFavoriteCheckBox.setOnClickListener(v -> mListener.onSizeFavoriteClick(mSize));
        }

        public void setSize(Size size) {
            mSize = size;
        }

        @Override
        public void onClick(View v) {
            mListener.onSizeItemViewClick(mSize, this.mBinding.listCheckBox, getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onSizeItemViewLongClick(this.mBinding.listCardView, getLayoutPosition());
            return false;
        }

        @Override
        public boolean onTouch(View v, @NotNull MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) mListener.onSizeDragHandleTouch(this);
            return false;
        }

        public ItemListRecyclerListBinding getBinding() {
            return mBinding;
        }
    }
}
