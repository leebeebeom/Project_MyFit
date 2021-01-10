package com.example.project_myfit.ui.main.listfragment.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemListRecyclerFolderBinding;
import com.example.project_myfit.ui.main.listfragment.ListViewModel;
import com.example.project_myfit.ui.main.listfragment.database.Folder;

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
    private final HashSet<Integer> mSelectedPosition = new HashSet<>();

    public FolderAdapter(ListViewModel model) {
        super(new FolderDiffUtil());
        mModel = model;
        setHasStableIds(true);
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
        //bind item
        holder.mBinding.setFolder(getCurrentList().get(holder.getLayoutPosition()));

        //click listener
        holder.mBinding.folderCardView.setOnClickListener(v -> mListener.onCardViewClick(getCurrentList().get(holder.getLayoutPosition()), holder.mBinding.folderCheckBox, holder.getLayoutPosition()));
        holder.mBinding.folderCardView.setOnLongClickListener(v -> {
            mListener.onCardViewLongClick(getCurrentList().get(holder.getLayoutPosition()), holder, holder.mBinding.folderCheckBox, holder.getLayoutPosition());
            return true;
        });
        holder.mBinding.folderDragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mListener.onDragHandTouch(holder);
            }
            return false;
        });

        //check box visibility
        if (mActionModeState == ACTION_MODE_ON)
            holder.mBinding.folderCheckBox.setVisibility(View.VISIBLE);
        else {
            holder.mBinding.folderCheckBox.setVisibility(View.GONE);
            holder.mBinding.folderCheckBox.setChecked(false);
            mSelectedPosition.clear();
        }
        holder.mBinding.folderCheckBox.setChecked(mSelectedPosition.contains(holder.getLayoutPosition()));
    }

    @Override
    public long getItemId(int position) {
        return getCurrentList().get(position).getId();
    }

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
        mModel.updateFolderOrder(mFolderList);
    }

    public void setActionModeState(int actionModeState) {
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public int getActionModeState() {
        return mActionModeState;
    }

    //set selectedPosition
    public void setSelectedPosition(int position) {
        if (!mSelectedPosition.contains(position)) mSelectedPosition.add(position);
        else mSelectedPosition.remove(position);
    }

    //select all
    public void selectAll() {
        for (int i = 0; i < getCurrentList().size(); i++) {
            mSelectedPosition.add(i);
        }
        notifyDataSetChanged();
    }

    //deselect all
    public void deselectAll() {
        mSelectedPosition.clear();
        notifyDataSetChanged();
    }

    public static class FolderVH extends RecyclerView.ViewHolder {
        ItemListRecyclerFolderBinding mBinding;

        public FolderVH(ItemListRecyclerFolderBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
