package com.example.project_myfit.recyclebin.search.adapter;

import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.databinding.ItemFolderListBinding;
import com.example.project_myfit.util.adapter.AdapterUtil;
import com.example.project_myfit.util.adapter.viewholder.FolderListVH;
import com.example.project_myfit.util.adapter.viewholder.FolderVHListener;
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

public class RecycleBinFolderAdapter extends ListAdapter<Folder, FolderListVH> implements Filterable {
    private final FolderVHListener mListener;
    private final HashSet<Long> mSelectedFolderIdHashSet;
    private ViewPagerVH mViewPagerVH;
    private List<Folder> mAllFolderList, mSelectedFolderList;
    private List<Long> mFolderParentIdList, mSizeParentIdList;
    private AdapterUtil mAdapterUtil;
    private int mActionModeState;

    public RecycleBinFolderAdapter(FolderVHListener listener) {
        super(new DiffUtil.ItemCallback<Folder>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull Folder oldItem, @NonNull @NotNull Folder newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull Folder oldItem, @NonNull @NotNull Folder newItem) {
                return oldItem.getFolderName().equals(newItem.getFolderName()) &&
                        oldItem.getDummy() == newItem.getDummy();
            }
        });
        mListener = listener;
        mSelectedFolderIdHashSet = new HashSet<>();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void setViewPagerVH(ViewPagerVH mViewPagerVH) {
        this.mViewPagerVH = mViewPagerVH;
    }

    public void setItem(List<Folder> folderList, CharSequence word, TabLayout.Tab tab, TypedValue colorControl,
                        List<Long> folderParentIdList, List<Long> sizeParentIdList) {
        mAllFolderList = folderList;
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

        if (folderList != null && mViewPagerVH != null)
            mViewPagerVH.setNoResult(folderList.isEmpty());
    }

    @NonNull
    @NotNull
    @Override
    public FolderListVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemFolderListBinding binding = ItemFolderListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FolderListVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FolderListVH holder, int position) {
        if (mAdapterUtil == null) mAdapterUtil = new AdapterUtil(holder.itemView.getContext());

        if (mSelectedFolderList != null && !mSelectedFolderList.isEmpty()) {
            mAdapterUtil.restoreActionMode(mSelectedFolderList, mSelectedFolderIdHashSet);
            mSelectedFolderList = null;
        }


        Folder folder = getItem(position);
        holder.setFolder(folder);

        holder.getBinding().tvItemFolderListContentsSize.setText(String.valueOf(mAdapterUtil.
                getContentsSize(folder.getId(), mFolderParentIdList, mSizeParentIdList)));

        if (mActionModeState == ACTION_MODE_ON)
            mAdapterUtil.listActionModeOn(holder.getBinding().cardViewItemFolderList, holder.getBinding().cbItemFolderList,
                    mSelectedFolderIdHashSet, folder.getId());
        else if (mActionModeState == ACTION_MODE_OFF) {
            mAdapterUtil.listActionModeOff(holder.getBinding().cardViewItemFolderList, holder.getBinding().cbItemFolderList,
                    mSelectedFolderIdHashSet);
            new Handler().postDelayed(() -> mActionModeState = 0, 301);
        }
    }

    public void setActionModeState(int actionModeState) {
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void setSelectedFolderList(List<Folder> selectedFolderList) {
        this.mSelectedFolderList = selectedFolderList;
    }

    public void folderSelected(long id) {
        if (!mSelectedFolderIdHashSet.contains(id)) mSelectedFolderIdHashSet.add(id);
        else mSelectedFolderIdHashSet.remove(id);
    }

    public void selectAll() {
        for (Folder f : getCurrentList())
            mSelectedFolderIdHashSet.add(f.getId());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedFolderIdHashSet.clear();
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new RecycleBinSearchFolderFilter();
    }

    private class RecycleBinSearchFolderFilter extends Filter {

        @Override
        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
            String keyWord = constraint.toString().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
            List<Folder> filteredList = new ArrayList<>();
            FilterResults filterResults = new FilterResults();
            if (!TextUtils.isEmpty(keyWord)) {
                for (Folder folder : mAllFolderList) {
                    if (KoreanTextMatcher.isMatch(folder.getFolderName().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", ""), keyWord))
                        filteredList.add(folder);
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
