package com.example.project_myfit.main.list.adapter.folderdapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.databinding.ItemListRecyclerFolderBinding;
import com.example.project_myfit.main.list.ListViewModel;
import com.example.project_myfit.util.AdapterUtils;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.MyFitConstant.SORT_CUSTOM;

@SuppressLint("ClickableViewAccessibility")
public class FolderAdapter extends ListAdapter<Folder, FolderAdapter.FolderVH> {
    private final ListViewModel mModel;
    private FolderAdapterListener mListener;
    private List<Folder> mFolderList, mSelectedFolderList;
    private int mActionModeState, mSort;
    private final HashSet<Long> mSelectedFolderIdHashSet;
    private List<Long> mFolderFolderIdList, mSizeFolderIdList;

    public FolderAdapter(ListViewModel model) {
        //checked
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
        this.mModel = model;
        this.mSelectedFolderIdHashSet = new HashSet<>();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void setOnFolderAdapterListener(FolderAdapterListener listener) {
        //checked
        this.mListener = listener;
    }

    public void submitList(@Nullable @org.jetbrains.annotations.Nullable List<Folder> list, List<Long> folderFolderIdList, List<Long> sizeFolderIdList, int sort) {
        //checked
        super.submitList(list);
        this.mFolderList = list;
        this.mFolderFolderIdList = folderFolderIdList;
        this.mSizeFolderIdList = sizeFolderIdList;
        this.mSort = sort;
    }

    @NonNull
    @NotNull
    @Override
    public FolderVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        //checked
        ItemListRecyclerFolderBinding binding = ItemListRecyclerFolderBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new FolderVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FolderVH holder, int position) {
        //checked
        if (mSelectedFolderList != null) {
            mSelectedFolderIdHashSet.clear();
            for (Folder selectedFolder : mSelectedFolderList)
                mSelectedFolderIdHashSet.add(selectedFolder.getId());
            mSelectedFolderList = null;
        }

        Folder folder = getItem(holder.getLayoutPosition());
        holder.mBinding.setFolder(folder);
        holder.setFolder(folder);
        holder.setContentsSize(mFolderFolderIdList, mSizeFolderIdList);
        holder.setActionMode(mActionModeState, mSelectedFolderIdHashSet, mSort);

        holder.itemView.setVisibility(folder.getId() == -1 ? View.INVISIBLE : View.VISIBLE);
    }

    public void onItemMove(int from, int to) {
        //checked
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(mFolderList, i, i + 1);

                if (mFolderList.get(i + 1).getId() != -1 && mFolderList.get(i).getId() != -1) {
                    int toOrder = mFolderList.get(i).getOrderNumber();
                    int fromOrder = mFolderList.get(i + 1).getOrderNumber();
                    mFolderList.get(i).setOrderNumber(fromOrder);
                    mFolderList.get(i + 1).setOrderNumber(toOrder);
                }
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(mFolderList, i, i - 1);

                if (mFolderList.get(i - 1).getId() != -1 && mFolderList.get(i).getId() != -1) {
                    int toOrder = mFolderList.get(i).getOrderNumber();
                    int fromOrder = mFolderList.get(i - 1).getOrderNumber();
                    mFolderList.get(i).setOrderNumber(fromOrder);
                    mFolderList.get(i - 1).setOrderNumber(toOrder);
                }
            }
        }
        notifyItemMoved(from, to);
    }

    public void onItemDrop(@NotNull RecyclerView.ViewHolder viewHolder) {
        //checked
        mModel.getRepository().folderUpdate(mFolderList);
        mListener.onFolderDragHandleTouch(viewHolder);
        mModel.getSelectedFolderList().clear();
        for (Folder folder : getCurrentList())
            if (mSelectedFolderIdHashSet.contains(folder.getId()) && folder.getId() != -1)
                mModel.getSelectedFolderList().add(folder);
        ((FolderVH) viewHolder).mIsDragging = false;
    }
    //----------------------------------------------------------------------------------------------

    //action mode & drag select---------------------------------------------------------------------
    public void setActionModeState(int actionModeState) {
        //checked
        this.mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void setSelectedFolderList(List<Folder> selectedFolderList) {
        //checked
        this.mSelectedFolderList = selectedFolderList;
    }

    //drag select-----------------------------------------------------------------------------------
    public void setSelectedFolderIdHashSet(long id) {
        //checked
        if (!mSelectedFolderIdHashSet.contains(id)) mSelectedFolderIdHashSet.add(id);
        else mSelectedFolderIdHashSet.remove(id);
    }

    public void selectAll() {
        //checked
        for (Folder f : getCurrentList())
            mSelectedFolderIdHashSet.add(f.getId());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        //checked
        mSelectedFolderIdHashSet.clear();
        notifyDataSetChanged();
    }

    //----------------------------------------------------------------------------------------------

    public static class FolderVH extends RecyclerView.ViewHolder {
        //all checked
        private final ItemListRecyclerFolderBinding mBinding;
        private Folder mFolder;
        private boolean mIsDragging;
        private final AdapterUtils mAdapterUtils;

        public FolderVH(@NotNull ItemListRecyclerFolderBinding binding, FolderAdapterListener listener) {
            //checked
            super(binding.getRoot());
            this.mBinding = binding;
            this.mAdapterUtils = new AdapterUtils(itemView.getContext());


            itemView.setOnClickListener(v -> {
                if (mFolder.getId() != -1)
                    listener.onFolderItemViewClick(mFolder, mBinding.folderCheckBox);
            });

            itemView.setOnLongClickListener(v -> {
                if (mFolder.getId() != -1)
                    listener.onFolderItemViewLongClick(getLayoutPosition());
                return false;
            });

            mBinding.folderDragHandle.setOnTouchListener((v, event) -> {
                if (mFolder.getId() != -1 && event.getAction() == MotionEvent.ACTION_DOWN && !mIsDragging) {
                    mIsDragging = true;
                    listener.onFolderDragHandleTouch(this);
                }
                return false;
            });
        }

        public void setContentsSize(List<Long> folderFolderIdList, List<Long> sizeFolderIdList) {
            mBinding.folderContensSize.setText(String.valueOf(mAdapterUtils
                    .getFolderContentsSize(mFolder, folderFolderIdList, sizeFolderIdList)));
        }

        public void setFolder(Folder folder) {
            this.mFolder = folder;
        }

        public ItemListRecyclerFolderBinding getBinding() {
            return mBinding;
        }

        public void setActionMode(int actionModeState, HashSet<Long> selectedFolderIdHashSet, int sort) {
            mBinding.folderDragHandle.setVisibility(sort == SORT_CUSTOM && actionModeState == ACTION_MODE_ON ? View.VISIBLE : View.GONE);

            if (actionModeState == ACTION_MODE_ON)
                mAdapterUtils.gridActionModeOn(mBinding.folderCheckBox, selectedFolderIdHashSet, mFolder.getId());
            else
                mAdapterUtils.gridActionModeOff(mBinding.folderCheckBox, selectedFolderIdHashSet);
        }
    }

    public interface FolderAdapterListener {
        //checked
        void onFolderItemViewClick(Folder folder, MaterialCheckBox checkBox);

        void onFolderItemViewLongClick(int position);

        void onFolderDragHandleTouch(RecyclerView.ViewHolder holder);
    }
}
