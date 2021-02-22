package com.example.project_myfit.ui.main.adapter;

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
import androidx.recyclerview.widget.DiffUtil;
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
    private final CategoryAdapterListener mListener;
    private int mActionModeState;
    private Animation mAnimation;
    private int mSort;
    private final int mViewPagerPosition;

    public CategoryAdapter(MainViewModel model, CategoryAdapterListener listener, int viewPagerPosition) {
        super(new DiffUtil.ItemCallback<Category>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull Category oldItem, @NonNull @NotNull Category newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull Category oldItem, @NonNull @NotNull Category newItem) {
                return oldItem.getCategory().equals(newItem.getCategory()) &&
                        oldItem.getItemAmount().equals(newItem.getItemAmount());
            }
        });
        this.mModel = model;
        setHasStableIds(true);
        mSelectedPosition = new HashSet<>();
        this.mListener = listener;
        this.mViewPagerPosition = viewPagerPosition;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public void submitList(@Nullable @org.jetbrains.annotations.Nullable List<Category> list) {
        super.submitList(list);
        this.mCategoryList = list;
    }

    @NonNull
    @NotNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemMainRecyclerBinding binding = ItemMainRecyclerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryVH(binding, mListener, mViewPagerPosition);
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
                mAnimation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycler_list_slide_right);
            if (!mAnimation.hasStarted()) cardView.setAnimation(mAnimation);
            checkBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
        } else if (mActionModeState == ACTION_MODE_OFF) {
            mAnimation = null;
            cardView.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycler_list_slide_left));
            checkBox.setChecked(false);
            setActionModeStateNone();
            if (mSelectedPosition.size() != 0) mSelectedPosition.clear();
        }
        //------------------------------------------------------------------------------------------
        dragHandle.setVisibility(mSort == SORT_CUSTOM ? View.VISIBLE : View.GONE);
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

    public void onItemDrop(@NotNull RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setTranslationZ(0);
        mModel.getRepository().categoryUpdate(mCategoryList);
        mListener.onCategoryDragHandleTouch(viewHolder, mViewPagerPosition);
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
        notifyDataSetChanged();
    }

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

    public void setSort(int sort) {
        if (mSort != sort) {
            this.mSort = sort;
            notifyDataSetChanged();
        }
    }


    public static class CategoryVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
        private final ItemMainRecyclerBinding mBinding;
        private final CategoryAdapterListener mListener;
        private Category mCategory;
        int mViewPagerPosition;

        public CategoryVH(@NotNull ItemMainRecyclerBinding binding, CategoryAdapterListener listener, int viewPagerPosition) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.mListener = listener;
            this.mViewPagerPosition = viewPagerPosition;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mBinding.mainDragHandle.setOnTouchListener(this);
        }

        public void setCategory(Category category) {
            this.mCategory = category;
        }

        @Override
        public void onClick(View v) {
            mListener.onCategoryCardViewClick(mCategory, this.mBinding.mainCheckBox, getLayoutPosition(), mViewPagerPosition);
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onCategoryCardViewLongClick(this.mBinding.mainCardView, getLayoutPosition());
            return false;
        }

        @Override
        public boolean onTouch(View v, @NotNull MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                itemView.setTranslationZ(10);
                mListener.onCategoryDragHandleTouch(this, mViewPagerPosition);
            }
            return false;
        }
    }

    public interface CategoryAdapterListener {
        void onCategoryCardViewClick(Category category, MaterialCheckBox checkBox, int position, int viewPagerPosition);

        void onCategoryCardViewLongClick(MaterialCardView cardView, int position);

        void onCategoryDragHandleTouch(RecyclerView.ViewHolder viewHolder, int viewPagerPosition);
    }
}
