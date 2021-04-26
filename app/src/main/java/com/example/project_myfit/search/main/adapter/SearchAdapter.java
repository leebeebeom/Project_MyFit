package com.example.project_myfit.search.main.adapter;

import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemFolderListBinding;
import com.example.project_myfit.databinding.ItemSizeListBinding;
import com.example.project_myfit.util.adapter.AdapterUtil;
import com.example.project_myfit.util.adapter.view_holder.FolderListVH;
import com.example.project_myfit.util.adapter.view_holder.FolderVHListener;
import com.example.project_myfit.util.adapter.view_holder.SizeListVH;
import com.example.project_myfit.util.adapter.view_holder.SizeVHListener;
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

public class SearchAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> implements Filterable {
    private List<Object> mAllItemList, mSelectedItem;
    private final SizeVHListener mSizeListener;
    private final FolderVHListener mFolderListener;
    private int mActionModeState;
    private final HashSet<Long> mSelectedItemIdHashSet;
    private SearchViewPagerAdapter.ViewPagerVH mViewPagerVH;
    private AdapterUtil mFolderAdapterUtil, mSizeAdapterUtil;

    public SearchAdapter(SizeVHListener sizeVHListener, FolderVHListener folderVHListener) {
        super(new SearchDiffUtil());
        this.mSizeListener = sizeVHListener;
        this.mFolderListener = folderVHListener;
        mSelectedItemIdHashSet = new HashSet<>();
        setHasStableIds(true);
    }

    public void setSearchViewPagerVH(SearchViewPagerAdapter.ViewPagerVH viewPagerVH) {
        this.mViewPagerVH = viewPagerVH;
    }

    @Override
    public long getItemId(int position) {
        if (getItem(position) instanceof Folder)
            return ((Folder) getItem(position)).getId();
        else return ((Size) getItem(position)).getId();
    }

    public void setItem(List<Object> allItemList, CharSequence word, TabLayout.Tab tab, TypedValue colorControl) {
        this.mAllItemList = allItemList;
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
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Folder) return 0;
        else return 1;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            ItemFolderListBinding binding = ItemFolderListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new FolderListVH(binding, mFolderListener);
        } else {
            ItemSizeListBinding binding = ItemSizeListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new SizeListVH(binding, mSizeListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (mFolderAdapterUtil == null)
            mFolderAdapterUtil = new AdapterUtil(holder.itemView.getContext());
        if (mSizeAdapterUtil == null)
            mSizeAdapterUtil = new AdapterUtil(holder.itemView.getContext());

        if (mSelectedItem != null && !mSelectedItem.isEmpty()) {
            mFolderAdapterUtil.restoreActionMode(mSelectedItem, mSelectedItemIdHashSet);
            mSelectedItem = null;
        }

        if (holder instanceof FolderListVH) {
            Folder folder = (Folder) getItem(holder.getLayoutPosition());
            ((FolderListVH) holder).setFolder(folder);

            int amount = 0;
            for (Object o : mAllItemList)
                if (o instanceof Folder && ((Folder) o).getParentId() == folder.getId())
                    amount++;
                else if (o instanceof Size && ((Size) o).getParentId() == folder.getId())
                    amount++;

            ((FolderListVH) holder).getBinding().tvItemFolderListContentsSize.setText(String.valueOf(amount));

            if (mActionModeState == ACTION_MODE_ON)
                mFolderAdapterUtil.listActionModeOn(((FolderListVH) holder).getBinding().cardViewItemFolderList,
                        ((FolderListVH) holder).getBinding().cbItemFolderList, mSelectedItemIdHashSet, folder.getId());
            else if (mActionModeState == ACTION_MODE_OFF)
                mFolderAdapterUtil.listActionModeOff(((FolderListVH) holder).getBinding().cardViewItemFolderList,
                        ((FolderListVH) holder).getBinding().cbItemFolderList, mSelectedItemIdHashSet);
        } else {
            Size size = (Size) getItem(holder.getLayoutPosition());
            ((SizeListVH) holder).setSize(size);

            if (mActionModeState == ACTION_MODE_ON)
                mSizeAdapterUtil.listActionModeOn(((SizeListVH) holder).getBinding().cardViewItemSizeList,
                        ((SizeListVH) holder).getBinding().cbItemSizeList, mSelectedItemIdHashSet, size.getId());
            else if (mActionModeState == ACTION_MODE_OFF)
                mSizeAdapterUtil.listActionModeOff(((SizeListVH) holder).getBinding().cardViewItemSizeList,
                        ((SizeListVH) holder).getBinding().cbItemSizeList, mSelectedItemIdHashSet);

            ((SizeListVH) holder).getBinding().cbItemSizeListFavorite.setClickable(mActionModeState != ACTION_MODE_ON);
            ((SizeListVH) holder).getBinding().iconItemSizeListDragHandle.setVisibility(View.GONE);
        }
        if (mActionModeState == ACTION_MODE_OFF)
            new Handler().postDelayed(() -> mActionModeState = 0, 301);
    }

    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }

    public void setActionModeState(int actionModeState) {
        this.mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void selectAll() {
        for (Object o : getCurrentList())
            if (o instanceof Folder)
                mSelectedItemIdHashSet.add(((Folder) o).getId());
            else if (o instanceof Size)
                mSelectedItemIdHashSet.add(((Size) o).getId());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedItemIdHashSet.clear();
        notifyDataSetChanged();
    }

    public void itemSelected(long id) {
        if (!mSelectedItemIdHashSet.contains(id)) mSelectedItemIdHashSet.add(id);
        else mSelectedItemIdHashSet.remove(id);
    }

    public void setSelectedItem(List<Object> selectedItem) {
        this.mSelectedItem = selectedItem;
    }

    private class SearchAdapterFilter extends Filter {
        @Override
        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
            String keyWord = constraint.toString().toLowerCase(Locale.getDefault()).replaceAll("\\p{Z}", "");
            List<Object> filteredList = new ArrayList<>();

            if (!TextUtils.isEmpty(keyWord)) {
                for (Object o : mAllItemList)
                    if (o instanceof Folder) {
                        Folder folder = (Folder) o;
                        if (KoreanTextMatcher.isMatch(folder.getFolderName().toLowerCase().replaceAll("\\p{Z}", ""), keyWord))
                            filteredList.add(folder);
                    } else {
                        Size size = (Size) o;
                        if (KoreanTextMatcher.isMatch(size.getBrand().toLowerCase().replaceAll("\\p{Z}", ""), keyWord) ||
                                KoreanTextMatcher.isMatch(size.getName().toLowerCase().replaceAll("\\p{Z}", ""), keyWord))
                            filteredList.add(size);
                    }
            }
            submitList(filteredList);

            FilterResults results = new FilterResults();
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (mViewPagerVH != null) mViewPagerVH.setNoResult(results.count == 0);
        }
    }

    @Override
    public Filter getFilter() {
        return new SearchAdapterFilter();
    }
}
