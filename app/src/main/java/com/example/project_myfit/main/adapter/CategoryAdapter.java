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
    private final HashSet<Long> mSelectedCategoryIdHashSet;
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
        this.mSelectedCategoryIdHashSet = new HashSet<>();
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
            mSelectedCategoryIdHashSet.clear();
            for (Category selectedCategory : mSelectedCategoryList)
                mSelectedCategoryIdHashSet.add(selectedCategory.getId());
            mSelectedCategoryList = null;
        }

        Category category = getItem(holder.getLayoutPosition());
        holder.mBinding.setCategory(category);
        holder.setCategory(category);
        holder.setContentsSize(mFolderFolderIdList, mSizeFolderIdList);

        holder.setActionMode(mActionModeState, mSelectedCategoryIdHashSet, mSort);
        if (mActionModeState == ACTION_MODE_OFF)
            new Handler().postDelayed(() -> mActionModeState = 0, 301);
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
            if (mSelectedCategoryIdHashSet.contains(c.getId())) mModel.getSelectedCategoryList().add(c);
        ((CategoryVH) viewHolder).mIsDragging = true;
    }

    public void setActionModeState(int actionModeState) {
        //checked
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void setSelectedCategoryList(List<Category> selectedCategoryList) {
        //checked
        this.mSelectedCategoryList = selectedCategoryList;
    }

    public void setSelectedPosition(long id) {
        //checked
        if (!mSelectedCategoryIdHashSet.contains(id)) mSelectedCategoryIdHashSet.add(id);
        else mSelectedCategoryIdHashSet.remove(id);
    }

    public void selectAll() {
        //checked
        for (Category c : getCurrentList())
            mSelectedCategoryIdHashSet.add(c.getId());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        //checked
        mSelectedCategoryIdHashSet.clear();
        notifyDataSetChanged();
    }

    public static class CategoryVH extends RecyclerView.ViewHolder {
        //all checked
        private final ItemMainRecyclerBinding mBinding;
        private Category mCategory;
        private final AdapterUtils mAdapterUtils;
        private boolean mIsDragging;

        public CategoryVH(@NotNull ItemMainRecyclerBinding binding, CategoryAdapterListener listener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.mAdapterUtils = new AdapterUtils(itemView.getContext());

            itemView.setOnClickListener(v -> listener.onCategoryCardViewClick(mCategory, mBinding.mainCheckBox));

            itemView.setOnLongClickListener(v -> {
                listener.onCategoryCardViewLongClick(getLayoutPosition());
                return false;
            });

            mBinding.mainDragHandle.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !mIsDragging) {
                    mIsDragging = true;
                    listener.onCategoryDragHandleTouch(this);
                }
                return false;
            });
        }

        public void setCategory(Category category) {
            this.mCategory = category;
        }

        public void setContentsSize(List<Long> folderFolderIdList, List<Long> sizeFolderIdList) {
            mBinding.mainContentsSize.setText(String.valueOf(mAdapterUtils.
                    getCategoryContentsSize(mCategory, folderFolderIdList, sizeFolderIdList)));
        }

        public ItemMainRecyclerBinding getBinding() {
            return mBinding;
        }

        public void setActionMode(int actionModeState, HashSet<Long> selectedItemIHashSet, int sort) {
            //checked
            mBinding.mainDragHandle.setVisibility(sort == SORT_CUSTOM && actionModeState == ACTION_MODE_ON ? View.VISIBLE : View.GONE);

            if (actionModeState == ACTION_MODE_ON)
                mAdapterUtils.listActionModeOn(mBinding.mainCardView, mBinding.mainCheckBox, selectedItemIHashSet, mCategory.getId());
            else if (actionModeState == ACTION_MODE_OFF) {
                mAdapterUtils.listActionModeOff(mBinding.mainCardView, mBinding.mainCheckBox);
                if (selectedItemIHashSet.size() != 0) selectedItemIHashSet.clear();
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
