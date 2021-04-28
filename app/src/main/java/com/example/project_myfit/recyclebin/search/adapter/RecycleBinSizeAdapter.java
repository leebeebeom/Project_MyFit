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

import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemSizeListBinding;
import com.example.project_myfit.util.adapter.AdapterUtil;
import com.example.project_myfit.util.adapter.viewholder.SizeListVH;
import com.example.project_myfit.util.adapter.viewholder.SizeVHListener;
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

public class RecycleBinSizeAdapter extends ListAdapter<Size, SizeListVH> implements Filterable {
    private final SizeVHListener mListener;
    private final HashSet<Long> mSelectedSizeIdHashSet;
    private ViewPagerVH mViewPagerVH;
    private List<Size> mAllSizeList, mSelectedSizeList;
    private AdapterUtil mAdapterUtil;
    private int mActionModeState;

    public RecycleBinSizeAdapter(SizeVHListener listener) {
        super(new DiffUtil.ItemCallback<Size>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull Size oldItem, @NonNull @NotNull Size newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull Size oldItem, @NonNull @NotNull Size newItem) {
                return oldItem.getBrand().equals(newItem.getBrand());
            }
        });
        mListener = listener;
        mSelectedSizeIdHashSet = new HashSet<>();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void setViewPagerVH(ViewPagerVH mViewPagerVH) {
        this.mViewPagerVH = mViewPagerVH;
    }

    public void setItem(List<Size> sizeList, CharSequence word, TabLayout.Tab tab, TypedValue colorControl) {
        mAllSizeList = sizeList;

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

        if (sizeList != null && mViewPagerVH != null)
            mViewPagerVH.setNoResult(sizeList.isEmpty());
    }

    @NonNull
    @NotNull
    @Override
    public SizeListVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemSizeListBinding binding = ItemSizeListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SizeListVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SizeListVH holder, int position) {
        if (mAdapterUtil == null) mAdapterUtil = new AdapterUtil(holder.itemView.getContext());

        if (mSelectedSizeList != null && !mSelectedSizeList.isEmpty()) {
            mAdapterUtil.restoreActionMode(mSelectedSizeList, mSelectedSizeIdHashSet);
            mSelectedSizeList = null;
        }

        Size size = getItem(position);
        holder.setSize(size);

        if (mActionModeState == ACTION_MODE_ON)
            mAdapterUtil.listActionModeOn(holder.getBinding().cardViewItemSizeList, holder.getBinding().cbItemSizeList,
                    mSelectedSizeIdHashSet, size.getId());
        else if (mActionModeState == ACTION_MODE_OFF) {
            mAdapterUtil.listActionModeOff(holder.getBinding().cardViewItemSizeList, holder.getBinding().cbItemSizeList,
                    mSelectedSizeIdHashSet);
            new Handler().postDelayed(() -> mActionModeState = 0, 301);
        }

        holder.getBinding().iconItemSizeListDragHandle.setVisibility(View.GONE);
        holder.getBinding().cbItemSizeListFavorite.setClickable(false);
    }

    public void setActionModeState(int actionModeState) {
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void setSelectedFolderList(List<Size> selectedSizeList) {
        this.mSelectedSizeList = selectedSizeList;
    }

    public void sizeSelected(long id) {
        if (!mSelectedSizeIdHashSet.contains(id)) mSelectedSizeIdHashSet.add(id);
        else mSelectedSizeIdHashSet.remove(id);
    }

    public void selectAll() {
        for (Size s : getCurrentList())
            mSelectedSizeIdHashSet.add(s.getId());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedSizeIdHashSet.clear();
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new RecycleBinSearchSizeFilter();
    }

    private class RecycleBinSearchSizeFilter extends Filter {

        @Override
        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
            String keyWord = constraint.toString().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
            List<Size> filteredList = new ArrayList<>();
            FilterResults filterResults = new FilterResults();
            if (!TextUtils.isEmpty(keyWord)) {
                for (Size size : mAllSizeList) {
                    if (KoreanTextMatcher.isMatch(size.getBrand().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", ""), keyWord))
                        filteredList.add(size);
                    else if (KoreanTextMatcher.isMatch(size.getSize().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", ""), keyWord))
                        filteredList.add(size);
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
