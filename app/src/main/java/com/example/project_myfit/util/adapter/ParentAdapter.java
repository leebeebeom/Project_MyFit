package com.example.project_myfit.util.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.ParentModel;
import com.example.project_myfit.util.MyFitVariable;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_OFF;
import static com.example.project_myfit.util.MyFitConstant.ACTION_MODE_ON;
import static com.example.project_myfit.util.MyFitConstant.LISTVIEW;
import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;

public abstract class ParentAdapter<T extends ParentModel, VH extends RecyclerView.ViewHolder> extends ListAdapter<T, VH> {
    protected final AdapterUtil mAdapterUtil;
    private List<T> mSelectedItemList;
    protected int mActionModeState, mSort;
    private final HashSet<Long> mSelectedItemIdHashSet;
    private List<Long> mFolderParentIdList, mSizeParentIdList;

    protected ParentAdapter(@NonNull @NotNull DiffUtil.ItemCallback<T> diffCallback, Context context) {
        super(diffCallback);
        this.mSelectedItemIdHashSet = new HashSet<>();
        this.mAdapterUtil = new AdapterUtil(context);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public void setItem(int sort, List<T> list, List<Long> folderParentIdList, List<Long> sizeParentIdList) {
        this.mSort = sort;
        submitList(list);
        this.mFolderParentIdList = folderParentIdList;
        this.mSizeParentIdList = sizeParentIdList;
    }

    public void setItem(int sort, List<T> list){
        this.mSort = sort;
        submitList(list);
    }

    public void setItem(List<Long> folderParentIdList, List<Long> sizeParentIdList) {
        this.mFolderParentIdList = folderParentIdList;
        this.mSizeParentIdList = sizeParentIdList;
    }

    protected void restoreSelectedHashSet() {
        if (mSelectedItemList != null && !mSelectedItemList.isEmpty()) {
            mAdapterUtil.restoreActionMode(mSelectedItemList, mSelectedItemIdHashSet);
            mSelectedItemList = null;
        }
    }

    protected void setContentsSize(@NotNull MaterialTextView tvContentsSize, long id) {
        tvContentsSize.setText(String.valueOf(mAdapterUtil.getContentsSize(id, mFolderParentIdList, mSizeParentIdList)));
    }

    protected void setActionMode(int viewType, @Nullable MaterialCardView cardView, MaterialCheckBox checkBox, long id) {
        if (viewType == LISTVIEW)
            listActionMode(cardView, checkBox, id);
        else gridActionMode(checkBox, id);
    }

    private void listActionMode(MaterialCardView cardView, MaterialCheckBox checkBox, long id) {
        if (mActionModeState == ACTION_MODE_ON)
            mAdapterUtil.listActionModeOn(cardView, checkBox, mSelectedItemIdHashSet, id);
        else if (mActionModeState == ACTION_MODE_OFF) {
            mAdapterUtil.listActionModeOff(cardView, checkBox, mSelectedItemIdHashSet);
            new Handler().postDelayed(() -> mActionModeState = 0, 301);
        }
    }

    private void gridActionMode(MaterialCheckBox checkBox, long id) {
        if (mActionModeState == ACTION_MODE_ON)
            mAdapterUtil.gridActionModeOn(checkBox, mSelectedItemIdHashSet, id);
        else mAdapterUtil.gridActionModeOff(checkBox, mSelectedItemIdHashSet);
    }

    protected void draggingView(@NotNull RecyclerView.ViewHolder viewHolder) {
        MyFitVariable.isDragging = true;
        viewHolder.itemView.setTranslationZ(10);
    }

    protected void dropView(@NotNull RecyclerView.ViewHolder viewHolder) {
        MyFitVariable.isDragging = false;
        viewHolder.itemView.setTranslationZ(0);
    }

    abstract public void itemMove(int from, int to);

    public void itemDrop(RecyclerView.ViewHolder viewHolder) {
        dropView(viewHolder);
    }

    public void setActionModeState(int actionModeState) {
        mActionModeState = actionModeState;
        notifyDataSetChanged();
    }

    public void setSelectedItemList(List<T> selectedItemList) {
        this.mSelectedItemList = selectedItemList;
    }

    public void itemSelected(long id) {
        if (!mSelectedItemIdHashSet.contains(id)) mSelectedItemIdHashSet.add(id);
        else mSelectedItemIdHashSet.remove(id);
    }

    public void selectAll() {
        for (ParentModel item : getCurrentList())
            mSelectedItemIdHashSet.add(item.getId());
        notifyDataSetChanged();
    }

    public void deselectAll() {
        mSelectedItemIdHashSet.clear();
        notifyDataSetChanged();
    }

    protected void dragHandleVisibility(@NotNull ImageView dragHandle) {
        dragHandle.setVisibility(mSort == SORT_CUSTOM && mActionModeState == ACTION_MODE_ON ? View.VISIBLE : View.GONE);
    }

    protected void cbFavoriteClickable(@NotNull MaterialCheckBox cbFavorite){
        cbFavorite.setClickable(mActionModeState != ACTION_MODE_ON);
    }
}
