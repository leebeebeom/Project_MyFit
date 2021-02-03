package com.example.project_myfit.ui.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemMainRecyclerBinding;
import com.example.project_myfit.ui.main.MainViewModel;
import com.example.project_myfit.ui.main.database.Category;
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
public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryVH> {
    private List<Category> mCategoryList;
    private final MainViewModel mModel;
    private HashSet<Integer> mSelectedPosition;
    private CategoryAdapterListener mListener;
    private Context mContext;
    private int mActionModeState;
    private Animation mAnimation;
    private int mSort;


    public CategoryAdapter(MainViewModel model) {
        super(new CategoryDiffUtil());
        this.mModel = model;
        setHasStableIds(true);
        mSelectedPosition = new HashSet<>();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void setOnSizeAdapterListener(CategoryAdapterListener listener) {
        this.mListener = listener;
    }

    public void setItem(List<Category> categoryList) {
        this.mCategoryList = categoryList;
        submitList(categoryList);
    }

    @NonNull
    @NotNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        ItemMainRecyclerBinding binding = ItemMainRecyclerBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new CategoryVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CategoryVH holder, int position) {
        Category category = getItem(holder.getLayoutPosition());
        holder.mBinding.setCategory(category);
        holder.setCategory(category);

        MaterialCardView cardView = holder.mBinding.mainCardView;
        MaterialCheckBox checkBox = holder.mBinding.mainCheckBox;
        ImageView dragHandle = holder.mBinding.mainDragHandle;

        //animation---------------------------------------------------------------------------------
        if (mActionModeState == ACTION_MODE_ON) {
            if (mAnimation == null)
                mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.recycler_list_slide_right);
            if (!mAnimation.hasStarted()) cardView.setAnimation(mAnimation);
            checkBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
        } else if (mActionModeState == ACTION_MODE_OFF) {
            mAnimation = null;
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

    public void onItemMove(int from, int to) {
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(mCategoryList, i, i + 1);

                int toOrder = mCategoryList.get(i).getOrderNumber();
                int fromOrder = mCategoryList.get(i + 1).getOrderNumber();
                mCategoryList.get(i).setOrderNumber(fromOrder);
                mCategoryList.get(i + 1).setOrderNumber(toOrder);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(mCategoryList, i, i - 1);

                int toOrder = mCategoryList.get(i).getOrderNumber();
                int fromOrder = mCategoryList.get(i - 1).getOrderNumber();
                mCategoryList.get(i).setOrderNumber(fromOrder);
                mCategoryList.get(i - 1).setOrderNumber(toOrder);
            }
        }
        notifyItemMoved(from, to);
    }

    public void onItemDrop(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setTranslationZ(0);
        mListener.onCategoryDragHandleTouch(viewHolder);
        mModel.updateCategoryList(mCategoryList);
    }

    public void setActionModeState(int actionModeState) {
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public HashSet<Integer> getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(HashSet<Integer> selectedPosition) {
        this.mSelectedPosition = selectedPosition;
        mAnimation = null;
        notifyDataSetChanged();
    }

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

    public void setSort(int mSort) {
        this.mSort = mSort;
        notifyDataSetChanged();
    }


    public static class CategoryVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
        private final ItemMainRecyclerBinding mBinding;
        private CategoryAdapterListener mListener;
        private Category mCategory;

        public CategoryVH(ItemMainRecyclerBinding binding, CategoryAdapterListener listener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.mListener = listener;

            ImageView dragHandle = mBinding.mainDragHandle;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            dragHandle.setOnTouchListener(this);
        }

        public void setCategory(Category category) {
            this.mCategory = category;
        }

        @Override
        public void onClick(View v) {
            mListener.onCategoryCardViewClick(mCategory, this.mBinding.mainCheckBox, getLayoutPosition());

        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onCategoryCardViewLongClick(this.mBinding.mainCardView, getLayoutPosition());

            return false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                itemView.setTranslationZ(10);
                mListener.onCategoryDragHandleTouch(this);
            }
            return false;
        }
    }
}
