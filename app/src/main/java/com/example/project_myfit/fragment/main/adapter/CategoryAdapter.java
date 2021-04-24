package com.example.project_myfit.fragment.main.adapter;

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
import com.example.project_myfit.databinding.ItemMainRecyclerCategoryBinding;
import com.example.project_myfit.fragment.main.MainViewModel;
import com.example.project_myfit.util.adapter_utils.AdapterUtil;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;

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
    private AdapterUtil mAdapterUtil;
    private boolean mIsDragging;

    public CategoryAdapter(MainViewModel model, CategoryAdapterListener listener) {
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
        this.mSelectedCategoryIdHashSet = new HashSet<>();
        this.mListener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void submitList(int sort, @Nullable @org.jetbrains.annotations.Nullable List<Category> list,
                           List<Long> allFolderFolderId, List<Long> allSizeFolderId) {
        super.submitList(list);
        this.mCategoryList = list;
        this.mFolderFolderIdList = allFolderFolderId;
        this.mSizeFolderIdList = allSizeFolderId;
        this.mSort = sort;

        if (list != null && mViewPagerVH != null)
            mViewPagerVH.setNoData(list.isEmpty());
    }

    public void setViewPagerVH(MainViewPagerAdapter.ViewPagerVH viewPagerVH) {
        this.mViewPagerVH = viewPagerVH;
    }

    @NonNull
    @NotNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemMainRecyclerCategoryBinding binding = ItemMainRecyclerCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CategoryVH holder, int position) {
        if (mAdapterUtil == null) mAdapterUtil = new AdapterUtil(holder.itemView.getContext());

        if (mSelectedCategoryList != null && !mSelectedCategoryList.isEmpty()) {
            mAdapterUtil.restoreActionMode(mSelectedCategoryList, mSelectedCategoryIdHashSet);
            mSelectedCategoryList = null;
        }

        Category category = getItem(holder.getLayoutPosition());
        holder.setCategory(category);
        holder.mBinding.itemMainDragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !mIsDragging) {
                mIsDragging = true;
                mListener.onCategoryDragHandleTouch(holder);
                draggingView(holder);
            }
            return false;
        });

        holder.mBinding.itemMainContentsSizeText.setText(String.valueOf(mAdapterUtil.
                getCategoryContentsSize(category, mFolderFolderIdList, mSizeFolderIdList)));

        if (mActionModeState == ACTION_MODE_ON)
            mAdapterUtil.listActionModeOn(holder.mBinding.itemMainCardView, holder.mBinding.itemMainCheckBox,
                    mSelectedCategoryIdHashSet, category.getId());
        else if (mActionModeState == ACTION_MODE_OFF) {
            mAdapterUtil.listActionModeOff(holder.mBinding.itemMainCardView, holder.mBinding.itemMainCheckBox,
                    mSelectedCategoryIdHashSet);
            new Handler().postDelayed(() -> mActionModeState = 0, 301);
        }

        holder.mBinding.itemMainDragHandle.setVisibility(mSort == SORT_CUSTOM && mActionModeState == ACTION_MODE_ON ? View.VISIBLE : View.GONE);
    }

    private void draggingView(@NotNull CategoryVH holder) {
        holder.itemView.setTranslationZ(10);
        holder.mBinding.itemMainCheckBox.setVisibility(View.INVISIBLE);
        holder.mBinding.itemMainCategoryText.setAlpha(0.5f);
        holder.mBinding.itemMainContentsSizeLayout.setAlpha(0.5f);
    }

    private void dropView(@NotNull CategoryVH holder) {
        holder.itemView.setTranslationZ(0);
        holder.mBinding.itemMainCheckBox.setVisibility(View.VISIBLE);
        holder.mBinding.itemMainCategoryText.setAlpha(0.8f);
        holder.mBinding.itemMainContentsSizeLayout.setAlpha(0.8f);
    }

    public void onItemMove(int from, int to) {
        mAdapterUtil.itemMove(from, to, mCategoryList);
        notifyItemMoved(from, to);
    }

    public void onItemDrop(@NotNull RecyclerView.ViewHolder viewHolder) {
        mListener.onCategoryDragHandleTouch(viewHolder);
        mModel.categoryItemDrop(mCategoryList);
        dropView((CategoryVH) viewHolder);
        mIsDragging = false;
    }

    public void setActionModeState(int actionModeState) {
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void setSelectedCategoryList(List<Category> selectedCategoryList) {
        this.mSelectedCategoryList = selectedCategoryList;
    }

    public void categorySelected(long id) {
        if (!mSelectedCategoryIdHashSet.contains(id)) mSelectedCategoryIdHashSet.add(id);
        else mSelectedCategoryIdHashSet.remove(id);
    }

    public void selectAll() {
        for (Category c : getCurrentList())
            mSelectedCategoryIdHashSet.add(c.getId());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedCategoryIdHashSet.clear();
        notifyDataSetChanged();
    }

    public static class CategoryVH extends RecyclerView.ViewHolder {
        private final ItemMainRecyclerCategoryBinding mBinding;
        private Category mCategory;

        public CategoryVH(@NotNull ItemMainRecyclerCategoryBinding binding, CategoryAdapterListener listener) {
            super(binding.getRoot());
            this.mBinding = binding;

            itemView.setOnClickListener(v -> listener.onCategoryCardViewClick(mCategory, mBinding.itemMainCheckBox));

            itemView.setOnLongClickListener(v -> {
                listener.onCategoryCardViewLongClick(getLayoutPosition());
                return false;
            });
        }

        public void setCategory(Category category) {
            this.mCategory = category;
            mBinding.setCategory(category);
        }
    }

    public interface CategoryAdapterListener {
        void onCategoryCardViewClick(Category category, MaterialCheckBox checkBox);

        void onCategoryCardViewLongClick(int position);

        void onCategoryDragHandleTouch(RecyclerView.ViewHolder viewHolder);
    }
}
