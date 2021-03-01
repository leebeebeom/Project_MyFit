package com.example.project_myfit.ui.main.listfragment.adapter.folderdapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemListRecyclerFolderBinding;
import com.example.project_myfit.ui.main.listfragment.ListViewModel;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.google.android.material.card.MaterialCardView;
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
    private List<Folder> mFolderList;
    private int mActionModeState, mSort;
    private HashSet<Integer> mSelectedPosition;
    private List<Long> mFolderFolderIdList, mSizeFolderIdList;

    public FolderAdapter(ListViewModel model) {
        super(new DiffUtil.ItemCallback<Folder>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull Folder oldItem, @NonNull @NotNull Folder newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull Folder oldItem, @NonNull @NotNull Folder newItem) {
                return oldItem.getFolderName().equals(newItem.getFolderName());
            }
        });
        this.mModel = model;
        this.mSelectedPosition = new HashSet<>();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void setOnFolderAdapterListener(FolderAdapterListener listener) {
        this.mListener = listener;
    }

    public void submitList(@Nullable @org.jetbrains.annotations.Nullable List<Folder> list, List<Long> folderFolderIdList, List<Long> sizeFolderIdList) {
        super.submitList(list);
        this.mFolderList = list;
        this.mFolderFolderIdList = folderFolderIdList;
        this.mSizeFolderIdList = sizeFolderIdList;
    }

    @NonNull
    @NotNull
    @Override
    public FolderVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemListRecyclerFolderBinding binding = ItemListRecyclerFolderBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new FolderVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FolderVH holder, int position) {
        Folder folder = getItem(holder.getLayoutPosition());
            holder.mBinding.setFolder(folder);
            holder.setFolder(folder);

        int amount = 0;
        for (Long l : mFolderFolderIdList)
            if (l == folder.getId()) amount++;
        for (Long l : mSizeFolderIdList)
            if (l == folder.getId()) amount++;
        holder.mBinding.folderAmount.setText(String.valueOf(amount));

        MaterialCheckBox checkBox = holder.mBinding.folderCheckBox;
        AppCompatImageView dragHandle = holder.mBinding.folderDragHandle;

        //check box visibility----------------------------------------------------------------------
        if (mActionModeState == ACTION_MODE_ON) {
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
        } else {
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
            if (!mSelectedPosition.isEmpty()) mSelectedPosition.clear();
        }
        //------------------------------------------------------------------------------------------
        dragHandle.setVisibility(mSort == SORT_CUSTOM ? View.VISIBLE : View.GONE);
        holder.itemView.setVisibility(folder.getId() == -1 ? View.INVISIBLE : View.VISIBLE);
        //------------------------------------------------------------------------------------------
    }

    //drag------------------------------------------------------------------------------------------
    public void onItemMove(int from, int to) {
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(mFolderList, i, i + 1);

                int toOrder = mFolderList.get(i).getOrderNumber();
                int fromOrder = mFolderList.get(i + 1).getOrderNumber();
                mFolderList.get(i).setOrderNumber(fromOrder);
                mFolderList.get(i + 1).setOrderNumber(toOrder);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(mFolderList, i, i - 1);

                int toOrder = mFolderList.get(i).getOrderNumber();
                int fromOrder = mFolderList.get(i - 1).getOrderNumber();
                mFolderList.get(i).setOrderNumber(fromOrder);
                mFolderList.get(i - 1).setOrderNumber(toOrder);
            }
        }
        notifyItemMoved(from, to);
    }

    public void onItemDrop(@NotNull RecyclerView.ViewHolder viewHolder) {
        ((FolderAdapter.FolderVH) viewHolder).mBinding.folderAmountLayout.setVisibility(View.VISIBLE);
        viewHolder.itemView.setTranslationZ(0);
        mListener.onFolderDragHandleTouch(viewHolder, ((FolderAdapter.FolderVH) viewHolder).mBinding.folderAmountLayout);
        mModel.getRepository().folderUpdate(mFolderList);
    }
    //----------------------------------------------------------------------------------------------

    //action mode & drag select---------------------------------------------------------------------
    public void setActionModeState(int actionModeState) {
        this.mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public HashSet<Integer> getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(HashSet<Integer> selectedPosition) {
        this.mSelectedPosition = selectedPosition;
    }

    //drag select-----------------------------------------------------------------------------------
    public void setSelectedPosition(int position) {
        if (!mSelectedPosition.contains(position)) mSelectedPosition.add(position);
        else mSelectedPosition.remove(position);
    }

    public void selectAll() {
        for (int i = 0; i < getCurrentList().size(); i++) mSelectedPosition.add(i);
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedPosition.clear();
        notifyDataSetChanged();
    }

    //----------------------------------------------------------------------------------------------
    public void setSort(int sort) {
        if (mSort != sort) {
            this.mSort = sort;
            notifyDataSetChanged();
        }
    }

    public static class FolderVH extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener, View.OnTouchListener {
        private final ItemListRecyclerFolderBinding mBinding;
        private final FolderAdapterListener mListener;
        private Folder mFolder;

        public FolderVH(@NotNull ItemListRecyclerFolderBinding binding, FolderAdapterListener listener) {
            super(binding.getRoot());
            this.mBinding = binding;
            this.mListener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mBinding.folderDragHandle.setOnTouchListener(this);
        }

        public void setFolder(Folder folder) {
            this.mFolder = folder;
        }

        @Override
        public void onClick(View v) {
            if (!mFolder.getFolderName().equals("dummy"))
                mListener.onFolderItemViewClick(mFolder, mBinding.folderCheckBox, getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            if (!mFolder.getFolderName().equals("dummy"))
                mListener.onFolderItemViewLongClick(mBinding.folderCardView, getLayoutPosition());
            return false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!mFolder.getFolderName().equals("dummy") && event.getAction() == MotionEvent.ACTION_DOWN) {
                itemView.setTranslationZ(10);
                mListener.onFolderDragHandleTouch(this, mBinding.folderAmountLayout);
            }
            return false;
        }
    }

    public interface FolderAdapterListener {
        void onFolderItemViewClick(Folder folder, MaterialCheckBox checkBox, int position);

        void onFolderItemViewLongClick(MaterialCardView cardView, int position);

        void onFolderDragHandleTouch(RecyclerView.ViewHolder holder, LinearLayoutCompat folderAmountLayout);
    }
}
