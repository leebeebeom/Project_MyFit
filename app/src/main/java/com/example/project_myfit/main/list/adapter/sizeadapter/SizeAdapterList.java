package com.example.project_myfit.main.list.adapter.sizeadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemSizeListBinding;
import com.example.project_myfit.main.list.ListViewModel;
import com.example.project_myfit.util.MyFitVariable;
import com.example.project_myfit.util.adapter.ParentAdapter;
import com.example.project_myfit.util.adapter.viewholder.SizeListVH;
import com.example.project_myfit.util.adapter.viewholder.SizeVHListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.LISTVIEW;

public class SizeAdapterList extends ParentAdapter<Size, SizeListVH> {
    private final ListViewModel mModel;
    private final SizeVHListener mListener;
    private List<Size> mSizeList;

    public SizeAdapterList(Context context, ListViewModel model, SizeVHListener listener) {
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
    public SizeListVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemSizeListBinding binding = ItemSizeListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SizeListVH(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SizeListVH holder, int position) {
        Size size = getItem(position);
        holder.setSize(size);
        setDragHandleTouchListener(holder);

        restoreSelectedHashSet();
        setActionMode(LISTVIEW, holder.getBinding().cardView, holder.getBinding().cb, size.getId());
        setDragHandleVisibility(holder.getBinding().iconDragHandle);
        setFavoriteClickable(holder.getBinding().cbFavorite);
        holder.getBinding().cbFavorite.setVisibility(holder.getBinding().iconDragHandle.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setDragHandleTouchListener(@NotNull SizeListVH holder) {
        holder.getBinding().iconDragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !MyFitVariable.isDragging) {
                setDraggingView(holder);
                mListener.sizeDragHandleTouch(holder);
            }
            return false;
        });
    }

    @Override
    protected void setDraggingView(@NotNull RecyclerView.ViewHolder viewHolder) {
        super.setDraggingView(viewHolder);
        SizeListVH sizeListVH = (SizeListVH) viewHolder;
        sizeListVH.getBinding().cb.setVisibility(View.INVISIBLE);
        sizeListVH.getBinding().layoutTv.setAlpha(0.7f);
        sizeListVH.getBinding().iv.setAlpha(0.5f);
    }

    @Override
    protected void setDropView(@NotNull RecyclerView.ViewHolder viewHolder) {
        super.setDropView(viewHolder);
        SizeListVH sizeListVH = (SizeListVH) viewHolder;
        sizeListVH.getBinding().cb.setVisibility(View.VISIBLE);
        sizeListVH.getBinding().layoutTv.setAlpha(1);
        sizeListVH.getBinding().iv.setAlpha(1f);
    }

    @Override
    public void moveItem(int from, int to) {
        mAdapterUtil.itemMove(from, to, mSizeList);
        notifyItemMoved(from, to);
    }

    @Override
    public void dropItem(RecyclerView.ViewHolder viewHolder) {
        super.dropItem(viewHolder);
        mModel.updateNewOrderSizeList(mSizeList);
    }
}
