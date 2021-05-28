package com.example.myfit.util.adapter.viewholder;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.data.model.size.SizeTuple;
import com.example.myfit.data.model.tuple.BaseTuple;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.Set;

import static com.example.myfit.util.actionmodecallback.BaseActionModeCallBack.sActionMode;

public abstract class BaseVH<T extends BaseTuple, L extends BaseVHListener> extends RecyclerView.ViewHolder {
    public static boolean sIsDragging = false;
    private final L mListener;
    private T mTuple;

    @SuppressLint("ClickableViewAccessibility")
    public BaseVH(ViewDataBinding binding, L listener, Set<T> selectedItems) {
        super(binding.getRoot());
        this.mListener = listener;

        setItemViewClickListener(listener, selectedItems);

        itemView.setOnLongClickListener(v -> {
            listener.itemViewLongClick(getLayoutPosition(), mTuple.getParentIndex());
            return false;
        });

        if (getDragHandleIcon() != null) {
            getDragHandleIcon().setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !sIsDragging)
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

    private void setItemViewClickListener(L listener, Set<T> selectedItems) {
        itemView.setOnClickListener(
                v -> {
                    if (sActionMode != null) {
                        if (selectedItems.contains(mTuple))
                            removeItem(selectedItems);
                        else addItem(selectedItems);
                    } else listener.itemViewClick(mTuple);
                });
    }

    private void addItem(Set<T> selectedItemSet) {
        getCheckBox().setChecked(true);
        selectedItemSet.add(mTuple);
    }

    private void removeItem(Set<T> selectedItemSet) {
        getCheckBox().setChecked(false);
        selectedItemSet.remove(mTuple);
    }

    protected T getTuple() {
        return mTuple;
    }

    public abstract MaterialCheckBox getCheckBox();

    public abstract AppCompatImageView getDragHandleIcon();

    private void dragStart() {
        sIsDragging = true;
        mListener.dragStart(this, getTuple().getParentIndex());
        setDraggingView();
    }

    protected abstract void setDraggingView();

    public void dragStop() {
        sIsDragging = false;
        mListener.dragStop(getTuple().getParentIndex());
        setDropView();
    }

    protected abstract void setDropView();

    protected void setItemViewTranslationZ(View itemView, int value) {
        itemView.setTranslationZ(value);
    }

    public abstract MaterialCardView getCardView();

    public abstract static class BaseSizeVH extends BaseVH<SizeTuple, SizeVHListener> {

        public BaseSizeVH(ViewDataBinding binding, SizeVHListener listener, Set<SizeTuple> selectedItems) {
            super(binding, listener, selectedItems);
        }

        public abstract MaterialCheckBox getFavorite();
    }
}
