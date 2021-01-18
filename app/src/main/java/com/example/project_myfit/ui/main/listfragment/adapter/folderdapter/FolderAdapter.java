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

public class FolderAdapter extends ListAdapter<Folder, FolderAdapter.FolderVH> {
    private final ListViewModel mModel;
    private FolderAdapterListener mListener;
    private List<Folder> mFolderList;
    private int mActionModeState;
    private final HashSet<Integer> mSelectedPosition;

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
        return new FolderVH(binding);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull @NotNull FolderVH holder, int position) {
        Folder folder = getItem(holder.getLayoutPosition());
        MaterialCardView cardView = holder.mBinding.folderCardView;
        MaterialCheckBox checkBox = holder.mBinding.folderCheckBox;
        AppCompatImageView dragHandle = holder.mBinding.folderDragHandle;

        holder.mBinding.setFolder(folder);

        //click-------------------------------------------------------------------------------------
        cardView.setOnClickListener(v -> mListener.onFolderCardViewClick(folder, checkBox, holder.getLayoutPosition()));
        cardView.setOnLongClickListener(v -> {
            mListener.onFolderCardViewLongClick(folder, holder.mBinding.folderCardView, checkBox, holder.getLayoutPosition());
            return true;
        });
        dragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mListener.onFolderDragHandTouch(holder);
            }
            return false;
        });
        //------------------------------------------------------------------------------------------

        //check box visibility----------------------------------------------------------------------
        if (mActionModeState == ACTION_MODE_ON)
            checkBox.setVisibility(View.VISIBLE);
        else {
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
            mSelectedPosition.clear();
        }
        //------------------------------------------------------------------------------------------
        checkBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
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

    public void onItemDrop() {
        mModel.updateFolder(mFolderList);
    }
    //----------------------------------------------------------------------------------------------

    //action mode & drag select---------------------------------------------------------------------
    public void setActionModeState(int actionModeState) {
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public int getActionModeState() {
        return mActionModeState;
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

    public static class FolderVH extends RecyclerView.ViewHolder {
        ItemListRecyclerFolderBinding mBinding;

        public FolderVH(ItemListRecyclerFolderBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
