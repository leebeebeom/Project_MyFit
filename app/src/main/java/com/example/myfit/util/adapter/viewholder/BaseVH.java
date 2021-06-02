package com.example.myfit.util.adapter.viewholder;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.data.tuple.tuple.SizeTuple;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.Set;

import static com.example.myfit.util.ActionModeImpl.sActionMode;

public abstract class BaseVH<T extends BaseTuple, L extends BaseVHListener> extends RecyclerView.ViewHolder {
    public static boolean sDragging = false;
    private final L mListener;
    protected T mTuple;

    @SuppressLint("ClickableViewAccessibility")
    public BaseVH(ViewDataBinding binding, L listener, Set<T> selectedItems) {
        super(binding.getRoot());
        this.mListener = listener;

        setItemViewClickListener(selectedItems);

        itemView.setOnLongClickListener(v -> {
            listener.itemViewLongClick(getLayoutPosition(), mTuple);
            return false;
        });

        if (getDragHandleIcon() != null) {
            getDragHandleIcon().setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !sDragging)
                    dragStart();
                return false;
            });
        }
    }

    public void setTuple(T tuple) {
        this.mTuple = tuple;
        bind(tuple);
    }

    protected abstract void bind(T tuple);

    private void setItemViewClickListener(Set<T> selectedItems) {
        itemView.setOnClickListener(
                v -> {
                    if (sActionMode != null) {
                        if (selectedItems.contains(mTuple))
                            removeItem(selectedItems);
                        else addItem(selectedItems);
                    } else mListener.itemViewClick(mTuple);
                });
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

    public abstract AppCompatImageView getDragHandleIcon();

    private void dragStart() {
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

    public abstract static class BaseSizeVH extends BaseVH<SizeTuple, BaseVHListener.SizeVHListener> {

        public BaseSizeVH(ViewDataBinding binding, BaseVHListener.SizeVHListener listener, Set<SizeTuple> selectedItems) {
            super(binding, listener, selectedItems);

            getFavorite().setOnClickListener(v -> listener.favoriteClick(mTuple));
        }

        public abstract MaterialCheckBox getFavorite();
    }
}
