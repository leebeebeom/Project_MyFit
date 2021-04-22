package com.example.project_myfit.searchActivity.adapter;

import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemSearchFolderBinding;
import com.example.project_myfit.databinding.ItemSearchSizeBinding;
import com.example.project_myfit.util.adapter.AdapterUtil;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_ON;

public class SearchAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> implements Filterable {
    private List<Object> mOriginList, mSelectedItem;
    private final SearchAdapterListener mListener;
    private int mActionModeState;
    private final HashSet<Long> mSelectedItemIdHashSet;
    private SearchViewPagerAdapter.SearchViewPagerVH mSearchViewPagerVH;
    private AdapterUtil mFolderAdapterUtil, mSizeAdapterUtil;

    public SearchAdapter(SearchAdapterListener listener) {
        super(new SearchDiffUtil());
        this.mListener = listener;
        mSelectedItemIdHashSet = new HashSet<>();
        setHasStableIds(true);
    }

    public void setSearchViewPagerVH(SearchViewPagerAdapter.SearchViewPagerVH searchViewPagerVH) {
        this.mSearchViewPagerVH = searchViewPagerVH;
    }

    @Override
    public long getItemId(int position) {
        if (getCurrentList().get(position) instanceof Folder)
            return ((Folder) getCurrentList().get(position)).getId();
        else return ((Size) getCurrentList().get(position)).getId();
    }

    public void setItem(List<Object> allItemList, CharSequence word, TabLayout.Tab tab, TypedValue colorControl) {
        this.mOriginList = allItemList;
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
        if (getCurrentList().get(position) instanceof Folder) return 0;
        else return 1;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            ItemSearchFolderBinding binding = ItemSearchFolderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new SearchRecyclerFolderVH(binding, mListener);
        } else {
            ItemSearchSizeBinding binding = ItemSearchSizeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new SearchRecyclerSizeVH(binding, mListener);
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

        if (holder instanceof SearchRecyclerFolderVH) {
            Folder folder = (Folder) getItem(holder.getLayoutPosition());
            ((SearchRecyclerFolderVH) holder).setFolder(folder);

            int amount = 0;
            for (Object o : mOriginList)
                if (o instanceof Folder && ((Folder) o).getParentId() == folder.getId())
                    amount++;
                else if (o instanceof Size && ((Size) o).getParentId() == folder.getId())
                    amount++;

            ((SearchRecyclerFolderVH) holder).mFolderBinding.searchContentsSize.setText(String.valueOf(amount));

            if (mActionModeState == ACTION_MODE_ON)
                mFolderAdapterUtil.listActionModeOn(((SearchRecyclerFolderVH) holder).mFolderBinding.searchFolderCardView,
                        ((SearchRecyclerFolderVH) holder).mFolderBinding.searchFolderCheckBox, mSelectedItemIdHashSet, folder.getId());
            else if (mActionModeState == ACTION_MODE_OFF)
                mFolderAdapterUtil.listActionModeOff(((SearchRecyclerFolderVH) holder).mFolderBinding.searchFolderCardView,
                        ((SearchRecyclerFolderVH) holder).mFolderBinding.searchFolderCheckBox, mSelectedItemIdHashSet);
        } else {
            Size size = (Size) getItem(holder.getLayoutPosition());

            ((SearchRecyclerSizeVH) holder).setSize(size);

            if (mActionModeState == ACTION_MODE_ON)
                mSizeAdapterUtil.listActionModeOn(((SearchRecyclerSizeVH) holder).mSizeBinding.searchSizeCardView,
                        ((SearchRecyclerSizeVH) holder).mSizeBinding.searchSizeCheckBox, mSelectedItemIdHashSet, size.getId());
            else if (mActionModeState == ACTION_MODE_OFF)
                mSizeAdapterUtil.listActionModeOff(((SearchRecyclerSizeVH) holder).mSizeBinding.searchSizeCardView,
                        ((SearchRecyclerSizeVH) holder).mSizeBinding.searchSizeCheckBox, mSelectedItemIdHashSet);

            ((SearchRecyclerSizeVH) holder).mSizeBinding.searchFavoriteCheckBox.setClickable(mActionModeState != ACTION_MODE_ON);
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
            String filterString = constraint.toString().toLowerCase().trim();
            List<Object> filteredList = new ArrayList<>();
            if (!TextUtils.isEmpty(filterString)) {
                for (Object o : mOriginList)
                    if (o instanceof Folder) {
                        Folder folder = (Folder) o;
                        if (folder.getFolderName().toLowerCase().trim().contains(filterString))
                            filteredList.add((Folder) o);
                    } else {
                        Size size = (Size) o;
                        if (size.getBrand().toLowerCase().trim().contains(filterString) ||
                                size.getName().toLowerCase().trim().contains(filterString))
                            filteredList.add((Size) o);
                    }
            }
            submitList(filteredList);

            FilterResults results = new FilterResults();
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (mSearchViewPagerVH != null)
                mSearchViewPagerVH.setNoResult(results.count == 0);
        }
    }

    @Override
    public Filter getFilter() {
        return new SearchAdapterFilter();
    }

    public static class SearchRecyclerFolderVH extends RecyclerView.ViewHolder {
        private final ItemSearchFolderBinding mFolderBinding;
        private Folder mFolder;

        public SearchRecyclerFolderVH(@NotNull ItemSearchFolderBinding folderBinding, SearchAdapterListener listener) {
            super(folderBinding.getRoot());
            this.mFolderBinding = folderBinding;

            itemView.setOnClickListener(v -> listener.searchAdapterFolderClick(mFolder, mFolderBinding.searchFolderCheckBox));
            itemView.setOnLongClickListener(v -> {
                listener.searchAdapterFolderLongClick(getLayoutPosition());
                return false;
            });
        }

        public void setFolder(Folder folder) {
            mFolderBinding.setFolder(folder);
            this.mFolder = folder;
        }
    }

    public static class SearchRecyclerSizeVH extends RecyclerView.ViewHolder {
        private final ItemSearchSizeBinding mSizeBinding;
        private Size mSize;

        public SearchRecyclerSizeVH(@NotNull ItemSearchSizeBinding sizeBinding, SearchAdapterListener listener) {
            super(sizeBinding.getRoot());
            this.mSizeBinding = sizeBinding;

            itemView.setOnClickListener(v -> listener.searchAdapterSizeClick(mSize, mSizeBinding.searchSizeCheckBox));
            itemView.setOnLongClickListener(v -> {
                listener.searchAdapterSizeLongClick(getLayoutPosition());
                return false;
            });
            mSizeBinding.searchFavoriteCheckBox.setOnClickListener(v -> listener.searchAdapterFavoriteClick(mSize));
        }

        public void setSize(Size size) {
            mSizeBinding.setSize(size);
            this.mSize = size;
        }
    }

    public interface SearchAdapterListener {
        void searchAdapterSizeClick(Size size, MaterialCheckBox checkBox);

        void searchAdapterSizeLongClick(int position);

        void searchAdapterFavoriteClick(Size size);

        void searchAdapterFolderClick(Folder folder, MaterialCheckBox checkBox);

        void searchAdapterFolderLongClick(int position);
    }
}
