package com.example.myfit.util.adapter.viewholder;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.data.tuple.tuple.SizeTuple;
import com.example.myfit.util.adapter.BaseAdapter;
import com.example.myfit.util.constant.Sort;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.Set;

import lombok.Getter;
import lombok.experimental.Accessors;

import static com.example.myfit.util.ActionModeImpl.sActionMode;

@Accessors(prefix = "m")
public abstract class BaseVH<T extends BaseTuple, L extends BaseVHListener> extends RecyclerView.ViewHolder {
    public static boolean sDragging = false;
    @Getter
    private final L mListener;
    private final Set<T> mSelectedItems;
    @Getter
    protected T mTuple;

    @SuppressLint("ClickableViewAccessibility")
    public BaseVH(ViewDataBinding binding, L listener, Set<T> selectedItems) {
        super(binding.getRoot());
        this.mListener = listener;
        this.mSelectedItems = selectedItems;
    }

    public void setTuple(T tuple) {
        this.mTuple = tuple;
    }

    protected abstract void bind();

    public void itemViewClick() {
        if (sActionMode != null) {
            if (mSelectedItems.contains(mTuple))
                removeItem(mSelectedItems);
            else addItem(mSelectedItems);
        } else mListener.itemViewClick(mTuple);
    }

    private void removeItem(Set<T> selectedItemSet) {
        getCheckBox().setChecked(false);
        selectedItemSet.remove(mTuple);
    }

    private void addItem(Set<T> selectedItemSet) {
        getCheckBox().setChecked(true);
        selectedItemSet.add(mTuple);
    }

    public abstract MaterialCheckBox getCheckBox();

    public void dragStart() {
        sDragging = true;
        mListener.dragStart(this, mTuple);
        setDraggingView();
    }

    protected abstract void setDraggingView();

    public void dragStop() {
        sDragging = false;
        mListener.dragStop(mTuple);
        setDropView();
    }

    protected abstract void setDropView();

    protected void setItemViewTranslationZ(View itemView, int value) {
        itemView.setTranslationZ(value);
    }

    public abstract MaterialCardView getCardView();

    public abstract void setAdapter(BaseAdapter<?, ?, ?> baseAdapter);

    public abstract static class BaseSizeVH extends BaseVH<SizeTuple, BaseVHListener.SizeVHListener> {

        private final BaseVHListener.SizeVHListener mListener;

        public BaseSizeVH(ViewDataBinding binding, BaseVHListener.SizeVHListener listener, Set<SizeTuple> selectedItems) {
            super(binding, listener, selectedItems);
            this.mListener = listener;
        }

        public void favoriteClick() {
            mListener.favoriteClick(mTuple);
        }
    }
}
