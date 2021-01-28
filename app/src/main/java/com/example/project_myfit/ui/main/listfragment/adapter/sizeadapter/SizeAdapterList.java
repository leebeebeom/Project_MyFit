package com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
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
    private List<Size> mSizeList;
    private int mActionModeState, mSort;
    private HashSet<Integer> mSelectedPosition;
    private Context mContext;

    public SizeAdapterList(ListViewModel model) {
        super(new SizeDiffUtil());
        mModel = model;
        setHasStableIds(true);
        mSelectedPosition = new HashSet<>();
    }

    @Override
    public long getItemId(int position) {
        return getCurrentList().get(position).getId();
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
    public SizeListVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        ItemListRecyclerListBinding binding = ItemListRecyclerListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SizeListVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SizeListVH holder, int position) {
        Size size = getItem(holder.getLayoutPosition());
        holder.mBinding.setSize(size);
        holder.setSize(size);

        MaterialCardView cardView = holder.mBinding.listCardView;
        MaterialCheckBox checkBox = holder.mBinding.listCheckBox;
        ImageView dragHandle = holder.mBinding.listDragHandle;
        //animation---------------------------------------------------------------------------------
        if (mActionModeState == ACTION_MODE_ON) {
            cardView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.recycler_list_slide_right));
            checkBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
        } else if (mActionModeState == ACTION_MODE_OFF) {
            cardView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.recycler_list_slide_left));
            checkBox.setChecked(false);
            setActionModeStateNone();
            if (mSelectedPosition.size() != 0) mSelectedPosition.clear();
        }
        //------------------------------------------------------------------------------------------

        if (mSort == SORT_CUSTOM)
            dragHandle.setVisibility(View.VISIBLE);
        else dragHandle.setVisibility(View.GONE);
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

    public void onItemDrop(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setTranslationZ(0);
        mListener.onSizeDragHandleTouch(viewHolder);
        mModel.updateSizeList(mSizeList);
    }
    //----------------------------------------------------------------------------------------------

    //action mode & drag select---------------------------------------------------------------------
    public void setActionModeState(int actionModeState) {
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public HashSet<Integer> getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(HashSet<Integer> selectedPosition) {
        this.mSelectedPosition = selectedPosition;
        notifyDataSetChanged();
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
        notifyDataSetChanged();
    }
    //----------------------------------------------------------------------------------------------

    public void setSort(int mSort) {
        this.mSort = mSort;
        notifyDataSetChanged();
    }

    public static class SizeListVH extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener, View.OnTouchListener {
        private final ItemListRecyclerListBinding mBinding;
        private final SizeAdapterListener mListener;
        private Size mSize;

        public SizeListVH(ItemListRecyclerListBinding binding, SizeAdapterListener listener) {
            super(binding.getRoot());
            mBinding = binding;
            mListener = listener;

            MaterialCardView cardView = mBinding.listCardView;
            MaterialCheckBox checkBox = mBinding.listCheckBox;
            ImageView dragHandle = mBinding.listDragHandle;

            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
            dragHandle.setOnTouchListener(this);

            checkBox.setEnabled(false);
            checkBox.setOnClickListener(v -> cardView.callOnClick());
            checkBox.setOnLongClickListener(v -> cardView.performLongClick());
        }

        public void setSize(Size size) {
            mSize = size;
        }

        @Override
        public void onClick(View v) {
            mListener.onSizeCardViewClick(mSize, this.mBinding.listCheckBox, getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onSizeCardViewLongClick(this.mBinding.listCardView, getLayoutPosition());
            return false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                itemView.setTranslationZ(10);
                mListener.onSizeDragHandleTouch(this);
            }
            return true;
        }
    }
}
