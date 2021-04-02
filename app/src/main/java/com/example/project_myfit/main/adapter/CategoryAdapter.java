package com.example.project_myfit.main.adapter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.databinding.ItemMainRecyclerBinding;
import com.example.project_myfit.main.MainViewModel;
import com.example.project_myfit.util.AdapterUtils;
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
    private List<Category> mCategoryList, mSelectedCategoryList;
    private final MainViewModel mModel;
    private final HashSet<Long> mSelectedItemIdList;
    private final CategoryAdapterListener mListener;
    private int mActionModeState, mSort;
    private Animation mAnimation;
    private List<Long> mFolderFolderIdList, mSizeFolderIdList;
    private MainViewPagerAdapter.ViewPagerVH mViewPagerVH;

    public CategoryAdapter(MainViewModel model, CategoryAdapterListener listener) {
        //checked
        super(new DiffUtil.ItemCallback<Category>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull Category oldItem, @NonNull @NotNull Category newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull Category oldItem, @NonNull @NotNull Category newItem) {
                return oldItem.getCategoryName().equals(newItem.getCategoryName()) &&
                        oldItem.getDummy() == newItem.getDummy();
            }
        });
        this.mModel = model;
        setHasStableIds(true);
        this.mSelectedItemIdList = new HashSet<>();
        this.mListener = listener;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void submitList(int sort, @Nullable @org.jetbrains.annotations.Nullable List<Category> list,
                           List<Long> allFolderFolderId, List<Long> allSizeFolderId) {
        //checked
        super.submitList(list);
        this.mCategoryList = list;
        this.mFolderFolderIdList = allFolderFolderId;
        this.mSizeFolderIdList = allSizeFolderId;
        this.mSort = sort;

        if (list != null && mViewPagerVH != null)
            mViewPagerVH.setNoData(list.isEmpty());
    }

    public void setViewPagerVH(MainViewPagerAdapter.ViewPagerVH viewPagerVH) {
        //checked
        this.mViewPagerVH = viewPagerVH;
    }

    @NonNull
    @NotNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        //checked
        ItemMainRecyclerBinding binding = ItemMainRecyclerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CategoryVH holder, int position) {
        //checked
        if (mSelectedCategoryList != null) {
            mSelectedItemIdList.clear();
            for (Category selectedItem : mSelectedCategoryList)
                mSelectedItemIdList.add(selectedItem.getId());
            mSelectedCategoryList = null;
        }

        Category category = getItem(holder.getLayoutPosition());
        holder.mBinding.setCategory(category);
        holder.setCategory(category);

        holder.setActionMode(mActionModeState, mSelectedItemIdList, mSort);
        if (mActionModeState == ACTION_MODE_OFF)
            new Handler().postDelayed(() -> mActionModeState = 0, 301);

        int amount = 0;
        for (Long l : mFolderFolderIdList)
            if (l == category.getId()) amount++;
        for (Long l : mSizeFolderIdList)
            if (l == category.getId()) amount++;
        holder.mBinding.mainItemAmount.setText(String.valueOf(amount));
    }

    public void onItemMove(int from, int to) {
        //checked
        if (from < to) {//down
            for (int i = from; i < to; i++) {
                Collections.swap(mCategoryList, i, i + 1);

                int toOrder = mCategoryList.get(i).getOrderNumber();
                int fromOrder = mCategoryList.get(i + 1).getOrderNumber();
                mCategoryList.get(i).setOrderNumber(fromOrder);
                mCategoryList.get(i + 1).setOrderNumber(toOrder);
            }
        } else {//up
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
        //check
        mModel.getRepository().categoryUpdate(mCategoryList);
        mListener.onCategoryDragHandleTouch(viewHolder);
        mModel.getSelectedCategoryList().clear();
        for (Category c : getCurrentList())
            if (mSelectedItemIdList.contains(c.getId())) mModel.getSelectedCategoryList().add(c);
    }

    public void setActionModeState(int actionModeState) {
        //checked
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void setSelectedItem(List<Category> mSelectedItem) {
        //checked
        this.mSelectedCategoryList = mSelectedItem;
    }

    public void setSelectedPosition(long id) {
        //checked
        if (!mSelectedItemIdList.contains(id)) mSelectedItemIdList.add(id);
        else mSelectedItemIdList.remove(id);
    }

    public void selectAll() {
        //checked
        for (Category c : getCurrentList())
            mSelectedItemIdList.add(c.getId());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        //checked
        mSelectedItemIdList.clear();
        notifyDataSetChanged();
    }

    public static class CategoryVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
        //all checked
        private final ItemMainRecyclerBinding mBinding;
        private final CategoryAdapterListener mListener;
        private Category mCategory;
        private final AdapterUtils mAdapterUtils;

        public CategoryVH(@NotNull ItemMainRecyclerBinding binding, CategoryAdapterListener listener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.mListener = listener;
            this.mAdapterUtils = new AdapterUtils(itemView.getContext());

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mBinding.mainDragHandle.setOnTouchListener(this);
        }

        public void setCategory(Category category) {
            this.mCategory = category;
        }

        @Override
        public void onClick(View v) {
            mListener.onCategoryCardViewClick(mCategory, this.mBinding.mainCheckBox);
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onCategoryCardViewLongClick(getLayoutPosition());
            return false;
        }

        @Override
        public boolean onTouch(View v, @NotNull MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                mListener.onCategoryDragHandleTouch(this);
            return false;
        }

        public ItemMainRecyclerBinding getBinding() {
            return mBinding;
        }

        public void setActionMode(int actionModeState, HashSet<Long> selectedItemIdList, int sort) {
            //checked
            if (actionModeState == ACTION_MODE_ON) {
                mAdapterUtils.listActionModeOn(mBinding.mainCardView, mBinding.mainCheckBox, selectedItemIdList, mCategory.getId());
                mBinding.mainDragHandle.setVisibility(sort == SORT_CUSTOM ? View.VISIBLE : View.GONE);
            } else if (actionModeState == ACTION_MODE_OFF) {
                mAdapterUtils.listActionModeOff();
                mBinding.mainDragHandle.setVisibility(View.GONE);
                if (selectedItemIdList.size() != 0) selectedItemIdList.clear();
            }
        }
    }

    public interface CategoryAdapterListener {
        //all checked
        void onCategoryCardViewClick(Category category, MaterialCheckBox checkBox);

        void onCategoryCardViewLongClick(int position);

        void onCategoryDragHandleTouch(RecyclerView.ViewHolder viewHolder);
    }
}
