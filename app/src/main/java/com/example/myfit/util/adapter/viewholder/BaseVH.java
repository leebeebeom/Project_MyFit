package com.example.myfit.util.adapter.viewholder;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.data.model.tuple.BaseTuple;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.Set;

import static com.example.myfit.util.ActionModeImpl.ACTION_MODE;

public abstract class BaseVH<T extends BaseTuple, L extends BaseVHListener> extends RecyclerView.ViewHolder {
    public static boolean IS_DRAGGING = false;
    private final L listener;
    private T tuple;

    @SuppressLint("ClickableViewAccessibility")
    public BaseVH(ViewDataBinding binding, L listener, Set<Long> selectedIds) {
        super(binding.getRoot());
        this.listener = listener;

        setItemViewClickListener(listener, selectedIds);

        itemView.setOnLongClickListener(v -> {
            listener.itemViewLongClick(getLayoutPosition());
            return false;
        });

        getDragHandleIcon().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !IS_DRAGGING)
                dragStart();
            return false;
        });
    }

    public void setTuple(T tuple) {
        this.tuple = tuple;
        bind(tuple);
    }

    protected abstract void bind(T tuple);

    private void setItemViewClickListener(L listener, Set<Long> selectedItemSet) {
        itemView.setOnClickListener(
                v -> {
                    if (ACTION_MODE != null) {
                        if (selectedItemSet.contains(tuple.getId()))
                            removeItem(selectedItemSet);
                        else addItem(selectedItemSet);
                    } else listener.itemViewClick(tuple);
                });
    }

    private void addItem(Set<Long> selectedItemSet) {
        getCheckBox().setChecked(true);
        selectedItemSet.add(tuple.getId());
    }

    private void removeItem(Set<Long> selectedItemSet) {
        getCheckBox().setChecked(false);
        selectedItemSet.remove(tuple.getId());
    }

    protected T getTuple() {
        return tuple;
    }

    public abstract MaterialCheckBox getCheckBox();

    public abstract AppCompatImageView getDragHandleIcon();

    private void dragStart() {
        IS_DRAGGING = true;
        listener.dragStart(this);
        setDraggingView();
    }

    protected abstract void setDraggingView();

    public void dragStop() {
        IS_DRAGGING = false;
        listener.dragStop();
        setDropView();
    }

    protected abstract void setDropView();

    protected void setItemViewTranslationZ(View itemView, int value) {
        itemView.setTranslationZ(value);
    }

    public abstract MaterialCardView getCardView();
}
