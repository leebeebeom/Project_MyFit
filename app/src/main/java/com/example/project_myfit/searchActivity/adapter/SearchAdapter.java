package com.example.project_myfit.searchActivity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemSearchFolderBinding;
import com.example.project_myfit.databinding.ItemSearchSizeBinding;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;

public class SearchAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> implements Filterable {
    private List<Object> mOriginList;
    private final SearchAdapterListener mListener;
    private int mActionModeState;
    private final HashSet<Integer> mSelectedPosition;

    public SearchAdapter(SearchAdapterListener listener) {
        super(new SearchDiffUtil());
        this.mListener = listener;
        mSelectedPosition = new HashSet<>();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (getCurrentList().get(position) instanceof Folder)
            return ((Folder) getCurrentList().get(position)).getId();
        else return ((Size) getCurrentList().get(position)).getId();
    }

    public void setItem(List<Object> list) {
        this.mOriginList = list;
        submitList(list);
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
        if (holder instanceof SearchRecyclerFolderVH) {
            Folder folder = (Folder) getCurrentList().get(position);

            if (mActionModeState != ACTION_MODE_ON) {
                ((SearchRecyclerFolderVH) holder).mFolderBinding.setFolder(folder);
                ((SearchRecyclerFolderVH) holder).setFolder(folder);
            }

            MaterialCheckBox checkBox = ((SearchRecyclerFolderVH) holder).mFolderBinding.searchFolderCheckBox;
            if (mActionModeState == ACTION_MODE_ON) {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
            } else {
                checkBox.setVisibility(View.GONE);
                checkBox.setChecked(false);
                if (!mSelectedPosition.isEmpty()) mSelectedPosition.clear();
            }
        } else {
            Size size = (Size) getCurrentList().get(position);

            if (mActionModeState != ACTION_MODE_ON) {
                ((SearchRecyclerSizeVH) holder).mSizeBinding.setSize(size);
                ((SearchRecyclerSizeVH) holder).setSize(size);
            }

            MaterialCheckBox checkBox = ((SearchRecyclerSizeVH) holder).mSizeBinding.searchSizeCheckBox;
            if (mActionModeState == ACTION_MODE_ON) {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
            } else {
                checkBox.setVisibility(View.GONE);
                checkBox.setChecked(false);
                if (!mSelectedPosition.isEmpty()) mSelectedPosition.clear();
            }
        }
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
        for (int i = 0; i < getCurrentList().size(); i++) mSelectedPosition.add(i);
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedPosition.clear();
        notifyDataSetChanged();
    }

    public HashSet<Integer> getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(int position) {
        if (!mSelectedPosition.contains(position)) mSelectedPosition.add(position);
        else mSelectedPosition.remove(position);
    }

    private class SearchAdapterFilter extends Filter {
        @Override
        protected FilterResults performFiltering(@NotNull CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase().trim();
            List<Object> filteredString = new ArrayList<>();

            for (Object o : mOriginList)
                if (o instanceof Folder) {
                    Folder folder = (Folder) o;
                    if (folder.getParentCategory().toLowerCase().trim().contains(filterString) ||
                            folder.getFolderName().toLowerCase().trim().contains(filterString))
                        filteredString.add(o);
                } else {
                    Size size = (Size) o;
                    if (size.getParentCategory().toLowerCase().trim().contains(filterString) ||
                            size.getBrand().toLowerCase().trim().contains(filterString) ||
                            size.getName().toLowerCase().trim().contains(filterString))
                        filteredString.add(o);
                }
            submitList(filteredString);
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

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

            itemView.setOnClickListener(v -> listener.searchAdapterFolderClick(mFolder, mFolderBinding.searchFolderCheckBox, getLayoutPosition()));
            itemView.setOnLongClickListener(v -> {
                listener.searchAdapterFolderLongClick(getLayoutPosition());
                return false;
            });
        }

        public void setFolder(Folder folder) {
            this.mFolder = folder;
        }
    }

    public static class SearchRecyclerSizeVH extends RecyclerView.ViewHolder {
        private final ItemSearchSizeBinding mSizeBinding;
        private Size mSize;

        public SearchRecyclerSizeVH(@NotNull ItemSearchSizeBinding sizeBinding, SearchAdapterListener listener) {
            super(sizeBinding.getRoot());
            this.mSizeBinding = sizeBinding;

            itemView.setOnClickListener(v -> listener.searchAdapterSizeClick(mSize, mSizeBinding.searchSizeCheckBox, getLayoutPosition()));
            itemView.setOnLongClickListener(v -> {
                listener.searchAdapterSizeLongClick(getLayoutPosition());
                return false;
            });
        }

        public void setSize(Size size) {
            this.mSize = size;
        }
    }

    public interface SearchAdapterListener {
        void searchAdapterSizeClick(Size size, MaterialCheckBox checkBox, int position);

        void searchAdapterSizeLongClick(int position);

        void searchAdapterFolderClick(Folder folder, MaterialCheckBox checkBox, int position);

        void searchAdapterFolderLongClick(int position);
    }
}
