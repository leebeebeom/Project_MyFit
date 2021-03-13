package com.example.project_myfit.searchActivity.adapter;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemSearchFolderBinding;
import com.example.project_myfit.databinding.ItemSearchSizeBinding;
import com.example.project_myfit.searchActivity.SearchViewModel;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;

public class SearchAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> implements Filterable {
    private List<Object> mOriginList;
    private final SearchAdapterListener mListener;
    private int mActionModeState;
    private final HashSet<Integer> mSelectedPosition;
    private List<Long> mFolderFolderIdList, mSizeFolderIdList;
    private final SearchViewModel mModel;
    private SearchViewPagerAdapter.SearchViewPagerVH mSearchViewPagerVH;
    private Animation mAnimation;

    public SearchAdapter(SearchAdapterListener listener, SearchViewModel model) {
        super(new SearchDiffUtil());
        this.mListener = listener;
        this.mModel = model;
        mSelectedPosition = new HashSet<>();
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

    public void setItem(List<Object> list, List<Long> folderFolderIdList, List<Long> sizeFolderIdList) {
        this.mOriginList = list;
        submitList(list);
        this.mFolderFolderIdList = folderFolderIdList;
        this.mSizeFolderIdList = sizeFolderIdList;
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

            int amount = 0;
            for (Long l : mFolderFolderIdList)
                if (l == folder.getId()) amount++;
            for (Long l : mSizeFolderIdList)
                if (l == folder.getId()) amount++;
            ((SearchRecyclerFolderVH) holder).mFolderBinding.searchItemAmount.setText(String.valueOf(amount));

            MaterialCardView cardView = ((SearchRecyclerFolderVH) holder).mFolderBinding.searchFolderCardView;
            MaterialCheckBox checkBox = ((SearchRecyclerFolderVH) holder).mFolderBinding.searchFolderCheckBox;
            if (mActionModeState == ACTION_MODE_ON) {
                if (mAnimation == null)
                    mAnimation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycler_list_slide_right);
                if (!mAnimation.hasStarted()) cardView.setAnimation(mAnimation);
                checkBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
            } else if (mActionModeState == ACTION_MODE_OFF) {
                mAnimation = null;
                cardView.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycler_list_slide_left));
                checkBox.setChecked(false);
                setActionModeStateNone();
                if (mSelectedPosition.size() != 0) mSelectedPosition.clear();
            }
        } else {
            Size size = (Size) getCurrentList().get(position);

            ((SearchRecyclerSizeVH) holder).mSizeBinding.setSize(size);
            ((SearchRecyclerSizeVH) holder).setSize(size);

            MaterialCardView cardView = ((SearchRecyclerSizeVH) holder).mSizeBinding.searchSizeCardView;
            MaterialCheckBox checkBox = ((SearchRecyclerSizeVH) holder).mSizeBinding.searchSizeCheckBox;
            if (mActionModeState == ACTION_MODE_ON) {
                if (mAnimation == null)
                    mAnimation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycler_list_slide_right);
                if (!mAnimation.hasStarted()) cardView.setAnimation(mAnimation);
                checkBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
            } else if (mActionModeState == ACTION_MODE_OFF) {
                mAnimation = null;
                cardView.setAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycler_list_slide_left));
                checkBox.setChecked(false);
                setActionModeStateNone();
                if (mSelectedPosition.size() != 0) mSelectedPosition.clear();
            }
        }
    }

    private void setActionModeStateNone() {
        new Handler().postDelayed(() -> mActionModeState = 0, 310);
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
            List<Folder> folderList = new ArrayList<>();
            List<Size> sizeList = new ArrayList<>();
            List<Object> filteredList = new ArrayList<>();
            if (!TextUtils.isEmpty(filterString)) {
                for (Object o : mOriginList)
                    if (o instanceof Folder) {
                        Folder folder = (Folder) o;
                        if (folder.getFolderName().toLowerCase().trim().contains(filterString))
                            folderList.add((Folder) o);
                    } else {
                        Size size = (Size) o;
                        if (size.getBrand().toLowerCase().trim().contains(filterString) ||
                                size.getName().toLowerCase().trim().contains(filterString))
                            sizeList.add((Size) o);
                    }
            }

            folderListing(filteredList, folderList);
            sizeListing(filteredList, sizeList);
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

    private void sizeListing(List<Object> objectList, @NotNull List<Size> allSizeList) {
        for (Size s : allSizeList) {
            Category parentCategory = mModel.getRepository().getCategory(s.getFolderId());
            if (parentCategory != null && parentCategory.getIsDeleted() == 0) objectList.add(s);
            else if (parentCategory == null) {
                Folder parentFolder = mModel.getRepository().getFolder(s.getFolderId());
                if (parentFolder.getIsDeleted() == 0)
                    checkParentIsDeleted(parentFolder, s, objectList);
            }
        }
    }

    private void folderListing(List<Object> objectList, @NotNull List<Folder> allFolderList) {
        for (Folder f : allFolderList) {
            Category parentCategory = mModel.getRepository().getCategory(f.getFolderId());
            if (parentCategory != null && parentCategory.getIsDeleted() == 0) objectList.add(f);
            else if (parentCategory == null) {
                Folder parentFolder = mModel.getRepository().getFolder(f.getFolderId());
                if (parentFolder.getIsDeleted() == 0)
                    checkParentIsDeleted(parentFolder, f, objectList);
            }
        }
    }

    private void checkParentIsDeleted(@NotNull Folder parentFolder, Folder folder, List<Object> objectList) {
        Folder parentFolder2 = mModel.getRepository().getFolder(parentFolder.getFolderId());
        if (parentFolder2 == null) {
            Category category = mModel.getRepository().getCategory(parentFolder.getFolderId());
            if (category.getIsDeleted() == 0) objectList.add(folder);
        } else {
            if (parentFolder2.getIsDeleted() == 0) {
                checkParentIsDeleted(parentFolder2, folder, objectList);
            }
        }
    }

    private void checkParentIsDeleted(@NotNull Folder parentFolder, Size size, List<Object> objectList) {
        Folder parentFolder2 = mModel.getRepository().getFolder(parentFolder.getFolderId());
        if (parentFolder2 == null) {
            Category category = mModel.getRepository().getCategory(parentFolder.getFolderId());
            if (category.getIsDeleted() == 0) objectList.add(size);
        } else {
            if (parentFolder2.getIsDeleted() == 0) {
                checkParentIsDeleted(parentFolder2, size, objectList);
            }
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
