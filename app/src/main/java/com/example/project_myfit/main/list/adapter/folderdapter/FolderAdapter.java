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
import com.example.project_myfit.util.AdapterUtil;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

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
    private AdapterUtil mAdapterUtil;

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
        holder.setFolder(folder);

        if (mAdapterUtil == null) mAdapterUtil = new AdapterUtil(holder.itemView.getContext());

        holder.mBinding.folderContensSize.setText(String.valueOf(mAdapterUtil
                .getFolderContentsSize(folder, mFolderFolderIdList, mSizeFolderIdList)));

        if (mActionModeState == ACTION_MODE_ON)
            mAdapterUtil.gridActionModeOn(holder.mBinding.folderCheckBox, mSelectedFolderIdHashSet, folder.getId());
        else
            mAdapterUtil.gridActionModeOff(holder.mBinding.folderCheckBox, mSelectedFolderIdHashSet);

        holder.mBinding.folderDragHandle.setVisibility(mSort == SORT_CUSTOM && mActionModeState == ACTION_MODE_ON ? View.VISIBLE : View.GONE);

        holder.itemView.setVisibility(folder.getId() == -1 ? View.INVISIBLE : View.VISIBLE);
    }

    public void onItemMove(int from, int to) {
        //checked
        mAdapterUtil.itemMove(from, to, mFolderList);
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
    public void folderSelected(long id) {
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

        public FolderVH(@NotNull ItemListRecyclerFolderBinding binding, FolderAdapterListener listener) {
            //checked
            super(binding.getRoot());
            this.mBinding = binding;

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

        public void setFolder(Folder folder) {
            this.mFolder = folder;
            mBinding.setFolder(folder);
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
