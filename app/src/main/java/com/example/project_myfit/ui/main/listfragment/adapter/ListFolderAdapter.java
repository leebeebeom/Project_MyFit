package com.example.project_myfit.ui.main.listfragment.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.project_myfit.databinding.ItemListRecyclerFolderBinding;
import com.example.project_myfit.ui.main.listfragment.ListViewModel;
import com.example.project_myfit.ui.main.listfragment.database.ListFolder;

import java.util.Collections;
import java.util.List;

public class ListFolderAdapter extends RecyclerView.Adapter<ListFolderAdapter.ListFolderVH> implements ListDragCallBack.DragFolderListener {
    private List<ListFolder> mFolderList;
    private ListViewModel mModel;
    private final ViewBinderHelper mBinderHelper = new ViewBinderHelper();
    private OnFolderClickListener mListener;

    public interface OnFolderClickListener {
        void onItemClicked();

        void onEditClicked(ListFolder listFolder, int position);

        void onDeleteClicked(ListFolder listFolder);
    }

    public void setOnFolderClickListener(OnFolderClickListener listener) {
        mListener = listener;
    }

    public void setItem(List<ListFolder> listFolders, ListViewModel model) {
        mFolderList = listFolders;
        mModel = model;
    }

    @NonNull
    @Override
    public ListFolderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListRecyclerFolderBinding binding = ItemListRecyclerFolderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ListFolderVH(binding);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ListFolderVH holder, int position) {
        ListFolder listFolder = mFolderList.get(position);
        holder.binding.setListFolder(listFolder);
        mBinderHelper.bind(holder.binding.listSwipeLayout, String.valueOf(listFolder.getId()));
        mBinderHelper.setOpenOnlyOne(true);
        holder.binding.cardView.setOnClickListener(v -> mListener.onItemClicked());
        holder.binding.editFolder.setOnClickListener(v -> {
            mListener.onEditClicked(mFolderList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            holder.binding.listSwipeLayout.close(true);
        });
        holder.binding.deleteFolder.setOnClickListener(v -> {
            mListener.onDeleteClicked(mFolderList.get(holder.getAdapterPosition()));
            holder.binding.listSwipeLayout.close(true);
        });
    }

    @Override
    public int getItemCount() {
        return mFolderList.size();
    }

    @Override
    public void onItemMove(int from, int to) {
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(mFolderList, i, i + 1);

                int toOrder = mFolderList.get(i).getOrderNumberFolder();
                int fromOrder = mFolderList.get(i + 1).getOrderNumberFolder();
                mFolderList.get(i).setOrderNumberFolder(fromOrder);
                mFolderList.get(i + 1).setOrderNumberFolder(toOrder);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(mFolderList, i, i - 1);

                int toOrder = mFolderList.get(i).getOrderNumberFolder();
                int fromOrder = mFolderList.get(i - 1).getOrderNumberFolder();
                mFolderList.get(i).setOrderNumberFolder(fromOrder);
                mFolderList.get(i - 1).setOrderNumberFolder(toOrder);
            }
        }
        notifyItemMoved(from, to);
    }

    public void updateDiffUtils(List<ListFolder> newFolderList) {
        FolderDiffUtil diffUtil = new FolderDiffUtil(mFolderList, newFolderList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil, true);
        mFolderList.clear();
        mFolderList.addAll(newFolderList);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void onItemDrop() {
        mModel.updateFolderOrder(mFolderList);
    }

    public static class ListFolderVH extends RecyclerView.ViewHolder {
        ItemListRecyclerFolderBinding binding;

        public ListFolderVH(ItemListRecyclerFolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
