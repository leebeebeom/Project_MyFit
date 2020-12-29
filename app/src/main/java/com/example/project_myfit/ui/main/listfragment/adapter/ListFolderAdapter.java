package com.example.project_myfit.ui.main.listfragment.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.project_myfit.databinding.ItemListFolderBinding;
import com.example.project_myfit.ui.main.adapter.MainDragCallBack;
import com.example.project_myfit.ui.main.listfragment.ListViewModel;
import com.example.project_myfit.ui.main.listfragment.database.ListFolder;

import java.util.Collections;
import java.util.List;

public class ListFolderAdapter extends RecyclerView.Adapter<ListFolderAdapter.ListFolderVH> implements MainDragCallBack.DragFolderListener {
    private List<ListFolder> mFolderList;
    private MainDragCallBack.StartDragListener mListener;
    private Context mContext;
    private ListViewModel mModel;
    private static int mDefaultColor;
    private final ViewBinderHelper mBinderHelper = new ViewBinderHelper();
    private OnFolderClickListener mFolderListener;

    public interface OnFolderClickListener {
        void onItemClicked();

        void onEditClicked();

        void onDeleteClicked();
    }

    public void setOnFolderClickListener(OnFolderClickListener listener) {
        mFolderListener = listener;
    }

    public void setItem(List<ListFolder> listFolders, MainDragCallBack.StartDragListener listener, Context context, ListViewModel model) {
        mFolderList = listFolders;
        mListener = listener;
        mContext = context;
        mModel = model;
    }

    @NonNull
    @Override
    public ListFolderVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListFolderBinding binding = ItemListFolderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ListFolderVH(binding);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ListFolderVH holder, int position) {
        ListFolder listFolder = mFolderList.get(position);
        holder.binding.setListFolder(listFolder);
        mBinderHelper.bind(holder.binding.listSwipeLayout, String.valueOf(listFolder.getId()));
        mBinderHelper.setOpenOnlyOne(true);
        holder.binding.folderDragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mListener.startDrag(holder);
            }
            return false;
        });
        holder.binding.cardView.setOnClickListener(v -> mFolderListener.onItemClicked());
        holder.binding.editFolder.setOnClickListener(v -> mFolderListener.onEditClicked());
        holder.binding.deleteFolder.setOnClickListener(v -> mFolderListener.onDeleteClicked());
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
        ItemListFolderBinding binding;

        public ListFolderVH(ItemListFolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            mDefaultColor = binding.cardView.getCardBackgroundColor().getDefaultColor();
        }
    }
}
