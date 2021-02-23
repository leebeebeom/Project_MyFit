package com.example.project_myfit.searchActivity.adapter;

import android.view.LayoutInflater;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> implements Filterable {
    private List<Object> mOriginList;
    private final SearchAdapterListener mListener;

    public SearchAdapter(SearchAdapterListener listener) {
        super(new SearchDiffUtil());
        this.mListener = listener;
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

            ((SearchRecyclerFolderVH) holder).mFolderBinding.setFolder(folder);
            ((SearchRecyclerFolderVH) holder).setFolder(folder);
        } else {
            Size size = (Size) getCurrentList().get(position);

            ((SearchRecyclerSizeVH) holder).mSizeBinding.setSize(size);
            ((SearchRecyclerSizeVH) holder).setSize(size);
        }
    }

    @Override
    public int getItemCount() {
        return getCurrentList().size();
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

            itemView.setOnClickListener(v -> listener.searchAdapterFolderClick(mFolder));
            itemView.setOnLongClickListener(v -> {
                listener.searchAdapterFolderLongClick();
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

            itemView.setOnClickListener(v -> listener.searchAdapterSizeClick(mSize));
            itemView.setOnLongClickListener(v -> {
                listener.searchAdapterSizeLongClick();
                return false;
            });
        }

        public void setSize(Size size) {
            this.mSize = size;
        }
    }

    public interface SearchAdapterListener {
        void searchAdapterSizeClick(Size size);

        void searchAdapterSizeLongClick();

        void searchAdapterFolderClick(Folder folder);

        void searchAdapterFolderLongClick();
    }
}
