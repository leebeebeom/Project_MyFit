package com.example.project_myfit.main.main.adapter;

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
import com.example.project_myfit.databinding.ItemCategoryBinding;
import com.example.project_myfit.main.main.MainViewModel;
import com.example.project_myfit.util.adapter.AdapterUtil;
import com.example.project_myfit.util.adapter.view_holder.CategoryVH;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;

@SuppressLint("ClickableViewAccessibility")
public class CategoryAdapter extends ListAdapter<Category, CategoryVH> {

    private List<Category> mCategoryList, mSelectedCategoryList;
    private final MainViewModel mModel;
    private final HashSet<Long> mSelectedCategoryIdHashSet;
    private final CategoryVH.CategoryVHListener mListener;
    private int mActionModeState, mSort;
    private Animation mAnimation;
    private List<Long> mFolderParentIdList, mSizeParentIdList;
    private MainViewPagerAdapter.MainViewPagerVH mMainViewPagerVH;
    private AdapterUtil mAdapterUtil;
    private boolean isDragging;

    public CategoryAdapter(MainViewModel model, CategoryVH.CategoryVHListener listener) {
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
                           List<Long> folderParentIdList, List<Long> sizeParentIdList) {
        super.submitList(list);
        this.mCategoryList = list;
        this.mFolderParentIdList = folderParentIdList;
        this.mSizeParentIdList = sizeParentIdList;
        this.mSort = sort;

        if (list != null && mMainViewPagerVH != null)
            mMainViewPagerVH.setNoData(list.isEmpty());
    }

    public void setViewPagerVH(MainViewPagerAdapter.MainViewPagerVH mainViewPagerVH) {
        this.mMainViewPagerVH = mainViewPagerVH;
    }

    @NonNull
    @NotNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        holder.getBinding().iconItemCategoryDragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !isDragging) {
                isDragging = true;
                mListener.onCategoryDragHandleTouch(holder);
                draggingView(holder);
            }
            return false;
        });

        holder.getBinding().tvItemCategoryContentsSize.setText(String.valueOf(mAdapterUtil.
                getContentsSize(category.getId(), mFolderParentIdList, mSizeParentIdList)));

        if (mActionModeState == ACTION_MODE_ON)
            mAdapterUtil.listActionModeOn(holder.getBinding().cardViewItemCategory, holder.getBinding().cbItemCategory,
                    mSelectedCategoryIdHashSet, category.getId());
        else if (mActionModeState == ACTION_MODE_OFF) {
            mAdapterUtil.listActionModeOff(holder.getBinding().cardViewItemCategory, holder.getBinding().cbItemCategory,
                    mSelectedCategoryIdHashSet);
            new Handler().postDelayed(() -> mActionModeState = 0, 301);
        }

        holder.getBinding().iconItemCategoryDragHandle.setVisibility(mSort == SORT_CUSTOM && mActionModeState == ACTION_MODE_ON ? View.VISIBLE : View.GONE);
    }

    private void draggingView(@NotNull CategoryVH holder) {
        holder.itemView.setTranslationZ(10);
        holder.getBinding().cbItemCategory.setVisibility(View.INVISIBLE);
        holder.getBinding().tvItemCategoryTitle.setAlpha(0.5f);
        holder.getBinding().tvItemCategoryContentsSizeLayout.setAlpha(0.5f);
    }

    private void dropView(@NotNull CategoryVH holder) {
        holder.itemView.setTranslationZ(0);
        holder.getBinding().cbItemCategory.setVisibility(View.VISIBLE);
        holder.getBinding().tvItemCategoryTitle.setAlpha(0.8f);
        holder.getBinding().tvItemCategoryContentsSizeLayout.setAlpha(0.8f);
    }

    public void onItemMove(int from, int to) {
        mAdapterUtil.itemMove(from, to, mCategoryList);
        notifyItemMoved(from, to);
    }

    public void onItemDrop(@NotNull RecyclerView.ViewHolder viewHolder) {
        mListener.onCategoryDragHandleTouch(viewHolder);
        mModel.categoryItemDrop(mCategoryList);
        dropView((CategoryVH) viewHolder);
        isDragging = false;
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
}
