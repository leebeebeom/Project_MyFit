package com.example.project_myfit.main.list.adapter.folderadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.databinding.ItemFolderGridBinding;
import com.example.project_myfit.main.list.ListViewModel;
import com.example.project_myfit.util.MyFitVariable;
import com.example.project_myfit.util.adapter.ParentAdapter;
import com.example.project_myfit.util.adapter.viewholder.FolderGridVH;
import com.example.project_myfit.util.adapter.viewholder.FolderVHListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.GRIDVIEW;

public class FolderAdapter extends ParentAdapter<Folder, FolderGridVH> {
    private final ListViewModel mModel;
    private final FolderVHListener mListener;
    private List<Folder> mFolderList;

    public FolderAdapter(Context context, ListViewModel model, FolderVHListener listener) {
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
        }, context);
        this.mModel = model;
        this.mListener = listener;
    }

    @Override
    public void setItem(int sort, List<Folder> list, List<Long> folderParentIdList, List<Long> sizeParentIdList) {
        super.setItem(sort, list, folderParentIdList, sizeParentIdList);
        this.mFolderList = list;
    }

    @NonNull
    @NotNull
    @Override
    public FolderGridVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemFolderGridBinding binding = ItemFolderGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FolderGridVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FolderGridVH holder, int position) {
        Folder folder = getItem(position);
        holder.setFolder(folder);
        setContentsSize(holder.getBinding().tvContentsSize, folder.getId());
        if (String.valueOf(holder.getBinding().tvContentsSize.getText()).equals("1"))
            holder.getBinding().tvItems.setText(R.string.item_folder_item);
        else holder.getBinding().tvItems.setText(R.string.item_folder_items);
        dragHandleTouch(holder, folder);

        restoreSelectedHashSet();
        setActionMode(GRIDVIEW, holder.getBinding().cardView, holder.getBinding().cb, folder.getId());
        dragHandleVisibility(holder.getBinding().iconDragHandle);
        holder.itemView.setVisibility(folder.getId() == -1 ? View.GONE : View.VISIBLE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void dragHandleTouch(@NotNull FolderGridVH holder, Folder folder) {
        holder.getBinding().iconDragHandle.setOnTouchListener((v, event) -> {
            if (folder.getId() != -1 && event.getAction() == MotionEvent.ACTION_DOWN && !MyFitVariable.isDragging) {
                mListener.onFolderDragHandleTouch(holder);
                draggingView(holder);
            }
            return false;
        });
    }

    @Override
    protected void draggingView(@NotNull RecyclerView.ViewHolder viewHolder) {
        super.draggingView(viewHolder);
        FolderGridVH folderGridVH = (FolderGridVH) viewHolder;
        folderGridVH.getBinding().layoutContentsSize.setVisibility(View.INVISIBLE);
        folderGridVH.getBinding().tvFolderName.setAlpha(0.5f);
        folderGridVH.getBinding().cb.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void dropView(@NotNull RecyclerView.ViewHolder viewHolder) {
        super.dropView(viewHolder);
        FolderGridVH folderGridVH = (FolderGridVH) viewHolder;
        folderGridVH.getBinding().layoutContentsSize.setVisibility(View.VISIBLE);
        folderGridVH.getBinding().tvFolderName.setAlpha(1);
        folderGridVH.getBinding().cb.setVisibility(View.VISIBLE);
    }

    @Override
    public void itemMove(int from, int to) {
        mAdapterUtil.itemMove(from, to, mFolderList);
        notifyItemMoved(from, to);
    }

    @Override
    public void itemDrop(RecyclerView.ViewHolder viewHolder) {
        super.itemDrop(viewHolder);
        mModel.folderItemDrop(mFolderList);
    }
}
