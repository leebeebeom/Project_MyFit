package com.example.project_myfit.recyclebin.search.adapter;

import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.databinding.ItemCategoryBinding;
import com.example.project_myfit.util.adapter.AdapterUtil;
import com.example.project_myfit.util.adapter.viewholder.CategoryVH;
import com.example.project_myfit.util.adapter.viewholder.ViewPagerVH;
import com.example.project_myfit.util.ktw.KoreanTextMatcher;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_ON;

public class RecycleBinCategoryAdapter extends ListAdapter<Category, CategoryVH> implements Filterable {
    private final CategoryVH.CategoryVHListener mListener;
    private final HashSet<Long> mSelectedCategoryIdHashSet;
    private List<Long> mFolderParentIdList, mSizeParentIdList;
    private ViewPagerVH mViewPagerVH;
    private AdapterUtil mAdapterUtil;
    private List<Category> mSelectedCategoryList, mAllCategoryList;
    private int mActionModeState;

    public RecycleBinCategoryAdapter(CategoryVH.CategoryVHListener listener) {
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
        mListener = listener;
        mSelectedCategoryIdHashSet = new HashSet<>();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void setViewPagerVH(ViewPagerVH mViewPagerVH) {
        this.mViewPagerVH = mViewPagerVH;
    }

    public void setItem(List<Category> categoryList, CharSequence word, TabLayout.Tab tab, TypedValue colorControl,
                        List<Long> folderParentIdList, List<Long> sizeParentIdList) {
        mAllCategoryList = categoryList;
        mFolderParentIdList = folderParentIdList;
        mSizeParentIdList = sizeParentIdList;

        getFilter().filter(word, count -> {
            if (tab != null) {
                if (count == 0) tab.removeBadge();
                else {
                    BadgeDrawable badge = tab.getOrCreateBadge();
                    badge.setVisible(true);
                    badge.setNumber(count);
                    badge.setBackgroundColor(colorControl.data);
                }
            }
        });

        if (categoryList != null && mViewPagerVH != null)
            mViewPagerVH.setNoResult(categoryList.isEmpty());
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

        Category category = getItem(position);
        holder.setCategory(category);

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

        holder.getBinding().iconItemCategoryDragHandle.setVisibility(View.GONE);
    }

    public void setActionModeState(int actionModeState) {
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void setSelectedCategoryList(@NotNull List<Object> selectedCategoryList) {
        if (mSelectedCategoryList == null) mSelectedCategoryList = new ArrayList<>();
        for (Object o : selectedCategoryList) mSelectedCategoryList.add((Category) o);
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

    @Override
    public Filter getFilter() {
        return new RecycleBinSearchCategoryFilter();
    }

    private class RecycleBinSearchCategoryFilter extends Filter {

        @Override
        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
            String keyWord = constraint.toString().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
            List<Category> filteredList = new ArrayList<>();
            FilterResults filterResults = new FilterResults();
            if (!TextUtils.isEmpty(keyWord)) {
                for (Category category : mAllCategoryList) {
                    if (KoreanTextMatcher.isMatch(category.getCategoryName().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", ""), keyWord))
                        filteredList.add(category);
                }
            }
            submitList(filteredList);
            filterResults.count = filteredList.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (mViewPagerVH != null) mViewPagerVH.setNoResult(results.count == 0);
        }
    }
}
