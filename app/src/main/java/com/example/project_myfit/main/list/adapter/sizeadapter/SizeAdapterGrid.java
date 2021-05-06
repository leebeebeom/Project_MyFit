package com.example.project_myfit.main.list.adapter.sizeadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemSizeGridBinding;
import com.example.project_myfit.main.list.ListViewModel;
import com.example.project_myfit.util.MyFitVariable;
import com.example.project_myfit.util.adapter.ParentAdapter;
import com.example.project_myfit.util.adapter.viewholder.SizeGridVH;
import com.example.project_myfit.util.adapter.viewholder.SizeVHListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.GRIDVIEW;

public class SizeAdapterGrid extends ParentAdapter<Size, SizeGridVH> {
    private final ListViewModel mModel;
    private final SizeVHListener mListener;
    private List<Size> mSizeList;

    public SizeAdapterGrid(Context context, ListViewModel model, SizeVHListener listener) {
        super(new SizeDiffUtil(), context);
        this.mModel = model;
        this.mListener = listener;
    }

    @Override
    public void setItem(int sort, List<Size> list) {
        super.setItem(sort, list);
        this.mSizeList = list;
    }

    @NonNull
    @NotNull
    @Override
    public SizeGridVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemSizeGridBinding binding = ItemSizeGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SizeGridVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SizeGridVH holder, int position) {
        Size size = getItem(position);
        holder.setSize(size);
        setDragHandleTouchListener(holder);

        restoreSelectedHashSet();
        setActionMode(GRIDVIEW, holder.getBinding().cardView, holder.getBinding().cb, size.getId());
        setDragHandleVisibility(holder.getBinding().iconDragHandle);
        setFavoriteClickable(holder.getBinding().cbFavorite);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setDragHandleTouchListener(@NotNull SizeGridVH holder) {
        holder.getBinding().iconDragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !MyFitVariable.isDragging) {
                mListener.onSizeDragHandleTouch(holder);
                setDraggingView(holder);
            }
            return false;
        });
    }

    @Override
    protected void setDraggingView(@NotNull RecyclerView.ViewHolder viewHolder) {
        super.setDraggingView(viewHolder);
        SizeGridVH sizeGridVH = (SizeGridVH) viewHolder;
        sizeGridVH.getBinding().iv.setAlpha(0.5f);
        sizeGridVH.getBinding().layoutContents.setAlpha(0.7f);
        sizeGridVH.getBinding().cb.setAlpha(0.5f);
    }

    @Override
    protected void setDropView(@NotNull RecyclerView.ViewHolder viewHolder) {
        super.setDropView(viewHolder);
        SizeGridVH sizeGridVH = (SizeGridVH) viewHolder;
        sizeGridVH.getBinding().iv.setAlpha(1f);
        sizeGridVH.getBinding().layoutContents.setAlpha(1);
        sizeGridVH.getBinding().cb.setAlpha(0.8f);
    }

    @Override
    public void moveItem(int from, int to) {
        mAdapterUtil.itemMove(from, to, mSizeList);
        notifyItemMoved(from, to);
    }

    @Override
    public void dropItem(RecyclerView.ViewHolder viewHolder) {
        super.dropItem(viewHolder);
        mModel.sizeItemDrop(mSizeList);
    }
}
