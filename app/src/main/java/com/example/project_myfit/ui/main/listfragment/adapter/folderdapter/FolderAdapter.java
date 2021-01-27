package com.example.project_myfit.ui.main.listfragment.adapter.folderdapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
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

    public FolderAdapter(ListViewModel model) {
        super(new FolderDiffUtil());
        mModel = model;
        setHasStableIds(true);
        mSelectedPosition = new HashSet<>();
    }

    @Override
    public long getItemId(int position) {
        return getCurrentList().get(position).getId();
    }

    public void setOnFolderAdapterListener(FolderAdapterListener listener) {
        mListener = listener;
    }

    public void setItem(List<Folder> folderList) {
        mFolderList = folderList;
        submitList(folderList);
    }

    @NonNull
    @NotNull
    @Override
    public FolderVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemListRecyclerFolderBinding binding = ItemListRecyclerFolderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FolderVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FolderVH holder, int position) {
        Folder folder = getItem(holder.getLayoutPosition());
        holder.mBinding.setFolder(folder);
        holder.setFolder(folder);

        MaterialCheckBox checkBox = holder.mBinding.folderCheckBox;
        AppCompatImageView dragHandle = holder.mBinding.folderDragHandle;

        //check box visibility----------------------------------------------------------------------
        if (mActionModeState == ACTION_MODE_ON) {
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
        } else {
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
            if (mSelectedPosition.size() != 0)
                mSelectedPosition.clear();
        }
        //------------------------------------------------------------------------------------------

        if (mSort == SORT_CUSTOM)
            dragHandle.setVisibility(View.VISIBLE);
        else dragHandle.setVisibility(View.GONE);
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

    public void onItemDrop(RecyclerView.ViewHolder viewHolder) {
        ((FolderAdapter.FolderVH) viewHolder).mBinding.folderAmountLayout.setVisibility(View.VISIBLE);
        viewHolder.itemView.setTranslationZ(0);
        mModel.updateFolder(mFolderList);
        notifyDataSetChanged();
    }
    //----------------------------------------------------------------------------------------------

    //action mode & drag select---------------------------------------------------------------------
    public void setActionModeState(int actionModeState) {
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public HashSet<Integer> getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(HashSet<Integer> mSelectedPosition) {
        this.mSelectedPosition = mSelectedPosition;
    }

    //drag select-----------------------------------------------------------------------------------
    public void setSelectedPosition(int position) {
        if (!mSelectedPosition.contains(position)) mSelectedPosition.add(position);
        else mSelectedPosition.remove(position);
    }

    public void selectAll() {
        for (int i = 0; i < getCurrentList().size(); i++) {
            mSelectedPosition.add(i);
        }
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedPosition.clear();
        notifyDataSetChanged();
    }

    //----------------------------------------------------------------------------------------------
    public void setSort(int mSort) {
        this.mSort = mSort;
        notifyDataSetChanged();
    }

    public static class FolderVH extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener, View.OnTouchListener {
        private final ItemListRecyclerFolderBinding mBinding;
        private final FolderAdapterListener mListener;
        private Folder mFolder;

        public FolderVH(ItemListRecyclerFolderBinding binding, FolderAdapterListener listener) {
            super(binding.getRoot());
            mBinding = binding;
            mListener = listener;

            MaterialCardView cardView = mBinding.folderCardView;
            AppCompatImageView dragHandle = mBinding.folderDragHandle;

            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
            dragHandle.setOnTouchListener(this);
        }

        public void setFolder(Folder folder) {
            mFolder = folder;
        }

        @Override
        public void onClick(View v) {
            mListener.onFolderCardViewClick(mFolder, mBinding.folderCheckBox, getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onFolderCardViewLongClick(mBinding.folderCardView, getLayoutPosition());
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                itemView.setTranslationZ(10);
                mBinding.folderAmountLayout.setVisibility(View.INVISIBLE);
                mListener.onFolderDragHandTouch(this);
            }
            return false;
        }
    }
}
