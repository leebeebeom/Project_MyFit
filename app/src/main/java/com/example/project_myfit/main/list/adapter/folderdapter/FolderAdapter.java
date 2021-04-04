package com.example.project_myfit.main.list.adapter.folderdapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
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
    private List<Folder> mFolderList, mSelectedItem;
    private int mActionModeState, mSort;
    private final HashSet<Long> mSelectedItemId;
    private List<Long> mFolderFolderIdList, mSizeFolderIdList;
    private boolean mIsDragging;

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
        this.mSelectedItemId = new HashSet<>();
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
        if (mSelectedItem != null) {
            mSelectedItemId.clear();
            for (Folder selectedItem : mSelectedItem)
                mSelectedItemId.add(selectedItem.getId());
            mSelectedItem = null;
        }

        Folder folder = getItem(holder.getLayoutPosition());
        holder.mBinding.setFolder(folder);
        holder.setFolder(folder);

        holder.mBinding.folderAmount.setText(String.valueOf(new AdapterUtils(holder.itemView.getContext())
                .getFolderContentsSize(folder, mFolderFolderIdList, mSizeFolderIdList)));

        MaterialCheckBox checkBox = holder.mBinding.folderCheckBox;
        AppCompatImageView dragHandle = holder.mBinding.folderDragHandle;

        dragHandle.setOnTouchListener((v, event) -> {
            if (folder.getId() != -1 && event.getAction() == MotionEvent.ACTION_DOWN && !mIsDragging) {
                mIsDragging = true;
                mListener.onFolderDragHandleTouch(holder);
            }
            return false;
        });

        //check box visibility----------------------------------------------------------------------
        if (mActionModeState == ACTION_MODE_ON) {
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(mSelectedItemId.contains(folder.getId()));
        } else {
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
            if (!mSelectedItemId.isEmpty()) mSelectedItemId.clear();
        }
        //------------------------------------------------------------------------------------------
        dragHandle.setVisibility(mSort == SORT_CUSTOM && mActionModeState == ACTION_MODE_ON ? View.VISIBLE : View.GONE);
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
        mModel.getSelectedItemFolderList().clear();
        for (Folder folder : getCurrentList())
            if (mSelectedItemId.contains(folder.getId()) && folder.getId() != -1)
                mModel.getSelectedItemFolderList().add(folder);
        mIsDragging = false;

    }
    //----------------------------------------------------------------------------------------------

    //action mode & drag select---------------------------------------------------------------------
    public void setActionModeState(int actionModeState) {
        //checked
        this.mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void setSelectedItem(List<Folder> selectedItem) {
        //checked
        this.mSelectedItem = selectedItem;
    }

    //drag select-----------------------------------------------------------------------------------
    public void setSelectedPosition(long id) {
        //checked
        if (!mSelectedItemId.contains(id)) mSelectedItemId.add(id);
        else mSelectedItemId.remove(id);
    }

    public void selectAll() {
        //checked
        for (Folder f : getCurrentList())
            mSelectedItemId.add(f.getId());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        //checked
        mSelectedItemId.clear();
        notifyDataSetChanged();
    }

    //----------------------------------------------------------------------------------------------

    public static class FolderVH extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        //all checked
        private final ItemListRecyclerFolderBinding mBinding;
        private final FolderAdapterListener mListener;
        private Folder mFolder;

        public FolderVH(@NotNull ItemListRecyclerFolderBinding binding, FolderAdapterListener listener) {
            //checked
            super(binding.getRoot());
            this.mBinding = binding;
            this.mListener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setFolder(Folder folder) {
            this.mFolder = folder;
        }

        @Override
        public void onClick(View v) {
            if (mFolder.getId() != -1)
                mListener.onFolderItemViewClick(mFolder, mBinding.folderCheckBox);
        }

        @Override
        public boolean onLongClick(View v) {
            if (mFolder.getId() != -1)
                mListener.onFolderItemViewLongClick(getLayoutPosition());
            return false;
        }

        public ItemListRecyclerFolderBinding getBinding() {
            return mBinding;
        }
    }

    public interface FolderAdapterListener {
        //checked
        void onFolderItemViewClick(Folder folder, MaterialCheckBox checkBox);

        void onFolderItemViewLongClick(int position);

        void onFolderDragHandleTouch(RecyclerView.ViewHolder holder);
    }
}
