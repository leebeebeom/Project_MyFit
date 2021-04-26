package com.example.project_myfit.main.list.adapter.sizeadapter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemSizeListBinding;
import com.example.project_myfit.main.list.ListViewModel;
import com.example.project_myfit.util.adapter.AdapterUtil;
import com.example.project_myfit.util.adapter.viewholder.SizeListVH;
import com.example.project_myfit.util.adapter.viewholder.SizeVHListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;

@SuppressLint("ClickableViewAccessibility")
public class SizeAdapterList extends ListAdapter<Size, SizeListVH> {
    private final ListViewModel mModel;
    private final SizeVHListener mListener;
    private List<Size> mSizeList, mSelectedSizeList;
    private int mActionModeState, mSort;
    private final HashSet<Long> mSelectedSizeIdHashSet;
    private AdapterUtil mAdapterUtil;
    boolean mIsDragging;

    public SizeAdapterList(ListViewModel model, SizeVHListener listener) {
        super(new SizeDiffUtil());
        this.mModel = model;
        this.mSelectedSizeIdHashSet = new HashSet<>();
        this.mListener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void submitList(@Nullable @org.jetbrains.annotations.Nullable List<Size> list, int sort) {
        super.submitList(list);
        this.mSizeList = list;
        this.mSort = sort;
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
        if (mAdapterUtil == null) mAdapterUtil = new AdapterUtil(holder.itemView.getContext());

        if (mSelectedSizeList != null && !mSelectedSizeList.isEmpty()) {
            mAdapterUtil.restoreActionMode(mSelectedSizeList, mSelectedSizeIdHashSet);
            mSelectedSizeList = null;
        }

        Size size = getItem(holder.getLayoutPosition());
        holder.setSize(size);
        holder.getBinding().iconItemSizeListDragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !mIsDragging) {
                mIsDragging = true;
                draggingView(holder);
                mListener.onSizeDragHandleTouch(holder);
            }
            return false;
        });

        if (mActionModeState == ACTION_MODE_ON)
            mAdapterUtil.listActionModeOn(holder.getBinding().cardViewItemSizeList, holder.getBinding().cbItemSizeList, mSelectedSizeIdHashSet, size.getId());
        else if (mActionModeState == ACTION_MODE_OFF) {
            mAdapterUtil.listActionModeOff(holder.getBinding().cardViewItemSizeList, holder.getBinding().cbItemSizeList, mSelectedSizeIdHashSet);
            new Handler().postDelayed(() -> mActionModeState = 0, 310);
        }

        holder.getBinding().iconItemSizeListDragHandle.setVisibility(mSort == SORT_CUSTOM && mActionModeState == ACTION_MODE_ON ? View.VISIBLE : View.GONE);
        holder.getBinding().cbItemSizeListFavorite.setVisibility(holder.getBinding().iconItemSizeListDragHandle.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        holder.getBinding().cbItemSizeListFavorite.setClickable(mActionModeState != ACTION_MODE_ON);
    }

    private void draggingView(@NotNull SizeListVH holder) {
        holder.itemView.setTranslationZ(10);
        holder.getBinding().cbItemSizeList.setVisibility(View.INVISIBLE);
        holder.getBinding().tvItemSizeListBrand.setAlpha(0.4f);
        holder.getBinding().tvItemSizeListName.setAlpha(0.5f);
        holder.getBinding().ivItemSizeListPicture.setAlpha(0.5f);
    }

    private void dropView(@NotNull SizeListVH holder) {
        holder.itemView.setTranslationZ(0);
        holder.getBinding().cbItemSizeList.setVisibility(View.VISIBLE);
        holder.getBinding().tvItemSizeListBrand.setAlpha(0.7f);
        holder.getBinding().tvItemSizeListName.setAlpha(0.8f);
        holder.getBinding().ivItemSizeListPicture.setAlpha(1f);
    }

    public void onItemMove(int from, int to) {
        mAdapterUtil.itemMove(from, to, mSizeList);
        notifyItemMoved(from, to);
    }

    public void onItemDrop(@NotNull RecyclerView.ViewHolder viewHolder) {
        mListener.onSizeDragHandleTouch(viewHolder);
        mModel.sizeItemDrop(mSizeList);
        dropView((SizeListVH) viewHolder);
        mIsDragging = false;
    }

    public void setActionModeState(int actionModeState) {
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void setSelectedSizeList(List<Size> selectedSizeList) {
        this.mSelectedSizeList = selectedSizeList;
    }

    public void sizeSelected(long id) {
        if (!mSelectedSizeIdHashSet.contains(id)) mSelectedSizeIdHashSet.add(id);
        else mSelectedSizeIdHashSet.remove(id);
    }

    public void selectAll() {
        for (Size s : getCurrentList())
            mSelectedSizeIdHashSet.add(s.getId());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedSizeIdHashSet.clear();
        notifyDataSetChanged();
    }
}
