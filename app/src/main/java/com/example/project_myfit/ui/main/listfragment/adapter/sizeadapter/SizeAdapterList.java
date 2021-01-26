package com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

public class SizeAdapterList extends ListAdapter<Size, SizeAdapterList.SizeListVH> {
    private final ListViewModel mModel;
    private List<Size> mSizeList;
    private SizeAdapterListener mListener;
    private int mActionModeState, mSort;
    private boolean mAnimOn;
    private Context mContext;
    private HashSet<Integer> mSelectedPosition;

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
        return new SizeListVH(binding);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull @NotNull SizeListVH holder, int position) {
        Size size = getItem(holder.getLayoutPosition());
        holder.mBinding.setSize(size);

        MaterialCardView cardView = holder.mBinding.listCardView;
        MaterialCheckBox checkBox = holder.mBinding.listCheckBox;
        ImageView dragHandle = holder.mBinding.listDragHandle;


        //click-------------------------------------------------------------------------------------
        cardView.setOnClickListener(v -> mListener.onSizeCardViewClick(size, checkBox, holder.getLayoutPosition()));
        cardView.setOnLongClickListener(v -> {
            mListener.onSizeCardViewLongClick(holder.getLayoutPosition());
            return true;
        });
        dragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                holder.itemView.setTranslationZ(10);
                mListener.onSizeDragHandTouch(holder);
            }
            return false;
        });
        checkBox.setOnClickListener(v -> mListener.onSizeCheckBoxClick(size, checkBox, holder.getLayoutPosition()));
        checkBox.setOnLongClickListener(v -> {
            mListener.onSizeCheckBoxLongCLick(holder.getLayoutPosition());
            return true;
        });
        //------------------------------------------------------------------------------------------

        //animation---------------------------------------------------------------------------------
        if (mActionModeState != 0) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) cardView.getLayoutParams();
            int startMargin = (int) mContext.getResources().getDimension(R.dimen._12sdp);
            int endMargin = (int) mContext.getResources().getDimension(R.dimen._32sdp);
            //actionModeON
            if (mActionModeState == ACTION_MODE_ON && mAnimOn) {
                checkBoxOpenAnim(cardView, params, startMargin, endMargin);
                setAnimOnFalse();
                checkBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
            } else if (mActionModeState == ACTION_MODE_ON) {
                params.setMarginStart(endMargin);
                params.setMarginEnd(0);
                cardView.setLayoutParams(params);
                checkBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
            } else if (mActionModeState == ACTION_MODE_OFF && mAnimOn) {
                checkBoxCloseAnim(cardView, params, startMargin, endMargin);
                checkBox.setChecked(false);
                if (mSelectedPosition.size() != 0)
                    mSelectedPosition.clear();
                setAnimOnFalse();
            } else if (mActionModeState == ACTION_MODE_OFF) {
                params.setMarginStart(startMargin);
                params.setMarginEnd(startMargin);
                cardView.setLayoutParams(params);
            }
        }
        //------------------------------------------------------------------------------------------

        if (mSort == SORT_CUSTOM)
            dragHandle.setVisibility(View.VISIBLE);
        else dragHandle.setVisibility(View.GONE);
    }

    //animation-------------------------------------------------------------------------------------
    private void checkBoxOpenAnim(MaterialCardView cardView, FrameLayout.LayoutParams params, int startMargin, int endMargin) {
        ValueAnimator leftAnimator = ValueAnimator.ofInt(startMargin, endMargin);
        leftAnimator.setDuration(300);
        leftAnimator.addUpdateListener(animation -> {
            params.setMarginStart((Integer) animation.getAnimatedValue());
            cardView.setLayoutParams(params);
        });
        leftAnimator.start();

        ValueAnimator rightAnimator = ValueAnimator.ofInt(startMargin, 0);
        rightAnimator.setDuration(300);
        rightAnimator.addUpdateListener(animation -> {
            params.setMarginEnd((Integer) animation.getAnimatedValue());
            cardView.setLayoutParams(params);
        });
        rightAnimator.start();
    }

    private void checkBoxCloseAnim(MaterialCardView cardView, FrameLayout.LayoutParams params, int startMargin, int endMargin) {
        ValueAnimator leftAnimator = ValueAnimator.ofInt(endMargin, startMargin);
        leftAnimator.setDuration(300);
        leftAnimator.addUpdateListener(animation -> {
            params.setMarginStart((Integer) animation.getAnimatedValue());
            cardView.setLayoutParams(params);
        });
        leftAnimator.start();

        ValueAnimator rightAnimator = ValueAnimator.ofInt(0, startMargin);
        rightAnimator.setDuration(300);
        rightAnimator.addUpdateListener(animation -> {
            params.setMarginEnd((Integer) animation.getAnimatedValue());
            cardView.setLayoutParams(params);
        });
        rightAnimator.start();
    }

    private void setAnimOnFalse() {
        new Handler().postDelayed(() -> {
            mAnimOn = false;
            mActionModeState = 0;
        }, 310);
    }
    //----------------------------------------------------------------------------------------------

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
        mModel.updateSizeList(mSizeList);
    }
    //----------------------------------------------------------------------------------------------

    //action mode & drag select---------------------------------------------------------------------
    public void setActionModeState(int actionModeState) {
        mActionModeState = actionModeState;
        mAnimOn = true;
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

    public static class SizeListVH extends RecyclerView.ViewHolder {
        private final ItemListRecyclerListBinding mBinding;

        public SizeListVH(ItemListRecyclerListBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
