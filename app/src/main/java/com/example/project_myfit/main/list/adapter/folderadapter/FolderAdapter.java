package com.example.project_myfit.main.list.adapter.folderadapter;

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

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.databinding.ItemFolderGridBinding;
import com.example.project_myfit.main.list.ListViewModel;
import com.example.project_myfit.util.adapter.AdapterUtil;
import com.example.project_myfit.util.adapter.view_holder.FolderGridVH;
import com.example.project_myfit.util.adapter.view_holder.FolderVHListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;

@SuppressLint("ClickableViewAccessibility")
public class FolderAdapter extends ListAdapter<Folder, FolderGridVH> {
    private final ListViewModel mModel;
    private FolderVHListener mListener;
    private List<Folder> mFolderList, mSelectedFolderList;
    private int mActionModeState, mSort;
    private final HashSet<Long> mSelectedFolderIdHashSet;
    private List<Long> mFolderFolderIdList, mSizeFolderIdList;
    private AdapterUtil mAdapterUtil;
    private boolean mIsDragging;

    public FolderAdapter(ListViewModel model) {
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

    public void setOnFolderAdapterListener(FolderVHListener listener) {
        this.mListener = listener;
    }

    public void submitList(@Nullable @org.jetbrains.annotations.Nullable List<Folder> list, List<Long> folderFolderIdList, List<Long> sizeFolderIdList, int sort) {
        super.submitList(list);
        this.mFolderList = list;
        this.mFolderFolderIdList = folderFolderIdList;
        this.mSizeFolderIdList = sizeFolderIdList;
        this.mSort = sort;
    }

    @NonNull
    @NotNull
    @Override
    public FolderGridVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemFolderGridBinding binding = ItemFolderGridBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new FolderGridVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FolderGridVH holder, int position) {
        if (mAdapterUtil == null) mAdapterUtil = new AdapterUtil(holder.itemView.getContext());

        if (mSelectedFolderList != null && !mSelectedFolderList.isEmpty()) {
            mAdapterUtil.restoreActionMode(mSelectedFolderList, mSelectedFolderIdHashSet);
            mSelectedFolderList = null;
        }

        Folder folder = getItem(holder.getLayoutPosition());
        holder.setFolder(folder);
        holder.getBinding().iconItemFolderGridDragHandle.setOnTouchListener((v, event) -> {
            if (folder.getId() != -1 && event.getAction() == MotionEvent.ACTION_DOWN && !mIsDragging) {
                mIsDragging = true;
                mListener.onFolderDragHandleTouch(holder);
                draggingView(holder);
            }
            return false;
        });
        int contentsSize = mAdapterUtil.getContentsSize(folder.getId(), mFolderFolderIdList, mSizeFolderIdList);
        if (contentsSize == 1)
            holder.getBinding().tvItemFolderGridItems.setText(R.string.item_folder_item);
        else holder.getBinding().tvItemFolderGridItems.setText(R.string.item_folder_items);
        holder.getBinding().tvItemFolderGridContentsSize.setText(String.valueOf(contentsSize));

        if (mActionModeState == ACTION_MODE_ON)
            mAdapterUtil.gridActionModeOn(holder.getBinding().cbItemFolderGrid, mSelectedFolderIdHashSet, folder.getId());
        else
            mAdapterUtil.gridActionModeOff(holder.getBinding().cbItemFolderGrid, mSelectedFolderIdHashSet);

        holder.getBinding().iconItemFolderGridDragHandle.setVisibility(mSort == SORT_CUSTOM && mActionModeState == ACTION_MODE_ON ? View.VISIBLE : View.GONE);

        holder.itemView.setVisibility(folder.getId() == -1 ? View.INVISIBLE : View.VISIBLE);
    }

    private void draggingView(@NotNull FolderGridVH holder) {
        holder.itemView.setTranslationZ(10);
        holder.getBinding().tvItemFolderGridContentsSizeLayout.setVisibility(View.INVISIBLE);
        holder.getBinding().tvItemFolderGridTitle.setAlpha(0.5f);
        holder.getBinding().cbItemFolderGrid.setVisibility(View.INVISIBLE);
    }

    private void dropView(@NotNull FolderGridVH holder) {
        holder.itemView.setTranslationZ(0);
        holder.getBinding().tvItemFolderGridContentsSizeLayout.setVisibility(View.VISIBLE);
        holder.getBinding().tvItemFolderGridTitle.setAlpha(1);
        holder.getBinding().cbItemFolderGrid.setVisibility(View.VISIBLE);
    }

    public void onItemMove(int from, int to) {
        mAdapterUtil.itemMove(from, to, mFolderList);
        notifyItemMoved(from, to);
    }

    public void onItemDrop(@NotNull RecyclerView.ViewHolder viewHolder) {
        mListener.onFolderDragHandleTouch(viewHolder);
        mModel.folderItemDrop(mFolderList);
        dropView((FolderGridVH) viewHolder);
        mIsDragging = false;
    }

    public void setActionModeState(int actionModeState) {
        this.mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void setSelectedFolderList(List<Folder> selectedFolderList) {
        this.mSelectedFolderList = selectedFolderList;
    }

    public void folderSelected(long id) {
        if (!mSelectedFolderIdHashSet.contains(id)) mSelectedFolderIdHashSet.add(id);
        else mSelectedFolderIdHashSet.remove(id);
    }

    public void selectAll() {
        for (Folder f : getCurrentList())
            mSelectedFolderIdHashSet.add(f.getId());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedFolderIdHashSet.clear();
        notifyDataSetChanged();
    }
}
